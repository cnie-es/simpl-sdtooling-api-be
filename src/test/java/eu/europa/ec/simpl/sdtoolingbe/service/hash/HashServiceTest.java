package eu.europa.ec.simpl.sdtoolingbe.service.hash;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.properties.ValidationProperties;
import eu.europa.ec.simpl.sdtoolingbe.TestSupport;
import eu.europa.ec.simpl.sdtoolingbe.dto.HashModel;
import eu.europa.ec.simpl.sdtoolingbe.properties.HashProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.validation.ValidationServiceImpl;
import eu.europa.ec.simpl.sdtoolingbe.util.HashGeneratorUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private ValidationProperties validationProperties;

    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private HashProperties hashProperties;

    @Mock
    private HashGeneratorUtil hashGeneratorUtil;

    private HashService hashService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        when(hashProperties.getHashAlgorithm()).thenReturn("SHA-256");

        HashModel hashModel = new HashModel();
        hashModel.setHashObj("contractTemplate");
        hashModel.setHashDoc("contractTemplateDocument");
        hashModel.setHashAlg("contractTemplateHashAlg");
        hashModel.setHashValue("contractTemplateHashValue");
        hashModel.setHashUrl("contractTemplateURL");
        when(hashProperties.getHashModelList()).thenReturn(List.of(hashModel));

        lenient().when(validationProperties.isEnabled()).thenReturn(false);

        hashService = new HashServiceImpl(hashProperties, hashGeneratorUtil);
    }

    @Test
    void testGenerateHashFromJsonLdWithValidJsonLd() throws Exception {
        String sdJsonLd = TestSupport.getResourceAsString("test/sd/infra-offering.json", null);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(hashGeneratorUtil.generateHashValue(any())).thenReturn("hash-value-test");

        hashService.generateHashFromJsonLd(sdJsonLdObj);

        assertTrue(sdJsonLdObj.toString().contains("\"simpl:generalServiceProperties\""));
    }

    @Test
    void testGenerateHashFromJsonLdWithMissingHashObject() throws Exception {
        String sdJsonLd = "{ \"example:someOtherObj\": {} }";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        hashService.generateHashFromJsonLd(sdJsonLdObj);
        String sdJsonLdString = sdJsonLdObj.toString();
        assertFalse(sdJsonLdString.contains("example:hashAlg"));
        assertFalse(sdJsonLdString.contains("example:hashValue"));
    }
}
