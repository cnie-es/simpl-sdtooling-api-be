package eu.europa.ec.simpl.sdtoolingbe.service.sd;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;

public interface SDService {

    void setOfferingType(JsonNode sdJsonLd, SchemaMetadata schemaMetadata);

    void setSharingMethodId(JsonNode sdJsonLd, String templateId);

    void setParticipantId(JsonNode sdJsonLd, String bearerToken);

    void removeProviderDataAddress(JsonNode sdJsonLd);

    void setMetadata(JsonNode sdJsonLd, SchemaMetadata schemaMetadata);

    /**
     * Derives the mandatory {@code simpl:*} metadata that is duplicated from the data-offering asset
     * ({@code edval:corpusAsset} / {@code edval:lcrAsset} / {@code edval:modelAsset} /
     * {@code edval:apiAsset}) so those fields can be hidden in the frontend and populated
     * automatically here. Maps whichever fields the asset carries; missing ones are left untouched.
     * No-op when the SD has no {@code edval:*Asset} node.
     *
     * @param sdJsonLd the self-description JSON-LD to enrich in place
     */
    void deriveSimplMetadataFromAsset(JsonNode sdJsonLd);

    /**
     * Re-tags every {@code langString} ({@code {"@value", "@language"}}) inside the data-offering asset
     * ({@code edval:corpusAsset} / {@code edval:lcrAsset} / {@code edval:modelAsset} /
     * {@code edval:apiAsset}) so its {@code @language} matches the metadata language declared in
     * {@code simpl:generalServiceProperties -> simpl:inLanguage}. The asset text is authored in the
     * single metadata language selected by the user, but arrives tagged with a fixed default (e.g.
     * {@code "en"}); this aligns the tag with the actual language. No-op when there is no
     * {@code edval:*Asset} node or no {@code inLanguage} value.
     *
     * @param sdJsonLd the self-description JSON-LD to enrich in place
     */
    void alignAssetLanguageToMetadata(JsonNode sdJsonLd);

    /**
     * Fixes the contract template to the default one ({@code "Contract Template 1"}) so the whole
     * contract-template section can be hidden in the frontend. Creates the {@code simpl:contractTemplate}
     * node if absent. Must run before hash generation, which fills the hash/URL fields from this document.
     *
     * @param sdJsonLd the self-description JSON-LD to enrich in place
     */
    void setDefaultContractTemplate(JsonNode sdJsonLd);
}
