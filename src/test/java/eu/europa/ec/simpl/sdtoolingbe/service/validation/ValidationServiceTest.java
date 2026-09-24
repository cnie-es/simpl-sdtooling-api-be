package eu.europa.ec.simpl.sdtoolingbe.service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.client.validation.ValidationClientBuilder;
import eu.europa.ec.simpl.data1.common.exception.InvalidSDJsonException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressValidationException;
import eu.europa.ec.simpl.data1.common.properties.ValidationProperties;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private ValidationClientBuilder validationClient;

    @Mock
    private ValidationProperties validationProperties;

    @Mock
    private static Request feignRequest;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ValidationServiceImpl validationService;

    @BeforeAll
    static void staticSetUp() {
        feignRequest = Mockito.mock(Request.class);
    }

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl(validationClient, validationProperties, objectMapper);
    }

    @Test
    void testValidateJsonLdWhenValidationDisabled() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(false);

        JsonNode sdJsonLd = objectMapper.readTree("{\"@graph\": []}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));

        verify(validationClient, never()).validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class));
    }

    @Test
    void testValidateJsonLdWhenValidationEnabledWithSuccess() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        String validResponse = "{\"@graph\": []}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(validResponse, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));

        verify(validationClient).validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class));
    }

    @Test
    void testValidateJsonLdWhenValidationEnabledWithFeignException() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        FeignException feignException =
                new FeignException.BadRequest("Bad Request from validation service", feignRequest, null, null);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenThrow(feignException);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertThrows(
                RemoteServiceErrorException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected RemoteServiceErrorException was not thrown on FeignException 400");
    }

    @Test
    void testValidateJsonLdWhenResponseIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(null);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    @Test
    void testValidateJsonLdWhenResponseBodyIsBlank() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("   ", HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    @Test
    void testValidateResourceAddressWhenValidationDisabled() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(false);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertDoesNotThrow(() -> validationService.validateResourceAddress(resourceAddress, "{}"));

        verify(validationClient, never()).validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class));
    }

    @Test
    void testValidateResourceAddressWhenValidationEnabledWithSuccess() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        String validResponse = "{\"valid\": true}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(validResponse, HttpStatus.OK);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertDoesNotThrow(() -> validationService.validateResourceAddress(resourceAddress, "{}"));

        verify(validationClient).validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class));
    }

    @Test
    void testValidateResourceAddressWhenValidationEnabledWithFeignException() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        FeignException feignException =
                new FeignException.BadRequest("Bad Request from validation service", feignRequest, null, null);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenThrow(feignException);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertThrows(
                RemoteServiceErrorException.class,
                () -> validationService.validateResourceAddress(resourceAddress, "{}"),
                "Expected RemoteServiceErrorException was not thrown on FeignException 400");
    }

    @Test
    void testHandleValidationResponseForResourceAddressWhenResponseIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(null);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertDoesNotThrow(() -> validationService.validateResourceAddress(resourceAddress, "{}"));
    }

    @Test
    void testHandleValidationResponseForResourceAddressWhenResponseBodyIsBlank() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("   ", HttpStatus.OK);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertDoesNotThrow(() -> validationService.validateResourceAddress(resourceAddress, "{}"));
    }

    // -------------------------------------------------------------------------
    // processBodyResponseForJsonLd()
    // -------------------------------------------------------------------------

    @Test
    void testProcessBodyResponseForJsonLdWhenGraphNodeIsNotArray() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // @graph is a JSON object, not an array -> graphNode.isArray() == false -> no iteration, no exception
        String responseBody = "{\"@graph\": {\"key\": \"value\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    @Test
    void testProcessBodyResponseForJsonLdWhenGraphNodeIsArrayWithValidationError() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // @graph is an array with one node that carries sh:resultMessage and sh:resultPath -> InvalidSDJsonException
        String responseBody = "{"
                + "\"@graph\": ["
                + "  {"
                + "    \"sh:resultMessage\": \"value is required\","
                + "    \"sh:resultPath\": {\"@id\": \"simpl:myField\"},"
                + "    \"sh:value\": {\"@value\": \"someValue\"}"
                + "  }"
                + "]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertThrows(
                InvalidSDJsonException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected InvalidSDJsonException was not thrown for array @graph with validation error node");
    }

    // -------------------------------------------------------------------------
    // processJsonNodeForJsonLd()
    // -------------------------------------------------------------------------

    @Test
    void testProcessJsonNodeForJsonLdWhenErrorDescriptionIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // node has no sh:resultMessage -> errorDescription == null -> early return, no exception
        String responseBody = "{\"@graph\": [{\"sh:resultPath\": {\"@id\": \"simpl:myField\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    @Test
    void testProcessJsonNodeForJsonLdWhenErrorFieldIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // node has sh:resultMessage but no sh:resultPath -> errorField == null -> early return, no exception
        String responseBody = "{\"@graph\": [{\"sh:resultMessage\": \"some error\"}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    @Test
    void testProcessJsonNodeForJsonLdWhenValueFieldIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // node has sh:resultMessage + sh:resultPath but no sh:value -> valueField == null -> InvalidSDJsonException
        String responseBody = "{\"@graph\": [{"
                + "\"sh:resultMessage\": \"some error\","
                + "\"sh:resultPath\": {\"@id\": \"simpl:myField\"}"
                + "}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertThrows(
                InvalidSDJsonException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected InvalidSDJsonException when valueField is null");
    }

    @Test
    void testProcessJsonNodeForJsonLdWhenValueFieldStartsWithUnderscore() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // valueField starts with "_" -> !valueField.startsWith("_") == false -> else branch -> [field] only
        String responseBody = "{\"@graph\": [{"
                + "\"sh:resultMessage\": \"some error\","
                + "\"sh:resultPath\": {\"@id\": \"simpl:myField\"},"
                + "\"sh:value\": {\"@value\": \"_blankNode\"}"
                + "}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        InvalidSDJsonException ex = assertThrows(
                InvalidSDJsonException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected InvalidSDJsonException when valueField starts with '_'");

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("[myField]") && !ex.getMessage().contains("="),
                "Error message should contain [field] without value");
    }

    @Test
    void testProcessJsonNodeForJsonLdWhenValueFieldDoesNotStartWithUnderscore() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // valueField does not start with "_" -> !valueField.startsWith("_") == true -> if branch -> [field=value]
        String responseBody = "{\"@graph\": [{"
                + "\"sh:resultMessage\": \"some error\","
                + "\"sh:resultPath\": {\"@id\": \"simpl:myField\"},"
                + "\"sh:value\": {\"@value\": \"badValue\"}"
                + "}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        InvalidSDJsonException ex = assertThrows(
                InvalidSDJsonException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected InvalidSDJsonException when valueField does not start with '_'");

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("[myField=badValue]"), "Error message should contain [field=value]");
    }

    // -------------------------------------------------------------------------
    // getErrorField()
    // -------------------------------------------------------------------------

    @Test
    void testGetErrorFieldWhenAtIdIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // sh:resultPath exists but has no @id -> errorFieldNode.get("@id") == null -> errorField == null
        // -> processJsonNodeForJsonLd() returns early without exception
        String responseBody =
                "{\"@graph\": [{" + "\"sh:resultMessage\": \"some error\"," + "\"sh:resultPath\": {}" + "}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        assertDoesNotThrow(() -> validationService.validateJsonLd(sdJsonLd, "schemaContent"));
    }

    // -------------------------------------------------------------------------
    // getNodeValue()
    // -------------------------------------------------------------------------

    @Test
    void testGetNodeValueWhenAtValueIsNull() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // sh:value exists but has no @value -> valueNode.get("@value") == null -> valueField == null
        // -> else branch fires -> InvalidSDJsonException with [field] only
        String responseBody = "{\"@graph\": [{"
                + "\"sh:resultMessage\": \"some error\","
                + "\"sh:resultPath\": {\"@id\": \"simpl:myField\"},"
                + "\"sh:value\": {}"
                + "}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateSelfDescription(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode sdJsonLd = objectMapper.readTree("{\"key\": \"value\"}");

        InvalidSDJsonException ex = assertThrows(
                InvalidSDJsonException.class,
                () -> validationService.validateJsonLd(sdJsonLd, "schemaContent"),
                "Expected InvalidSDJsonException when @value is null inside sh:value");

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("[myField]") && !ex.getMessage().contains("="),
                "Error message should contain [field] without value");
    }

    // -------------------------------------------------------------------------
    // processBodyResponseForResourceAddress()
    // -------------------------------------------------------------------------

    @Test
    void testProcessBodyResponseForResourceAddressWhenValidNodeNotPresent() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // response body has no "valid" field -> !responseJsonNode.has("valid") == true -> early return, no exception
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"other\": \"data\"}", HttpStatus.OK);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        assertDoesNotThrow(() -> validationService.validateResourceAddress(resourceAddress, "{}"));
    }

    @Test
    void testProcessBodyResponseForResourceAddressWhenValidFalseAndErrorsPresent() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // valid == false + errors array present -> ResourceAddressValidationException with errors content
        String responseBody = "{\"valid\": false, \"errors\": [{\"pointer\": \"name\", \"message\": \"required\"}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        ResourceAddressValidationException ex = assertThrows(
                ResourceAddressValidationException.class,
                () -> validationService.validateResourceAddress(resourceAddress, "{}"),
                "Expected ResourceAddressValidationException when valid is false and errors are present");

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("name") && ex.getMessage().contains("required"),
                "Exception message should contain the errors content");
    }

    @Test
    void testProcessBodyResponseForResourceAddressWhenValidFalseAndErrorsNotPresent() throws Exception {
        when(validationProperties.isEnabled()).thenReturn(true);

        // valid == false + no errors field -> ResourceAddressValidationException with "error obj not found"
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"valid\": false}", HttpStatus.OK);
        when(validationClient.validateResourceAddress(any(MultipartFile.class), any(MultipartFile.class)))
                .thenReturn(responseEntity);

        JsonNode resourceAddress = objectMapper.readTree("{\"baseUrl\": \"http://example.com\"}");

        ResourceAddressValidationException ex = assertThrows(
                ResourceAddressValidationException.class,
                () -> validationService.validateResourceAddress(resourceAddress, "{}"),
                "Expected ResourceAddressValidationException when valid is false and no errors field");

        org.junit.jupiter.api.Assertions.assertEquals(
                "error obj not found", ex.getMessage(), "Exception message should be 'error obj not found'");
    }
}
