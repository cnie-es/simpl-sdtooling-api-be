package eu.europa.ec.simpl.sdtoolingbe.service.sd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnauthorizedException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.SDUtil;
import eu.europa.ec.simpl.sdtoolingbe.TestSupport;
import eu.europa.ec.simpl.sdtoolingbe.client.authenticationprovider.AuthenticationProviderClient;
import eu.europa.ec.simpl.sdtoolingbe.constant.Constants;
import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import eu.europa.ec.simpl.sdtoolingbe.properties.ResourceAddressProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider.AuthenticationProviderService;
import eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider.AuthenticationProviderServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SDServiceTest {

    private SDServiceImpl sdService;

    @Mock
    private ResourceAddressProperties resourceAddressProperties;

    @Mock
    private AuthenticationProviderClient authenticationProviderClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String bearerToken;

    @BeforeEach
    void setUp() {
        AuthenticationProviderService authenticationProviderService =
                new AuthenticationProviderServiceImpl(authenticationProviderClient);
        sdService = new SDServiceImpl(resourceAddressProperties, authenticationProviderService);
        bearerToken = AuthBearerUtil.toBearerString(TestSupport.createValidJwt());
    }

    @Test
    void testSetOfferingTypeWithOfferingTypeValue() throws Exception {
        String expectedOfferingType = "application";
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        SchemaMetadata schemaMetadata =
                SchemaMetadata.builder().resourceType(expectedOfferingType).build();

        sdService.setOfferingType(sdJsonLdObj, schemaMetadata);

        assertTrue(sdJsonLdObj.has(SDUtil.NS + "generalServiceProperties"));
        JsonNode parent = sdJsonLdObj.get(SDUtil.NS + "generalServiceProperties");
        assertEquals(
                expectedOfferingType, parent.get(SDUtil.NS + "offeringType").asText());
    }

    @Test
    void testSetOfferingTypeWithParentFieldMissing() throws Exception {
        String sdJsonLd = createSdJson("unknown");

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        SchemaMetadata schemaMetadata =
                SchemaMetadata.builder().resourceType("application").build();

        sdService.setOfferingType(sdJsonLdObj, schemaMetadata);

        assertFalse(sdJsonLdObj.has(SDUtil.NS + "generalServiceProperties"));
    }

    @Test
    void testSetSharingMethodIdSuccess() throws Exception {
        String sourceAddressSchema = "tpl1";
        String sharingMethodId = "smid1";
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(resourceAddressProperties.getSharingMethodId(sourceAddressSchema)).thenReturn(sharingMethodId);

        sdService.setSharingMethodId(sdJsonLdObj, sourceAddressSchema);

        assertTrue(sdJsonLdObj.has(SDUtil.NS + "generalServiceProperties"));
        assertEquals(
                sharingMethodId,
                sdJsonLdObj
                        .get(SDUtil.NS + "generalServiceProperties")
                        .get(SDUtil.NS + "sharingMethodId")
                        .textValue());
    }

    @Test
    void testSetSharingMethodIdThrowsWhenMissing() throws Exception {
        String templateId = "tpl1";
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(resourceAddressProperties.getSharingMethodId(templateId)).thenReturn("");

        assertThrows(
                ResourceAddressNotFoundException.class, () -> sdService.setSharingMethodId(sdJsonLdObj, templateId));
    }

    @Test
    void testSetSharingMethodIdWithParentFieldMissing() throws Exception {
        String sdJsonLd = "{\"ex:other\": {}}";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        sdService.setSharingMethodId(sdJsonLdObj, "tpl1");

        assertFalse(sdJsonLdObj.has("gx:generalServiceProperties")
                && sdJsonLdObj.get("gx:generalServiceProperties").has("gx:sharingMethodId"));
    }

    @Test
    void testSetParticipantIdSuccess() throws Exception {
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        Participant participant = Participant.builder().id("participant-id-123").build();

        when(authenticationProviderClient.getParticipant(any())).thenReturn(participant);

        sdService.setParticipantId(sdJsonLdObj, bearerToken);

        assertTrue(sdJsonLdObj.has(SDUtil.NS + "providerInformation"));
        assertEquals(
                participant.getId(),
                sdJsonLdObj
                        .get(SDUtil.NS + "providerInformation")
                        .get(SDUtil.NS + "providedBy")
                        .asText());
    }

    @Test
    void testSetParticipantIdCredentialsNull() throws Exception {
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        Participant participant = Participant.builder().id(null).build();

        when(authenticationProviderClient.getParticipant(any())).thenReturn(participant);

        assertThrows(
                RemoteServiceUnexpectedResponseException.class,
                () -> sdService.setParticipantId(sdJsonLdObj, bearerToken));
    }

    @Test
    void testSetParticipantIdUnauthorizedException() throws Exception {
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(authenticationProviderClient.getParticipant(any())).thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceUnauthorizedException.class, () -> sdService.setParticipantId(sdJsonLdObj, bearerToken));
    }

    @Test
    void testSetParticipantIdParticipantIdNotFoundException() throws Exception {
        String sdJsonLd = createSdJson(CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(authenticationProviderClient.getParticipant(any())).thenThrow(FeignException.NotFound.class);

        assertThrows(RemoteServiceErrorException.class, () -> sdService.setParticipantId(sdJsonLdObj, bearerToken));
    }

    @Test
    void testRemoveProviderDataAddress() throws Exception {
        String sdJsonLd =
                """
                        {
                            "%s:assetProperties": {
                                "%s:providerDataAddress": "address"
                            }
                        }
                        """
                        .formatted(CommonConstants.ECOSYSTEM, CommonConstants.ECOSYSTEM);

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        sdService.removeProviderDataAddress(sdJsonLdObj);

        String ns = SDUtil.toNS(CommonConstants.ECOSYSTEM);
        assertTrue(sdJsonLdObj.has(ns + "assetProperties"), "Result should contain assetProperties");
        assertFalse(
                sdJsonLdObj.get(ns + "assetProperties").has(ns + "providerDataAddress"),
                "Provider data address should be removed from asset properties");
    }

    @Test
    void testSetMetadataSuccess() throws Exception {
        String sdJsonLd = "{}";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        SchemaMetadata schemaMetadata = SchemaMetadata.builder()
                .id("application-myshape")
                .title("My Shape")
                .description("My shape description")
                .version("1.0.0")
                .resourceType("application")
                .build();

        sdService.setMetadata(sdJsonLdObj, schemaMetadata);

        assertTrue(sdJsonLdObj.has(Constants.Dct.JSONLD_DCT_ROOT));
        JsonNode conformsTo = sdJsonLdObj.get(Constants.Dct.JSONLD_DCT_ROOT);
        assertEquals(
                Constants.Dct.JSONLD_DCT_STANDARD,
                conformsTo.get(Constants.Dct.JSONLD_DCT_TYPE).asText());
        assertEquals(
                Constants.Dct.DCT_ID_VALUE_PREFIX + schemaMetadata.getId(),
                conformsTo.get(Constants.Dct.JSONLD_DCT_ID).asText());
        assertEquals(
                schemaMetadata.getTitle(),
                conformsTo.get(Constants.Dct.JSONLD_DCT_TITLE).asText());
        assertEquals(
                schemaMetadata.getDescription(),
                conformsTo.get(Constants.Dct.JSONLD_DCT_DESCRIPTION).asText());
        assertEquals(
                schemaMetadata.getVersion(),
                conformsTo.get(Constants.Dct.JSONLD_DCT_HAS_VERSION).asText());
    }

    private String createSdJson(String ecosystem) {
        return """
                            {
                                "%s:generalServiceProperties": {},
                                "%s:providerInformation": {}
                            }
                        """
                .formatted(ecosystem, ecosystem);
    }
}
