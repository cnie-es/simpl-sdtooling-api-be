package eu.europa.ec.simpl.sdtoolingbe.client.assetorchestrator;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "assetOrchestratorClient", url = "${asset-orchestrator.service.url}")
public interface AssetOrchestratorClient {

    String WORKFLOW_DEFINITIONS_PATH_V1 = "/v1/workflowDefinitions";

    @PostMapping(value = WORKFLOW_DEFINITIONS_PATH_V1, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> registerV1(
            @RequestHeader("Authorization") String bearerToken, @RequestBody String jsonPayload);
}
