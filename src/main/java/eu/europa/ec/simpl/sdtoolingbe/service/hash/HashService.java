package eu.europa.ec.simpl.sdtoolingbe.service.hash;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public interface HashService {

    /**
     * Generates hash fields and inserts them into the JSON-LD file
     * @param jsonLd
     * @return
     */
    void generateHashFromJsonLd(JsonNode jsonLd) throws IOException;
}
