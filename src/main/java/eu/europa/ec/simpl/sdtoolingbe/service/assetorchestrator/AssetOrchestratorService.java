package eu.europa.ec.simpl.sdtoolingbe.service.assetorchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AssetOrchestratorService {

    void registerWorkflow(JsonNode sdJsonLd, ObjectNode resourceAddress, String bearerToken);
}
