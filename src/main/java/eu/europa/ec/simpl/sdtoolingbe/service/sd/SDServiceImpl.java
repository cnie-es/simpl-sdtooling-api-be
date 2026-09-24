package eu.europa.ec.simpl.sdtoolingbe.service.sd;

import static eu.europa.ec.simpl.sdtoolingbe.constant.Constants.Dct.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.data1.common.util.SDUtil;
import eu.europa.ec.simpl.sdtoolingbe.constant.Constants;
import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import eu.europa.ec.simpl.sdtoolingbe.properties.ResourceAddressProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider.AuthenticationProviderService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SDService
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class SDServiceImpl implements SDService {

    private static final String SHAPE_OFFERING_TYPE_FIELD_PARENT_NAME = "generalServiceProperties";
    private static final String SHAPE_OFFERING_TYPE_FIELD_NAME = "offeringType";

    private static final String SHAPE_SHARING_METHOD_FIELD_PARENT_NAME = "generalServiceProperties";
    private static final String SHAPE_SHARING_METHOD_ID_FIELD_NAME = "sharingMethodId";

    private static final String SHAPE_PARTICIPANT_ID_FIELD_PARENT_NAME = "providerInformation";
    private static final String SHAPE_PARTICIPANT_ID_FIELD_NAME = "providedBy";
    // Signature is, for now, auto-filled with the participant id (hidden in the frontend).
    private static final String SHAPE_SIGNATURE_FIELD_NAME = "signature";

    // Contract template is, for now, fixed so the whole section can be hidden; the hash/URL fields
    // are then filled by HashService from this document key.
    private static final String SHAPE_CONTRACT_TEMPLATE = "contractTemplate";
    private static final String FIELD_CONTRACT_TEMPLATE_DOCUMENT = "contractTemplateDocument";
    private static final String DEFAULT_CONTRACT_TEMPLATE = "Contract Template 1";
    private static final String CONTRACT_TEMPLATE_TYPE = "simpl:ContractTemplate";
    private static final String RDF_TYPE = "rdf:type";

    // --- Auto-derivation of simpl:* metadata from the edval:*Asset node ---
    // All data offerings (corpus/lcr/model/api) carry their metadata under one of these keys;
    // the inner field keys below are shared across every asset type (API simply omits some).
    private static final String API_ASSET_FIELD = "edval:apiAsset";
    private static final List<String> ASSET_FIELDS =
            List.of("edval:corpusAsset", "edval:lcrAsset", "edval:modelAsset", API_ASSET_FIELD);
    private static final String ASSET_TITLE = "dct:title";
    private static final String ASSET_DESCRIPTION = "dct:description";
    private static final String ASSET_KEYWORD = "dcat:keyword";
    private static final String ASSET_DISTRIBUTION = "dcat:distribution";
    private static final String ASSET_FORMAT = "dct:format";
    private static final String ASSET_LICENSE = "dct:license";
    private static final String ASSET_LEGALCODE = "cc:legalcode";
    private static final String ASSET_LR_TYPE = "ms:lrType";
    // lrType is fixed per asset type: assigned here (as an @id) and hidden in the frontend.
    private static final Map<String, String> ASSET_LR_TYPE_BY_FIELD = Map.of(
            "edval:corpusAsset", "ms:corpus1",
            "edval:lcrAsset", "ms:lexicalConceptualResource1",
            "edval:modelAsset", "ms:MLModel",
            API_ASSET_FIELD, "ms:toolService1");

    // JSON-LD value object keys.
    private static final String AT_VALUE = "@value";
    private static final String AT_LANGUAGE = "@language";
    private static final String AT_ID = "@id";
    private static final String AT_TYPE = "@type";
    private static final String XSD_ANY_URI = "xsd:anyURI";

    // Target simpl parent shapes and fields to populate.
    private static final String SHAPE_GENERAL_SERVICE_PROPERTIES = "generalServiceProperties";
    private static final String SHAPE_DATA_PROPERTIES = "dataProperties";
    private static final String SHAPE_OFFERING_PRICE = "offeringPrice";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_IN_LANGUAGE = "inLanguage";
    private static final String FIELD_KEYWORDS = "keywords";
    private static final String FIELD_FORMAT = "format";
    private static final String FIELD_LICENSE = "license";

    // Default dataProperties.format for API offerings (no distribution/format in ms:ToolService).
    private static final String API_DEFAULT_FORMAT = "json";

    // Frontend/shape length and cardinality limits.
    private static final int MAX_NAME_LEN = 255;
    private static final int MAX_DESCRIPTION_LEN = 1000;
    private static final int MAX_KEYWORDS = 16;
    private static final int MAX_KEYWORD_LEN = 50;

    private final ResourceAddressProperties resourceAddressProperties;

    private final AuthenticationProviderService authenticationProviderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setOfferingType(JsonNode sdJsonLd, SchemaMetadata schemaMetadata) {
        log.debug("setOfferingType() for schemaMetadata {} and sdJsonLd {}", schemaMetadata, sdJsonLd);

        if (sdJsonLd.has(SDUtil.NS + SHAPE_OFFERING_TYPE_FIELD_PARENT_NAME)) {
            ObjectNode parentObj = (ObjectNode) sdJsonLd.get(SDUtil.NS + SHAPE_OFFERING_TYPE_FIELD_PARENT_NAME);
            parentObj.put(SDUtil.NS + SHAPE_OFFERING_TYPE_FIELD_NAME, schemaMetadata.getResourceType());
        }
        log.debug("setOfferingType(): result {}", sdJsonLd);
    }

    @Override
    public void setSharingMethodId(JsonNode sdJsonLd, String templateId) {
        log.debug("setSharingMethodId() for templateId '{}' and sdJsonLd {}", templateId, sdJsonLd);
        if (sdJsonLd.has(SDUtil.NS + SHAPE_SHARING_METHOD_FIELD_PARENT_NAME)) {
            String sharingMethodId = resourceAddressProperties.getSharingMethodId(templateId);
            if (StringUtils.isBlank(sharingMethodId)) {
                log.error("setSharingMethodId() failed cause no sharingMethodId found for templateId '{}'", templateId);
                throw new ResourceAddressNotFoundException(
                        "SharingMethod not found", "No sharingMethod found for templateId " + templateId);
            }
            ObjectNode parentObj = (ObjectNode) sdJsonLd.get(SDUtil.NS + SHAPE_SHARING_METHOD_FIELD_PARENT_NAME);
            parentObj.put(SDUtil.NS + SHAPE_SHARING_METHOD_ID_FIELD_NAME, sharingMethodId);
        }
        log.debug("setSharingMethodId(): result {}", sdJsonLd);
    }

    @Override
    public void setParticipantId(JsonNode sdJsonLd, String bearerToken) {
        log.debug("setParticipantId() for sdJsonLd {}", sdJsonLd);
        Participant participant;
        try {
            participant = authenticationProviderService.getParticipant(bearerToken);
        } catch (RemoteServiceErrorException e) {
            log.error("setParticipantId() failed retrieving participant: {}", e.getMessage());
            throw e;
        }

        if (participant == null) {
            log.error(
                    "setParticipantId() failed cause no participant returned by authenticationProviderService.getCredentials()");
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_AUTH_PROVIDER_ERROR, "No participant returned", null);
        }

        String participantId = participant.getId();
        if (StringUtils.isBlank(participantId)) {
            log.error(
                    "setParticipantId() failed cause no participantId found in the participant returned by authenticationProviderService.getCredentials()");
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_AUTH_PROVIDER_ERROR, "No participantId found in participant response", null);
        }

        log.debug(
                "setParticipantId(): found participantId '{}' in the participant returned by authenticationProviderService",
                participantId);
        if (sdJsonLd.has(SDUtil.NS + SHAPE_PARTICIPANT_ID_FIELD_PARENT_NAME)) {
            ObjectNode parentObj = (ObjectNode) sdJsonLd.get(SDUtil.NS + SHAPE_PARTICIPANT_ID_FIELD_PARENT_NAME);
            parentObj.put(SDUtil.NS + SHAPE_PARTICIPANT_ID_FIELD_NAME, participantId);
            // temporary signature: derive it from the participant id (field hidden in the frontend).
            // The signature shape enforces ^[a-zA-Z0-9][a-zA-Z0-9\s]*$, so strip every other character
            // (the raw participant id is a URI and would fail that pattern).
            parentObj.put(SDUtil.NS + SHAPE_SIGNATURE_FIELD_NAME, toSignatureValue(participantId));
        }

        log.debug("setParticipantId(): result {}", sdJsonLd);
    }

    /**
     * Builds a placeholder signature value that satisfies the signature shape pattern
     * {@code ^[a-zA-Z0-9][a-zA-Z0-9\s]*$}. The raw participant id is a URI, so every character other
     * than letters and digits is removed. Falls back to {@code "signature"} if nothing remains.
     */
    private String toSignatureValue(String participantId) {
        String sanitized = participantId == null ? "" : participantId.replaceAll("[^a-zA-Z0-9]", "");
        return StringUtils.isBlank(sanitized) ? "signature" : sanitized;
    }

    @Override
    public void removeProviderDataAddress(JsonNode sdJsonLd) {
        JsonUtil.removeNode(sdJsonLd, CommonConstants.SD.AssetProperties.PROVIDER_DATA_ADDRESS_PATH);
        log.debug("removeProviderDataAddress(): result {}", sdJsonLd);
    }

    @Override
    public void setMetadata(JsonNode sdJsonLd, SchemaMetadata schemaMetadata) {
        log.debug("setMetadata() for schemaMetadata {} and sdJsonLd {}", schemaMetadata, sdJsonLd);

        ObjectNode conformsToNode = objectMapper.createObjectNode();
        conformsToNode.put(JSONLD_DCT_TYPE, JSONLD_DCT_STANDARD);
        conformsToNode.put(JSONLD_DCT_ID, Constants.Dct.DCT_ID_VALUE_PREFIX + schemaMetadata.getId());
        conformsToNode.put(JSONLD_DCT_SCHEMA_NAME, schemaMetadata.getName());
        conformsToNode.put(JSONLD_DCT_TITLE, schemaMetadata.getTitle());
        conformsToNode.put(JSONLD_DCT_DESCRIPTION, schemaMetadata.getDescription());
        conformsToNode.put(JSONLD_DCT_HAS_VERSION, schemaMetadata.getVersion());

        ObjectNode rootObj = (ObjectNode) sdJsonLd;
        rootObj.set(JSONLD_DCT_ROOT, conformsToNode);
        log.debug("setMetadata(): result {}", sdJsonLd);
    }

    @Override
    public void setDefaultContractTemplate(JsonNode sdJsonLd) {
        log.debug("setDefaultContractTemplate() for sdJsonLd {}", sdJsonLd);

        String key = SDUtil.NS + SHAPE_CONTRACT_TEMPLATE;
        ObjectNode contractTemplate;
        if (sdJsonLd.has(key) && sdJsonLd.get(key).isObject()) {
            contractTemplate = (ObjectNode) sdJsonLd.get(key);
        } else {
            // section hidden in the frontend: create the node so validation and hashing have something to work on
            contractTemplate = objectMapper.createObjectNode();
            ObjectNode type = objectMapper.createObjectNode();
            type.put(AT_ID, CONTRACT_TEMPLATE_TYPE);
            contractTemplate.set(RDF_TYPE, type);
            ((ObjectNode) sdJsonLd).set(key, contractTemplate);
        }
        contractTemplate.put(SDUtil.NS + FIELD_CONTRACT_TEMPLATE_DOCUMENT, DEFAULT_CONTRACT_TEMPLATE);

        log.debug("setDefaultContractTemplate(): result {}", sdJsonLd);
    }

    @Override
    public void alignAssetLanguageToMetadata(JsonNode sdJsonLd) {
        log.debug("alignAssetLanguageToMetadata() for sdJsonLd {}", sdJsonLd);

        String assetField = presentAssetField(sdJsonLd);
        if (assetField == null) {
            log.debug("alignAssetLanguageToMetadata(): no edval:*Asset node present, skipping");
            return;
        }

        JsonNode gsp = sdJsonLd.get(SDUtil.NS + SHAPE_GENERAL_SERVICE_PROPERTIES);
        String metadataLanguage = plainText(gsp == null ? null : gsp.get(SDUtil.NS + FIELD_IN_LANGUAGE));
        if (StringUtils.isBlank(metadataLanguage)) {
            log.debug("alignAssetLanguageToMetadata(): no simpl:inLanguage value present, skipping");
            return;
        }

        retagLangStrings(sdJsonLd.get(assetField), metadataLanguage);

        log.debug("alignAssetLanguageToMetadata(): result {}", sdJsonLd);
    }

    /**
     * Recursively sets {@code @language} to {@code language} on every langString object
     * (one carrying both {@code @value} and {@code @language}) found under the given node.
     */
    private void retagLangStrings(JsonNode node, String language) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            if (obj.has(AT_VALUE) && obj.has(AT_LANGUAGE)) {
                obj.put(AT_LANGUAGE, language);
            }
            for (JsonNode child : obj) {
                retagLangStrings(child, language);
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                retagLangStrings(child, language);
            }
        }
    }

    @Override
    public void deriveSimplMetadataFromAsset(JsonNode sdJsonLd) {
        log.debug("deriveSimplMetadataFromAsset() for sdJsonLd {}", sdJsonLd);

        String assetField = presentAssetField(sdJsonLd);
        if (assetField == null) {
            log.debug("deriveSimplMetadataFromAsset(): no edval:*Asset node present, skipping");
            return;
        }
        JsonNode asset = sdJsonLd.get(assetField);

        // lrType is fixed per asset type: assign it here (hidden in the frontend)
        putId(asset, ASSET_LR_TYPE, ASSET_LR_TYPE_BY_FIELD.get(assetField));

        // generalServiceProperties: name, description, keywords (present in every asset).
        // inLanguage is NOT derived: it is the metadata language, always entered by the user, and it is
        // what selects which language variant of the asset's langString fields we copy over.
        JsonNode gsp = sdJsonLd.get(SDUtil.NS + SHAPE_GENERAL_SERVICE_PROPERTIES);
        String metadataLanguage = plainText(gsp == null ? null : gsp.get(SDUtil.NS + FIELD_IN_LANGUAGE));
        putString(gsp, SDUtil.NS + FIELD_NAME, langValueFor(asset.get(ASSET_TITLE), metadataLanguage, MAX_NAME_LEN));
        putString(
                gsp,
                SDUtil.NS + FIELD_DESCRIPTION,
                langValueFor(asset.get(ASSET_DESCRIPTION), metadataLanguage, MAX_DESCRIPTION_LEN));
        putStringArray(
                gsp, SDUtil.NS + FIELD_KEYWORDS, asset.get(ASSET_KEYWORD), metadataLanguage, MAX_KEYWORDS, MAX_KEYWORD_LEN);

        // dataProperties: format from the distribution's dct:format. API (ms:ToolService) has no
        // distribution, so it falls back to a default format.
        JsonNode dataProperties = sdJsonLd.get(SDUtil.NS + SHAPE_DATA_PROPERTIES);
        String format = distributionFormat(asset);
        if (StringUtils.isBlank(format) && sdJsonLd.has(API_ASSET_FIELD)) {
            format = API_DEFAULT_FORMAT;
        }
        putString(dataProperties, SDUtil.NS + FIELD_FORMAT, format);

        // offeringPrice: license URL from the asset's dct:license -> cc:legalcode, typed as xsd:anyURI
        JsonNode offeringPrice = sdJsonLd.get(SDUtil.NS + SHAPE_OFFERING_PRICE);
        putTypedUri(offeringPrice, SDUtil.NS + FIELD_LICENSE, firstLegalcode(asset.get(ASSET_LICENSE)));

        log.debug("deriveSimplMetadataFromAsset(): result {}", sdJsonLd);
    }

    /**
     * Returns the key of the first {@code edval:*Asset} node present on the SD (corpus/lcr/model/api),
     * or {@code null} if none is present.
     */
    private String presentAssetField(JsonNode sdJsonLd) {
        for (String assetField : ASSET_FIELDS) {
            JsonNode asset = sdJsonLd.get(assetField);
            if (asset != null && !asset.isNull()) {
                return assetField;
            }
        }
        return null;
    }

    /**
     * Returns the first element of a node that may be a single object or an array (or {@code null}).
     */
    private JsonNode firstElement(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isArray()) {
            return node.isEmpty() ? null : node.get(0);
        }
        return node;
    }

    /**
     * Extracts the plain text of a langString value, choosing the entry whose {@code @language} matches
     * the metadata language ({@code inLanguage}); falls back to the first entry. Truncated to {@code maxLen}.
     */
    private String langValueFor(JsonNode node, String language, int maxLen) {
        JsonNode value = pickByLanguage(node, language);
        if (value == null) {
            return null;
        }
        String text = value.has(AT_VALUE) ? value.get(AT_VALUE).asText() : value.asText();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }

    /**
     * Picks the langString entry matching {@code language} (by primary subtag), or the first entry when
     * there is no match or no language is given. Accepts a single object or an array.
     */
    private JsonNode pickByLanguage(JsonNode node, String language) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isArray()) {
            return node;
        }
        if (node.isEmpty()) {
            return null;
        }
        if (StringUtils.isNotBlank(language)) {
            for (JsonNode element : node) {
                if (languageMatches(element, language)) {
                    return element;
                }
            }
        }
        return node.get(0);
    }

    /**
     * True when the entry's {@code @language} primary subtag equals {@code language} (e.g. "en" ~ "en-GB").
     */
    private boolean languageMatches(JsonNode entry, String language) {
        JsonNode lang = entry.get(AT_LANGUAGE);
        if (lang == null || lang.isNull()) {
            return false;
        }
        String entryLanguage = lang.asText();
        return StringUtils.isNotBlank(entryLanguage) && entryLanguage.split("-")[0].equalsIgnoreCase(language);
    }

    /**
     * Returns the plain text of a scalar node, unwrapping a {@code {"@value": ...}} object if present.
     */
    private String plainText(JsonNode node) {
        JsonNode value = firstElement(node);
        if (value == null) {
            return null;
        }
        return value.has(AT_VALUE) ? value.get(AT_VALUE).asText() : value.asText();
    }

    /**
     * Maps the distribution's {@code dct:format} IRI (e.g. {@code omtd:Csv}) to a simpl format label
     * ({@code csv}) that satisfies the {@code ^[a-zA-Z0-9][a-zA-Z0-9\s]*$} pattern.
     */
    private String distributionFormat(JsonNode asset) {
        JsonNode distribution = firstElement(asset.get(ASSET_DISTRIBUTION));
        if (distribution == null) {
            return null;
        }
        JsonNode format = firstElement(distribution.get(ASSET_FORMAT));
        if (format == null) {
            return null;
        }
        String id = format.has(AT_ID) ? format.get(AT_ID).asText() : format.asText();
        if (StringUtils.isBlank(id)) {
            return null;
        }
        String local = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        local = local.replaceAll("[^A-Za-z0-9]", "");
        return local.isEmpty() ? null : local.toLowerCase(Locale.ROOT);
    }

    /**
     * Extracts the license URL from the first {@code dct:license} entry's {@code cc:legalcode}.
     */
    private String firstLegalcode(JsonNode licenseNode) {
        JsonNode license = firstElement(licenseNode);
        if (license == null) {
            return null;
        }
        JsonNode legalcode = firstElement(license.get(ASSET_LEGALCODE));
        if (legalcode == null) {
            return null;
        }
        String url = legalcode.has(AT_VALUE) ? legalcode.get(AT_VALUE).asText() : legalcode.asText();
        return StringUtils.isBlank(url) ? null : url;
    }

    /**
     * Sets a plain string field on the parent shape node, only when both the parent and value are present.
     */
    private void putString(JsonNode parent, String field, String value) {
        if (parent != null && parent.isObject() && StringUtils.isNotBlank(value)) {
            ((ObjectNode) parent).put(field, value);
        }
    }

    /**
     * Sets an IRI reference {@code {"@id": iri}} on the parent node, only when both are present.
     */
    private void putId(JsonNode parent, String field, String iri) {
        if (parent != null && parent.isObject() && StringUtils.isNotBlank(iri)) {
            ObjectNode value = objectMapper.createObjectNode();
            value.put(AT_ID, iri);
            ((ObjectNode) parent).set(field, value);
        }
    }

    /**
     * Sets a typed value object {@code {"@value": url, "@type": "xsd:anyURI"}} on the parent shape node.
     */
    private void putTypedUri(JsonNode parent, String field, String value) {
        if (parent != null && parent.isObject() && StringUtils.isNotBlank(value)) {
            ObjectNode valueObj = objectMapper.createObjectNode();
            valueObj.put(AT_VALUE, value);
            valueObj.put(AT_TYPE, XSD_ANY_URI);
            ((ObjectNode) parent).set(field, valueObj);
        }
    }

    /**
     * Sets a string array field from a langString array, keeping only entries in the metadata language
     * (falling back to all entries when none match), capping the number of items and their length.
     */
    private void putStringArray(
            JsonNode parent, String field, JsonNode sourceNode, String language, int maxItems, int maxLen) {
        if (parent == null || !parent.isObject() || sourceNode == null || sourceNode.isNull()) {
            return;
        }
        Iterable<JsonNode> elements = sourceNode.isArray() ? sourceNode : List.of(sourceNode);

        // only filter by language when at least one entry actually matches it; otherwise keep them all
        boolean filterByLanguage = false;
        if (StringUtils.isNotBlank(language) && sourceNode.isArray()) {
            for (JsonNode element : sourceNode) {
                if (languageMatches(element, language)) {
                    filterByLanguage = true;
                    break;
                }
            }
        }

        ArrayNode array = objectMapper.createArrayNode();
        for (JsonNode element : elements) {
            if (array.size() >= maxItems) {
                break;
            }
            if (filterByLanguage && !languageMatches(element, language)) {
                continue;
            }
            String text = element.has(AT_VALUE) ? element.get(AT_VALUE).asText() : element.asText();
            if (StringUtils.isNotBlank(text)) {
                array.add(text.length() > maxLen ? text.substring(0, maxLen) : text);
            }
        }
        if (!array.isEmpty()) {
            ((ObjectNode) parent).set(field, array);
        }
    }
}
