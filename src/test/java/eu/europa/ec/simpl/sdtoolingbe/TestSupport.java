package eu.europa.ec.simpl.sdtoolingbe;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.json.JSONObject;

public final class TestSupport {

    private static final int PWD_LEN = 32;

    private TestSupport() {}

    /**
     *
     * @param name
     * @param charset (optional: null) default StandardCharsets.UTF_8
     * @return
     * @throws IOException
     */
    public static String getResourceAsString(String name, Charset charset) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream contentStream = classloader.getResourceAsStream(name)) {
            if (contentStream == null) {
                throw new IllegalArgumentException("Resource not found: " + name);
            }
            return new String(contentStream.readAllBytes(), charset != null ? charset : StandardCharsets.UTF_8);
        }
    }

    public static InputStream getResourceAsStream(String name) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(name);
    }

    public static String generateSecurePassword() {
        return RandomStringUtils.random(PWD_LEN, 0, 0, true, true, null, new SecureRandom());
    }

    public static String createValidJwt() {
        return createValidJwt(generateSecurePassword());
    }

    public static String createNoCredentialsJwt() {
        return createNoCredentialsJwt(generateSecurePassword());
    }

    public static String createValidJwt(String signPassword) {
        return JWT.create()
                .withClaim("exp", 1730819669L)
                .withClaim("iat", 1730819369L)
                .withClaim("auth_time", 1730819368L)
                .withClaim("jti", "cdd742f4-e245-4e78-9a20-b5f5d6544bb7")
                .withClaim("iss", "https://tier1-gateway.gaiax-edc-dev-sd.dev.simpl-europe.eu/auth/realms/participant")
                .withArrayClaim("aud", new String[] {"realm-management", "account"})
                .withClaim("sub", "4ac46130-3532-4843-9556-9ceb9ec20136")
                .withClaim("typ", "Bearer")
                .withClaim("azp", "frontend-cli")
                .withClaim("sid", "bb07f4d2-aa63-4929-93ad-4c263d85c2cf")
                .withClaim("acr", "1")
                .withArrayClaim("allowed-origins", new String[] {"*"})
                .withClaim(
                        "resource_access",
                        Map.of(
                                "realm-management",
                                        Map.of(
                                                "roles",
                                                List.of("view-realm", "view-users", "query-groups", "query-users")),
                                "frontend-cli", Map.of("roles", List.of("T1UAR_M", "ONBOARDER_M")),
                                "account",
                                        Map.of(
                                                "roles",
                                                List.of("manage-account", "manage-account-links", "view-profile"))))
                .withClaim("scope", "dsAttributes email profile")
                .withClaim("email_verified", true)
                .withClaim("participant_id", "0192e220-9c85-7068-b21a-122bdd34ebd7")
                .withClaim("name", "Alexander Williams")
                .withClaim("preferred_username", "a.w")
                .withClaim("given_name", "Alexander")
                .withClaim("family_name", "Williams")
                .withArrayClaim("client-roles", new String[] {"T1UAR_M", "ONBOARDER_M"})
                .withClaim("identity_attributes", List.of())
                .withClaim("email", "a.w@email.com")
                .withClaim(
                        "credential_id",
                        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEOyq5p2RG2IcSgXqVNyWPrTJ7rtwzf5he5BvI/BZX2KaGvv3NoFnBMGCbNDvaRz+Cwu3xCHwrq2WtuZaX/zVgow==")
                .sign(Algorithm.HMAC256(signPassword));
    }

    public static String createNoCredentialsJwt(String signPassword) {
        return JWT.create()
                .withClaim("exp", 1730819669L)
                .withClaim("iat", 1730819369L)
                .withClaim("auth_time", 1730819368L)
                .withClaim("jti", "cdd742f4-e245-4e78-9a20-b5f5d6544bb7")
                .withClaim("iss", "https://tier1-gateway.gaiax-edc-dev-sd.dev.simpl-europe.eu/auth/realms/participant")
                .withArrayClaim("aud", new String[] {"realm-management", "account"})
                .withClaim("sub", "4ac46130-3532-4843-9556-9ceb9ec20136")
                .withClaim("typ", "Bearer")
                .withClaim("azp", "frontend-cli")
                .withClaim("sid", "bb07f4d2-aa63-4929-93ad-4c263d85c2cf")
                .withClaim("acr", "1")
                .withArrayClaim("allowed-origins", new String[] {"*"})
                .withClaim(
                        "resource_access",
                        Map.of(
                                "realm-management",
                                        Map.of(
                                                "roles",
                                                List.of("view-realm", "view-users", "query-groups", "query-users")),
                                "frontend-cli", Map.of("roles", List.of("T1UAR_M", "ONBOARDER_M")),
                                "account",
                                        Map.of(
                                                "roles",
                                                List.of("manage-account", "manage-account-links", "view-profile"))))
                .withClaim("scope", "dsAttributes email profile")
                .withClaim("email_verified", true)
                .withClaim("participant_id", "0192e220-9c85-7068-b21a-122bdd34ebd7")
                .withClaim("name", "Alexander Williams")
                .withClaim("preferred_username", "a.w")
                .withClaim("given_name", "Alexander")
                .withClaim("family_name", "Williams")
                .withArrayClaim("client-roles", new String[] {"T1UAR_M", "ONBOARDER_M"})
                .withClaim("identity_attributes", List.of())
                .withClaim("email", "a.w@email.com")
                .withClaim(
                        "credential_id",
                        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEOyq5p2RG2IcSgXqVNyWPrTJ7rtwzf5he5BvI/BZX2KaGvv3NoFnBMGCbNDvaRz+Cwu3xCHwrq2WtuZaX/zVgow==")
                .sign(Algorithm.HMAC256(signPassword));
    }

    public static KeyPair generateEcKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        return keyGen.generateKeyPair();
    }

    public static JSONObject generateEcKeyPairAsJson() throws Exception {
        return toJson(generateEcKeyPair());
    }

    public static JSONObject toJson(KeyPair keyPair) throws Exception {
        String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        String privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());

        JSONObject json = new JSONObject();
        json.put("publicKey", publicKey);
        json.put("privateKey", privateKey);
        return json;
    }

    public static String convertToPem(PrivateKey privateKey) throws Exception {
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(privateKey);
        }
        return stringWriter.toString();
    }

    public static String generateCertificatePem(KeyPair keyPair) throws Exception {
        long now = System.currentTimeMillis();
        Date notBefore = new Date(now);
        Date notAfter = new Date(now + 365L * 24 * 60 * 60 * 1000); // 1 anno

        X500Name dnName = new X500Name("CN=Test Certificate");
        BigInteger certSerialNumber = BigInteger.valueOf(now);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerialNumber, notBefore, notAfter, dnName, keyPair.getPublic());

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withECDSA").build(keyPair.getPrivate());

        return toPem(
                new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(contentSigner)));
    }

    public static String toPem(Object obj) throws Exception {
        StringWriter strWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(strWriter)) {
            pemWriter.writeObject(obj);
        }
        return strWriter.toString();
    }
}
