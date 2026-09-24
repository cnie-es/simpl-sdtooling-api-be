package eu.europa.ec.simpl.sdtoolingbe.service.enrich;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.exception.InvalidPayloadException;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;
import eu.europa.ec.simpl.data1.common.service.schemasyncrepo.SchemaSyncRepoService;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.sdtoolingbe.properties.OfferingTypeProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.assetorchestrator.AssetOrchestratorService;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import eu.europa.ec.simpl.sdtoolingbe.service.hash.HashService;
import eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress.ResourceAddressService;
import eu.europa.ec.simpl.sdtoolingbe.service.sd.SDService;
import eu.europa.ec.simpl.sdtoolingbe.service.validation.ValidationService;
import eu.europa.ec.simpl.sdtoolingbe.service.versioning.VersioningService;
import eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * SDService
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class EnrichServiceImpl implements EnrichService {

    private final AssetOrchestratorService assetOrchestratorService;
    ;
    private final ConnectorAdapterService connectorAdapterService;
    private final HashService hashService;
    private final ResourceAddressService resourceAddressService;
    private final SchemaSyncRepoService schemaSyncRepoService;
    private final SDService sdService;
    private final ValidationService validationService;
    private final VersioningService versioningService;

    private final OfferingTypeProperties offeringTypeProperties;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    public JsonNode enrichAndValidateV1(String tier1BearerToken, JsonNode sdJsonLd, String schemaId, String templateId)
            throws IOException {

        /*
        *  conformsToNode.put(JSONLD_DCT_TYPE, JSONLD_DCT_STANDARD);
           conformsToNode.put(JSONLD_DCT_ID, "https://simpl.example.org/schema/" + schemaId);
           conformsToNode.put(JSONLD_DCT_TITLE, "TTL Schema for " + schemaId);
           conformsToNode.put(JSONLD_DCT_DESCRIPTION, "Defines the expected structure and data types for a dataset.");
           conformsToNode.put(JSONLD_DCT_HAS_VERSION, "1.0");
        */

        String offeringTypeValue = offeringTypeProperties.getValueFromKey(schemaId);
        SchemaMetadata schemaMetadata = SchemaMetadata.builder()
                .id(schemaId)
                .title("TTL Schema for " + schemaId)
                .description("Defines the expected structure and data types for a dataset.")
                .version("1.0")
                .resourceType(offeringTypeValue)
                .build();
        String sourceAddressSchema = resourceAddressService.getSourceAddressSchema(templateId);
        String schemaContent = ShaclFileUtil.getTtlFromFilename(schemaId);
        return enrichAndValidateV2(
                tier1BearerToken, sdJsonLd, schemaMetadata, sourceAddressSchema, schemaContent, templateId);
    }

    @Override
    public JsonNode enrichAndValidateV2(String tier1BearerToken, JsonNode sdJsonLd, String schemaId, String templateId)
            throws IOException {
        SchemaMetadata schemaMetadata = schemaSyncRepoService.getSchemaMetadata(schemaId);
        String sourceAddressSchema = resourceAddressService.getSourceAddressSchema(templateId);
        String schemaContent = schemaSyncRepoService.getSchemaContent(schemaId);
        return enrichAndValidateV2(
                tier1BearerToken, sdJsonLd, schemaMetadata, sourceAddressSchema, schemaContent, templateId);
    }

    @Override
    public JsonNode enrichAndValidateV3(String tier1BearerToken, JsonNode payload, String schemaId) throws IOException {
        String templateId = JsonUtil.getStringValue(
                payload.toString(), CommonConstants.Payload.RESOURCE_ADDRESS_TEMPLATE_ID_PATH, true);
        SchemaMetadata schemaMetadata = schemaSyncRepoService.getSchemaMetadata(schemaId);
        String sourceAddressSchema = resourceAddressService.getSourceAddressSchema(templateId);
        String schemaContent = schemaSyncRepoService.getSchemaContent(schemaId);
        return enrichAndValidateV3(
                tier1BearerToken, payload, schemaMetadata, sourceAddressSchema, schemaContent, templateId);
    }

    private JsonNode enrichAndValidateV3(
            String tier1BearerToken,
            JsonNode payload,
            SchemaMetadata schemaMetadata,
            String sourceAddressSchema,
            String schemaContent,
            String templateId)
            throws IOException {

        // extracting the SD JSON-LD from the payload
        JsonNode sdJsonLd = payload.get(CommonConstants.Payload.SD_FIELD_NAME);
        if (!JsonUtil.isValorized(sdJsonLd) || !JsonUtil.isObject(sdJsonLd)) {
            throw new InvalidPayloadException("'" + CommonConstants.Payload.SD_FIELD_NAME + "' field not valorized");
        }

        // extracting the resourceAddress object string from the payload resourceAddress value field
        ObjectNode resourceAddress = JsonUtil.createObjectNodeFromPayload(
                payload, CommonConstants.Payload.RESOURCE_ADDRESS_VALUE_PATH, true, objectMapper);

        validationService.validateResourceAddress(resourceAddress, sourceAddressSchema);

        sdService.setOfferingType(sdJsonLd, schemaMetadata);

        // align the asset's langString @language tags with the metadata language (simpl:inLanguage)
        // before deriving from them, so the language-based selection below matches
        sdService.alignAssetLanguageToMetadata(sdJsonLd);

        // auto-populate the mandatory simpl:* metadata duplicated from the edval:*Asset
        // (corpus/lcr/model/api, hidden in the frontend); no-op when no asset node is present
        sdService.deriveSimplMetadataFromAsset(sdJsonLd);

        sdService.setSharingMethodId(sdJsonLd, templateId);

        sdService.setParticipantId(sdJsonLd, tier1BearerToken);

        // fix the contract template (hidden section) before hashing, which fills its hash/URL fields
        sdService.setDefaultContractTemplate(sdJsonLd);

        hashService.generateHashFromJsonLd(sdJsonLd);

        sdJsonLd = connectorAdapterService.registerV2(tier1BearerToken, payload, schemaMetadata);

        assetOrchestratorService.registerWorkflow(sdJsonLd, resourceAddress, tier1BearerToken);

        // keeping the providerDataAddress removing from SD if sent from the FE
        sdService.removeProviderDataAddress(sdJsonLd);

        sdService.setMetadata(sdJsonLd, schemaMetadata);

        versioningService.nextVersion(sdJsonLd);

        validationService.validateJsonLd(sdJsonLd, schemaContent);

        return sdJsonLd;
    }

    private JsonNode enrichAndValidateV2(
            String tier1BearerToken,
            JsonNode sdJsonLd,
            SchemaMetadata schemaMetadata,
            String sourceAddressSchema,
            String schemaContent,
            String templateId)
            throws IOException {

        // extracting the providerDataAddress object string from the SD JSON-LD
        ObjectNode providerDataAddress = JsonUtil.createObjectNodeFromSD(
                sdJsonLd, CommonConstants.SD.AssetProperties.PROVIDER_DATA_ADDRESS_PATH, true, objectMapper);

        validationService.validateResourceAddress(providerDataAddress, sourceAddressSchema);

        sdService.setOfferingType(sdJsonLd, schemaMetadata);

        // align the asset's langString @language tags with the metadata language (simpl:inLanguage)
        // before deriving from them, so the language-based selection below matches
        sdService.alignAssetLanguageToMetadata(sdJsonLd);

        // auto-populate the mandatory simpl:* metadata duplicated from the edval:*Asset
        // (corpus/lcr/model/api, hidden in the frontend); no-op when no asset node is present
        sdService.deriveSimplMetadataFromAsset(sdJsonLd);

        sdService.setSharingMethodId(sdJsonLd, templateId);

        sdService.setParticipantId(sdJsonLd, tier1BearerToken);

        // fix the contract template (hidden section) before hashing, which fills its hash/URL fields
        sdService.setDefaultContractTemplate(sdJsonLd);

        hashService.generateHashFromJsonLd(sdJsonLd);

        sdJsonLd = connectorAdapterService.registerV1(tier1BearerToken, sdJsonLd, schemaMetadata);

        assetOrchestratorService.registerWorkflow(sdJsonLd, providerDataAddress, tier1BearerToken);

        sdService.removeProviderDataAddress(sdJsonLd);

        sdService.setMetadata(sdJsonLd, schemaMetadata);

        versioningService.nextVersion(sdJsonLd);

        validationService.validateJsonLd(sdJsonLd, schemaContent);

        return sdJsonLd;
    }
}
