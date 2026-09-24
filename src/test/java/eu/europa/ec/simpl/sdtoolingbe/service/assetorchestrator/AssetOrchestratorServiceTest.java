package eu.europa.ec.simpl.sdtoolingbe.service.assetorchestrator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.sdtoolingbe.client.assetorchestrator.AssetOrchestratorClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AssetOrchestratorServiceTest {

    private static final String BEARER_TOKEN = "testToken";

    private static final String ASSET_ID = "asset-001";
    private static final String ASSET_DESCRIPTION = "description for asset id 001";
    private static final String PROVIDER_CONTACT = "provider@test.com";

    /**
     * SD JSON-LD with all fields required by enrichResourceAddress():
     *   - simpl:edcRegistration / simpl:assetId          (ASSET_ID_PATH)
     *   - simpl:generalServiceProperties / simpl:description (DESCRIPTION_PATH)
     *   - simpl:providerInformation / simpl:contact      (CONTACT_PATH)
     */
    private final String SD_JSON_LD = String.format(
            """
            {
              "simpl:edcRegistration": {
                "simpl:assetId": "%s"
              },
              "simpl:generalServiceProperties": {
                "simpl:description": "%s"
              },
              "simpl:providerInformation": {
                "simpl:contact": "%s"
              }
            }
            """,
            ASSET_ID, ASSET_DESCRIPTION, PROVIDER_CONTACT);

    /**
     * resourceAddress with type=ProcessingWorkflow -> isRegistrable() == true.
     */
    private static final String RESOURCE_ADDRESS_REGISTRABLE =
            """
            {
              "type": "ProcessingWorkflow",
              "endpoint": "https://workflow.example.com"
            }
            """;

    /**
     * resourceAddress with type != "ProcessingWorkflow" -> isRegistrable() == false.
     */
    private static final String RESOURCE_ADDRESS_NOT_REGISTRABLE =
            """
            {
              "type": "MinioS3",
              "endpoint": "https://minio.example.com"
            }
            """;

    @Mock
    private AssetOrchestratorClient assetOrchestratorClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AssetOrchestratorServiceImpl assetOrchestratorService;

    private JsonNode sdJsonLd;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        assetOrchestratorService = new AssetOrchestratorServiceImpl(assetOrchestratorClient);
        sdJsonLd = objectMapper.readTree(SD_JSON_LD);
    }

    @Test
    void registerWorkflowWhenNotRegistrable() throws JsonProcessingException {
        JsonNode resourceAddress = objectMapper.readTree(RESOURCE_ADDRESS_NOT_REGISTRABLE);

        assertDoesNotThrow(
                () -> assetOrchestratorService.registerWorkflow(sdJsonLd, (ObjectNode) resourceAddress, BEARER_TOKEN));

        verify(assetOrchestratorClient, never()).registerV1(any(), any());
    }

    @Test
    void registerWorkflowWhenRegistrableWithSuccess() throws JsonProcessingException {
        JsonNode resourceAddress = objectMapper.readTree(RESOURCE_ADDRESS_REGISTRABLE);

        ResponseEntity<String> successResponse = new ResponseEntity<>("{\"id\":\"wf-001\"}", HttpStatus.OK);
        ArgumentCaptor<String> enrichedResourceAddressCaptor = ArgumentCaptor.forClass(String.class);
        when(assetOrchestratorClient.registerV1(any(), enrichedResourceAddressCaptor.capture()))
                .thenReturn(successResponse);

        assertDoesNotThrow(
                () -> assetOrchestratorService.registerWorkflow(sdJsonLd, (ObjectNode) resourceAddress, BEARER_TOKEN));

        verify(assetOrchestratorClient, times(1)).registerV1(any(), any());

        JsonNode enrichedResourceAddress = objectMapper.readTree(enrichedResourceAddressCaptor.getValue());
        assertEquals(ASSET_ID, enrichedResourceAddress.get("assetId").asText());
        assertEquals(
                ASSET_DESCRIPTION,
                enrichedResourceAddress.get("assetDescription").asText());
        assertEquals(
                PROVIDER_CONTACT, enrichedResourceAddress.get("providerEmail").asText());
    }

    @Test
    void registerWorkflowWhenRegistrableWithFeignException() throws Exception {

        ObjectNode resourceAddress = (ObjectNode) objectMapper.readTree(RESOURCE_ADDRESS_REGISTRABLE);

        FeignException feignException500 = new FeignException.InternalServerError(
                "Internal Server Error",
                Request.create(
                        Request.HttpMethod.POST,
                        AssetOrchestratorClient.WORKFLOW_DEFINITIONS_PATH_V1,
                        Collections.emptyMap(),
                        null,
                        new RequestTemplate()),
                null,
                Collections.emptyMap());

        when(assetOrchestratorClient.registerV1(any(), any())).thenThrow(feignException500);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> assetOrchestratorService.registerWorkflow(sdJsonLd, resourceAddress, BEARER_TOKEN));

        verify(assetOrchestratorClient, times(1)).registerV1(any(), any());
    }
}
