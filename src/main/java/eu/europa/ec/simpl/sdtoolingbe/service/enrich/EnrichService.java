package eu.europa.ec.simpl.sdtoolingbe.service.enrich;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public interface EnrichService {

    @Deprecated(since = "1.22.0", forRemoval = true)
    /*
     * @deprecated to be removed when FE will be aligned using the V2 SD controller
     */
    JsonNode enrichAndValidateV1(String tier1BearerToken, JsonNode sdJsonLd, String schemaId, String templateId)
            throws IOException;

    @Deprecated(since = "1.24.0", forRemoval = true)
    /*
     * @deprecated to be removed when FE will be aligned using the V3 SD controller
     */
    JsonNode enrichAndValidateV2(String tier1BearerToken, JsonNode sdJsonLd, String schemaId, String templateId)
            throws IOException;

    JsonNode enrichAndValidateV3(String tier1BearerToken, JsonNode payload, String schemaId) throws IOException;
}
