package eu.europa.ec.simpl.sdtoolingbe.client.connectoradapter;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.constant.MediaTypeConstants;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "connectorAdapterClient", url = "${connector-adapter.service.url}")
public interface ConnectorAdapterClient {

    String REGISTRATION_PATH_V1 = "/v1/selfDescriptions";
    String REGISTRATION_PATH_V2 = "/v2/selfDescriptions";

    String CONFIGURATION_PATH_V1 = "/v1/configs";

    String RESOURCE_ADDRESS_PATH_V1 = "/v1/resourceAddresses";

    @PostMapping(value = REGISTRATION_PATH_V1 + "/enriched", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> registerV1(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody String jsonFile,
            @RequestParam("offeringType") OfferType offeringType);

    @PostMapping(value = REGISTRATION_PATH_V2 + "/enriched", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> registerV2(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody String payload,
            @RequestParam("offeringType") OfferType offeringType);

    @GetMapping(value = CONFIGURATION_PATH_V1 + "/participant", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Participant> getParticipant(@RequestHeader("Authorization") String bearerToken);

    @GetMapping(value = RESOURCE_ADDRESS_PATH_V1 + "/{assetId}", produces = MediaTypeConstants.ALL_APPLICATION_JSON)
    ResponseEntity<ResourceAddress> getResourceAddress(
            @RequestHeader("Authorization") String bearerToken, @PathVariable("assetId") String assetId);
}
