package eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;

public interface ConnectorAdapterService {

    JsonNode registerV1(String bearerToken, JsonNode sdJsonLd, SchemaMetadata schemaMetadata);

    JsonNode registerV2(String bearerToken, JsonNode payload, SchemaMetadata schemaMetadata);

    Participant getParticipant(String bearerToken);

    ResourceAddress getResourceAddress(String bearerToken, String assetId);
}
