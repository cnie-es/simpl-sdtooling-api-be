package eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.exception.ResourceNotFoundException;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.TestSupport;
import eu.europa.ec.simpl.sdtoolingbe.client.connectoradapter.ConnectorAdapterClient;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ConnectorAdapterServiceTest {

    private static final String DATA_OFFERING_JSON_FILE = "test/sd/data-offering.json";
    private static final String BEARER_TOKEN = AuthBearerUtil.toBearerString("testToken");
    private static final SchemaMetadata SCHEMA_METADATA = SchemaMetadata.builder()
            .resourceType("data")
            .name("test-schema")
            .version("1.0.0")
            .build();

    private ConnectorAdapterService connectorAdapterService;

    @Mock
    private ConnectorAdapterClient connectorAdapterClient;

    @Mock
    private Validator validator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode payloadObj;

    @BeforeEach
    void setUpEach() throws JsonProcessingException {
        connectorAdapterService = new ConnectorAdapterServiceImpl(connectorAdapterClient, objectMapper, validator);

        payloadObj = objectMapper.readTree(
                """
        	{
        		"sdJson": {"key": "value"},
        		"properties": {
        			"resourceAddress": {
        				"templateId" : "5",
        				"value" : "{\\"type\\":\\"MinioS3\\", \\"endpoint\\":\\"https://minio01.integrated.simpl-europe.eu\\", \\"bucketName\\":\\"provider-bucket\\", \\"objectName\\":\\"example-s3.txt\\"}"
        			}
        		}
        	}
        	""");
    }

    @Test
    void testRegisterV1WithSuccess() throws IOException {
        String sdJsonLd = TestSupport.getResourceAsString(DATA_OFFERING_JSON_FILE, null);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        String responseBody = "{\"@id\":\"f31452f6-2d41-4edd-bf1f-e06329c244d9\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(connectorAdapterClient.registerV1(any(), any(), any(OfferType.class)))
                .thenReturn(responseEntity);

        JsonNode result = connectorAdapterService.registerV1(BEARER_TOKEN, sdJsonLdObj, SCHEMA_METADATA);
        assertNotNull(result);
    }

    @Test
    void testRegisterV1WithRemoteServiceErrorException() throws IOException {

        when(connectorAdapterClient.registerV1(any(), any(), any(OfferType.class)))
                .thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> connectorAdapterService.registerV1(BEARER_TOKEN, payloadObj, SCHEMA_METADATA));
    }

    @Test
    void testRegisterV1WithRemoteServiceUnexpectedResponseException() throws IOException {

        // an invalid JSON body causes objectMapper.readTree() to throw JsonProcessingException
        ResponseEntity<String> responseEntity = new ResponseEntity<>("not valid json {{{", HttpStatus.OK);
        when(connectorAdapterClient.registerV1(any(), any(), any(OfferType.class)))
                .thenReturn(responseEntity);

        assertThrows(
                RemoteServiceUnexpectedResponseException.class,
                () -> connectorAdapterService.registerV1(BEARER_TOKEN, payloadObj, SCHEMA_METADATA));
    }

    @Test
    void testRegisterV2WithSuccess() throws IOException {

        String responseBody = "{\"@id\":\"f31452f6-2d41-4edd-bf1f-e06329c244d9\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(connectorAdapterClient.registerV2(any(), any(), any(OfferType.class)))
                .thenReturn(responseEntity);

        JsonNode result = connectorAdapterService.registerV2(BEARER_TOKEN, payloadObj, SCHEMA_METADATA);
        assertNotNull(result);
    }

    @Test
    void testRegisterV2WithRemoteServiceErrorException() throws IOException {
        String sdJsonLd = TestSupport.getResourceAsString(DATA_OFFERING_JSON_FILE, null);
        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(connectorAdapterClient.registerV2(any(), any(), any(OfferType.class)))
                .thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> connectorAdapterService.registerV2(BEARER_TOKEN, sdJsonLdObj, SCHEMA_METADATA));
    }

    @Test
    void testRegisterV2WithRemoteServiceUnexpectedResponseException() throws IOException {
        String sdJsonLd = TestSupport.getResourceAsString(DATA_OFFERING_JSON_FILE, null);
        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        // an invalid JSON body causes objectMapper.readTree() to throw JsonProcessingException
        ResponseEntity<String> responseEntity = new ResponseEntity<>("not valid json {{{", HttpStatus.OK);
        when(connectorAdapterClient.registerV2(any(), any(), any(OfferType.class)))
                .thenReturn(responseEntity);

        assertThrows(
                RemoteServiceUnexpectedResponseException.class,
                () -> connectorAdapterService.registerV2(BEARER_TOKEN, sdJsonLdObj, SCHEMA_METADATA));
    }

    @Test
    void testGetParticipantWithSuccess() throws IOException {
        Participant responseBody =
                Participant.builder().id("participant-id-0001").build();
        ResponseEntity<Participant> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(connectorAdapterClient.getParticipant(any())).thenReturn(responseEntity);

        Participant result = connectorAdapterService.getParticipant(BEARER_TOKEN);
        assertEquals(responseBody, result);
    }

    @Test
    void testGetParticipantWithRemoteServiceErrorException() throws IOException {
        when(connectorAdapterClient.getParticipant(any())).thenThrow(FeignException.Unauthorized.class);

        assertThrows(RemoteServiceErrorException.class, () -> connectorAdapterService.getParticipant(BEARER_TOKEN));
    }

    @Test
    void testGetResourceAddressWithSuccess() {
        String assetId = "asset-001";
        ResourceAddress resourceAddress = ResourceAddress.builder()
                .templateId("5")
                .value("{\"type\":\"MinioS3\",\"endpoint\":\"https://minio01.integrated.simpl-europe.eu\"}")
                .build();
        ResponseEntity<ResourceAddress> responseEntity = new ResponseEntity<>(resourceAddress, HttpStatus.OK);

        when(connectorAdapterClient.getResourceAddress(any(), any())).thenReturn(responseEntity);

        ResourceAddress result = connectorAdapterService.getResourceAddress(BEARER_TOKEN, assetId);

        assertNotNull(result);
        assertEquals(resourceAddress, result);
    }

    @Test
    void testGetResourceAddressWithResourceNotFoundException() {
        String assetId = "asset-not-found";

        when(connectorAdapterClient.getResourceAddress(any(), any())).thenThrow(FeignException.NotFound.class);

        assertThrows(
                ResourceNotFoundException.class,
                () -> connectorAdapterService.getResourceAddress(BEARER_TOKEN, assetId));
    }

    @Test
    void testGetResourceAddressWithRemoteServiceErrorException() {
        String assetId = "asset-001";

        when(connectorAdapterClient.getResourceAddress(any(), any())).thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> connectorAdapterService.getResourceAddress(BEARER_TOKEN, assetId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetResourceAddressWithRemoteServiceUnexpectedResponseExceptionWhenViolationsArePresent() {
        String assetId = "asset-001";
        ResourceAddress resourceAddress = ResourceAddress.builder()
                .templateId("5")
                .value("{\"type\":\"MinioS3\"}")
                .build();
        ResponseEntity<ResourceAddress> responseEntity = new ResponseEntity<>(resourceAddress, HttpStatus.OK);

        ConstraintViolation<ResourceAddress> violation = mock(ConstraintViolation.class);

        when(connectorAdapterClient.getResourceAddress(any(), any())).thenReturn(responseEntity);
        when(validator.validate(any(ResourceAddress.class))).thenReturn(Set.of(violation));

        assertThrows(
                RemoteServiceUnexpectedResponseException.class,
                () -> connectorAdapterService.getResourceAddress(BEARER_TOKEN, assetId));
    }
}
