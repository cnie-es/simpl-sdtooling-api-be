package eu.europa.ec.simpl.sdtoolingbe.service.validation;

import com.fasterxml.jackson.databind.JsonNode;

public interface ValidationService {

    void validateJsonLd(JsonNode sdJsonLd, String schemaContent);

    void validateResourceAddress(JsonNode resourceAddress, String sourceAddressSchema);
}
