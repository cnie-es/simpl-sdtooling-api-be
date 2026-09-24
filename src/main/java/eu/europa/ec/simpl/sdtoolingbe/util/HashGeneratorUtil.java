package eu.europa.ec.simpl.sdtoolingbe.util;

import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.exception.ValidationException;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import eu.europa.ec.simpl.sdtoolingbe.properties.HashProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HashGeneratorUtil {

    private static final int BUFFER_SIZE = 8192;
    private final HashProperties hashProperties;
    private final String hashAlg;

    public HashGeneratorUtil(HashProperties hashProperties) {
        this.hashProperties = hashProperties;
        this.hashAlg = hashProperties.getHashAlgorithm();
    }

    public String generateHashValue(String document) throws IOException {
        if (document == null) {
            throw new ValidationException("Document Key Not Found");
        }

        String link = hashProperties.getValueFromKey(document);
        if (link == null) {
            throw new ValidationException("Hash URL Not Valid");
        }

        URL url = validateUrl(link);

        return computeHash(url);
    }

    private static URL validateUrl(String link) {
        try {
            return new URI(link).toURL();
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            throw new ValidationException("URL Value Not Valid", e);
        }
    }

    private String computeHash(URL url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient client = new OkHttpClient();
        MessageDigest digest;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("computeHash(): received response code {} invoking URL {}", response.code(), url);
                throw RemoteServiceUtil.toRemoteServiceErrorException(
                        CommonErrorType.REMOTE_HASH_SERVICE_ERROR,
                        "received error response from target URL '" + url + "'",
                        response.code(),
                        null);
            }
            digest = computeHash(url, response);
        }

        return convertToHex(digest.digest());
    }

    private MessageDigest computeHash(URL url, Response response) throws IOException {
        MessageDigest digest = getMessageDigest();
        try (ResponseBody responseBody = response.body()) {
            if (responseBody == null) {
                log.error("computeHash(): no response body invoking URL {}", url);
                throw new RemoteServiceUnexpectedResponseException(
                        CommonErrorType.REMOTE_HASH_SERVICE_ERROR,
                        "no response body from target URL '" + url + "'",
                        null);
            }

            try (InputStream inputStream = responseBody.byteStream()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    assert digest != null;
                    digest.update(buffer, 0, bytesRead);
                }
            }
        }
        return digest;
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance(hashAlg);
        } catch (NoSuchAlgorithmException e) {
            log.error("getMessageDigest() failed", e);
            throw new RuntimeException("no MessageDigest for algorithm '" + hashAlg + "'", e);
        }
    }

    private String convertToHex(byte[] hashBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        String hashString = sb.toString();
        log.debug("convertToHex(): result is '{}'", hashString);
        return hashString;
    }
}
