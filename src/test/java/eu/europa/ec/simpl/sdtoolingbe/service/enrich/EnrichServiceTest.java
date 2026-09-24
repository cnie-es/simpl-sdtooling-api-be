package eu.europa.ec.simpl.sdtoolingbe.service.enrich;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.exception.InvalidPayloadException;
import eu.europa.ec.simpl.data1.common.service.schemasyncrepo.SchemaSyncRepoService;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.TestSupport;
import eu.europa.ec.simpl.sdtoolingbe.properties.OfferingTypeProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.assetorchestrator.AssetOrchestratorService;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import eu.europa.ec.simpl.sdtoolingbe.service.hash.HashService;
import eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress.ResourceAddressService;
import eu.europa.ec.simpl.sdtoolingbe.service.sd.SDService;
import eu.europa.ec.simpl.sdtoolingbe.service.validation.ValidationService;
import eu.europa.ec.simpl.sdtoolingbe.service.versioning.VersioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrichServiceTest {

    private EnrichServiceImpl enrichService;

    @Mock
    private AssetOrchestratorService assetOrchestratorService;

    @Mock
    private ConnectorAdapterService connectorAdapterService;

    @Mock
    private HashService hashService;

    @Mock
    private ResourceAddressService resourceAddressService;

    @Mock
    private SchemaSyncRepoService schemaSyncRepoService;

    @Mock
    private SDService sdService;

    @Mock
    private ValidationService validationService;

    @Mock
    private VersioningService versioningService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String bearerToken;
    private JsonNode sdJsonLdObj;
    private JsonNode payloadObj;
    private String schemaId;
    private String templateId;

    @Mock
    private OfferingTypeProperties offeringTypeProperties;

    @BeforeEach
    void setUp() throws Exception {
        enrichService = new EnrichServiceImpl(
                assetOrchestratorService,
                connectorAdapterService,
                hashService,
                resourceAddressService,
                schemaSyncRepoService,
                sdService,
                validationService,
                versioningService,
                offeringTypeProperties,
                objectMapper);
        bearerToken = AuthBearerUtil.toBearerString(TestSupport.createValidJwt());
        schemaId = "schemaId";

        // V1 + V2
        sdJsonLdObj = objectMapper.readTree(TestSupport.getResourceAsString("test/sd/data-offering.json", null));
        templateId = "1";

        // V3
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
    void testEnrichAndValidateV1() throws Exception {
        // String sdJsonLd = "{\"key\": \"value\"}";
        // String schemaId = "any";
        // String templateId = "1";

        // JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(offeringTypeProperties.getValueFromKey(any())).thenReturn("data");
        when(connectorAdapterService.registerV1(nullable(String.class), any(), any()))
                .thenReturn(sdJsonLdObj);
        doNothing().when(validationService).validateJsonLd(any(), any());

        JsonNode result = enrichService.enrichAndValidateV1(bearerToken, sdJsonLdObj, schemaId, templateId);
        assertNotNull(result);
    }

    @Test
    void testEnrichAndValidateV2() throws Exception {
        when(connectorAdapterService.registerV1(nullable(String.class), any(), any()))
                .thenReturn(sdJsonLdObj);
        doNothing().when(validationService).validateJsonLd(any(), any());

        JsonNode result = enrichService.enrichAndValidateV2(bearerToken, sdJsonLdObj, schemaId, templateId);

        assertNotNull(result);
        verify(sdService).setOfferingType(any(), any());
        verify(sdService).setSharingMethodId(any(), any());
        verify(sdService).setParticipantId(any(), any());
        verify(sdService).removeProviderDataAddress(any());
        verify(hashService).generateHashFromJsonLd(any());
        verify(connectorAdapterService).registerV1(nullable(String.class), any(), any());
        verify(sdService).setMetadata(any(), any());
        verify(versioningService).nextVersion(any());
        verify(validationService).validateJsonLd(any(), any());
    }

    @Test
    void testEnrichAndValidateV3() throws Exception {
        when(connectorAdapterService.registerV2(nullable(String.class), any(), any()))
                .thenReturn(sdJsonLdObj);
        doNothing().when(validationService).validateJsonLd(any(), any());

        JsonNode result = enrichService.enrichAndValidateV3(bearerToken, payloadObj, schemaId);

        assertNotNull(result);
        verify(sdService).setOfferingType(any(), any());
        verify(sdService).setSharingMethodId(any(), any());
        verify(sdService).setParticipantId(any(), any());
        verify(sdService).removeProviderDataAddress(any());
        verify(hashService).generateHashFromJsonLd(any());
        verify(connectorAdapterService).registerV2(nullable(String.class), any(), any());
        verify(sdService).setMetadata(any(), any());
        verify(versioningService).nextVersion(any());
        verify(validationService).validateJsonLd(any(), any());
    }

    @Test
    void testEnrichAndValidateV3WithMissingSdJson() throws Exception {
        ((ObjectNode) payloadObj).remove(CommonConstants.Payload.SD_FIELD_NAME);

        when(schemaSyncRepoService.getSchemaMetadata(any())).thenReturn(null);
        when(resourceAddressService.getSourceAddressSchema(any())).thenReturn(null);
        when(schemaSyncRepoService.getSchemaContent(any())).thenReturn(null);

        assertThrows(
                InvalidPayloadException.class,
                () -> enrichService.enrichAndValidateV3(bearerToken, payloadObj, schemaId));
    }

    @Test
    void testEnrichAndValidateV3WithNullSdJson() throws Exception {
        ((ObjectNode) payloadObj).set(CommonConstants.Payload.SD_FIELD_NAME, null);

        when(schemaSyncRepoService.getSchemaMetadata(any())).thenReturn(null);
        when(resourceAddressService.getSourceAddressSchema(any())).thenReturn(null);
        when(schemaSyncRepoService.getSchemaContent(any())).thenReturn(null);

        assertThrows(
                InvalidPayloadException.class,
                () -> enrichService.enrichAndValidateV3(bearerToken, payloadObj, schemaId));
    }

    @Test
    void testEnrichAndValidateV3WithNullEmptySdJson() throws Exception {
        ((ObjectNode) payloadObj).set(CommonConstants.Payload.SD_FIELD_NAME, objectMapper.readTree("{}"));

        when(schemaSyncRepoService.getSchemaMetadata(any())).thenReturn(null);
        when(resourceAddressService.getSourceAddressSchema(any())).thenReturn(null);
        when(schemaSyncRepoService.getSchemaContent(any())).thenReturn(null);

        assertThrows(
                InvalidPayloadException.class,
                () -> enrichService.enrichAndValidateV3(bearerToken, payloadObj, schemaId));
    }

    @Test
    void testEnrichAndValidateV3WithNotObjectValue() throws Exception {
        ((ObjectNode) payloadObj).set(CommonConstants.Payload.SD_FIELD_NAME, objectMapper.readTree("\"value\""));

        when(schemaSyncRepoService.getSchemaMetadata(any())).thenReturn(null);
        when(resourceAddressService.getSourceAddressSchema(any())).thenReturn(null);
        when(schemaSyncRepoService.getSchemaContent(any())).thenReturn(null);

        assertThrows(
                InvalidPayloadException.class,
                () -> enrichService.enrichAndValidateV3(bearerToken, payloadObj, schemaId));
    }
}
