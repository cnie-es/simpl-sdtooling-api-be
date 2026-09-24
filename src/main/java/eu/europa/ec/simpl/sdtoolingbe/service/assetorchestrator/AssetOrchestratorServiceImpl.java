package eu.europa.ec.simpl.sdtoolingbe.service.assetorchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import eu.europa.ec.simpl.sdtoolingbe.client.assetorchestrator.AssetOrchestratorClient;
import eu.europa.ec.simpl.sdtoolingbe.enumeration.AppErrorType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AssetOrchestratorServiceImpl implements AssetOrchestratorService {

    private static final String REGISTRABLE_TYPE = "ProcessingWorkflow";

    private final AssetOrchestratorClient assetOrchestratorClient;

    @Override
    public void registerWorkflow(JsonNode sdJsonLd, ObjectNode resourceAddress, String bearerToken) {
        try {
            if (!isRegistrable(resourceAddress)) {
                log.debug("registerWorkflow() ignored cause resourceAddress is not registrable: {}", resourceAddress);
                return;
            }
            enrichResourceAddress(resourceAddress, sdJsonLd);

            log.debug("registerWorkflow(): invoking assetOrchestratorClient.registerV1() for {}", resourceAddress);
            ResponseEntity<String> response = assetOrchestratorClient.registerV1(
                    AuthBearerUtil.toBearerString(bearerToken), resourceAddress.toString());
            log.debug("registerWorkflow(): received response {}", response);
        } catch (FeignException e) {
            log.error("registerWorkflow() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(AppErrorType.REMOTE_ASSET_ORCHESTRATOR_ERROR, e);
        }
    }

    private boolean isRegistrable(ObjectNode resourceAddressValue) {
        String type = JsonUtil.getStringValue(resourceAddressValue.get("type"));
        return REGISTRABLE_TYPE.equalsIgnoreCase(type);
    }

    private void enrichResourceAddress(ObjectNode resourceAddressValue, JsonNode sdJsonLd) {
        String assetId =
                JsonUtil.getStringValueFromSD(sdJsonLd, CommonConstants.SD.EdcRegistration.ASSET_ID_PATH, true);
        String assetDescription = JsonUtil.getStringValueFromSD(
                sdJsonLd, CommonConstants.SD.GeneralServiceProperties.DESCRIPTION_PATH, true);
        String providerEmail =
                JsonUtil.getStringValueFromSD(sdJsonLd, CommonConstants.SD.ProviderInformation.CONTACT_PATH, true);

        resourceAddressValue.put("assetId", assetId);
        resourceAddressValue.put("assetDescription", assetDescription);
        resourceAddressValue.put("providerEmail", providerEmail);
    }
}
