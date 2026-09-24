package eu.europa.ec.simpl.sdtoolingbe.service.versioning;

import com.fasterxml.jackson.databind.JsonNode;

public interface VersioningService {

    void nextVersion(JsonNode sdJsonLd);
}
