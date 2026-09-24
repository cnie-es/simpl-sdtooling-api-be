package eu.europa.ec.simpl.sdtoolingbe.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.data1.common.exception.ValidationException;
import eu.europa.ec.simpl.sdtoolingbe.dto.HashModel;
import eu.europa.ec.simpl.sdtoolingbe.properties.HashProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class HashGeneratorUtilTest {

    @Mock
    private HashProperties hashProperties;

    @InjectMocks
    private HashGeneratorUtil hashGeneratorUtil;

    @Mock
    private HttpURLConnection httpURLConnection;

    @BeforeEach
    void setup() throws IOException {
        when(hashProperties.getHashAlgorithm()).thenReturn("SHA-256");
        lenient().when(hashProperties.getHashModelList()).thenReturn(List.of(new HashModel()));
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpStatus.OK.value());
        hashGeneratorUtil = new HashGeneratorUtil(hashProperties);
    }

    @Test
    void testGenerateHashValueDocumentNotFound() {
        assertThrows(ValidationException.class, () -> hashGeneratorUtil.generateHashValue(null));
    }

    @Test
    void testGenerateHashValueLinkNull() {
        String document = "testDocument";

        when(hashProperties.getValueFromKey(document)).thenReturn(null);

        assertThrows(ValidationException.class, () -> hashGeneratorUtil.generateHashValue(document));
    }

    @Test
    void testGenerateHashValueInvalidURL() {
        String document = "testDocument";

        when(hashProperties.getValueFromKey(document)).thenReturn("invalidUrl");

        assertThrows(ValidationException.class, () -> hashGeneratorUtil.generateHashValue(document));
    }

    @Test
    void testGenerateHashValueUnreachableURL() {
        String document = "testDocument";

        when(hashProperties.getValueFromKey(document)).thenReturn("http://unreachable-url.unreachable");

        assertThrows(UnknownHostException.class, () -> hashGeneratorUtil.generateHashValue(document));
    }

    @Test
    void testGenerateHashValueValidURL() throws IOException {
        String document = "testDocument";
        String testUrl = "http://reachable-url.reachable";

        when(hashProperties.getValueFromKey(document)).thenReturn(testUrl);

        try (MockedConstruction<OkHttpClient> mockedClient =
                Mockito.mockConstruction(OkHttpClient.class, (mock, context) -> {
                    Response mockResponse = Mockito.mock(Response.class);
                    ResponseBody mockBody = Mockito.mock(ResponseBody.class);
                    when(mockResponse.isSuccessful()).thenReturn(true);
                    when(mockResponse.body()).thenReturn(mockBody);
                    when(mockBody.byteStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));

                    Call mockCall = Mockito.mock(Call.class);
                    when(mockCall.execute()).thenReturn(mockResponse);
                    when(mock.newCall(Mockito.any())).thenReturn(mockCall);
                })) {

            String hash = hashGeneratorUtil.generateHashValue(document);
            assertNotNull(hash);
        }
    }
}
