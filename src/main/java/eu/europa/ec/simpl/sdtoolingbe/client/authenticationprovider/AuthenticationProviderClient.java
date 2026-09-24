package eu.europa.ec.simpl.sdtoolingbe.client.authenticationprovider;

import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "authenticationProvider", url = "${authentication-provider.service.url}")
public interface AuthenticationProviderClient {

    @GetMapping("/tier1/v2/identityAttributes")
    String getIdentityAttributes(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("assignableToRoles") boolean assignableToRoles,
            @RequestParam("enabled") boolean enabled);

    @GetMapping("/tier1/v2/participant")
    Participant getParticipant(@RequestHeader("Authorization") String bearerToken);
}
