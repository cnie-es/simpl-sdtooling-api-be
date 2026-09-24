package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPolicy;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.model.client.identityattribute.IdentityAttribute;
import eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction.PolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider.AuthenticationProviderService;
import eu.europa.ec.simpl.sdtoolingbe.service.policy.PolicyService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController("policyControllerV1")
@Log4j2
@RequiredArgsConstructor
public class PolicyControllerImpl extends AbstractController implements PolicyController {

    private final PolicyService policyService;
    private final AuthenticationProviderService authenticationProviderService;

    @LogRequest
    @Override
    public List<IdentityAttribute> getIdentityAttributes(HttpServletRequest httpRequest) {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);

        log.debug("getIdentityAttributes(): invoking policyService.getIdentityAttributes()");
        List<IdentityAttribute> result = authenticationProviderService.getIdentityAttributes(tier1BearerToken);

        log.debug("getIdentityAttributes(): returning '{}'", result);
        return result;
    }

    @LogRequest
    @Override
    public List<PolicyAction> getAccessPolicyActions() {
        log.debug("getAccessPolicyActions(): invoking policyService.getAccessPolicyActions()");
        List<PolicyAction> result = policyService.getAccessPolicyActions();

        log.debug("getAccessPolicyActions(): returning '{}'", result);
        return result;
    }

    @LogRequest
    @Override
    public OdrlPolicy getAccessPolicyJsonLD(AccessPolicyRequest request, HttpServletRequest httpRequest) {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);

        log.debug("getAccessPolicyJsonLD(): invoking policyService.getAccessPolicy() for {}", request);
        OdrlPolicy result = policyService.getAccessPolicy(tier1BearerToken, request);

        log.debug("getAccessPolicyJsonLD(): returning '{}' for request '{}'", result, request);
        return result;
    }

    @LogRequest
    @Override
    public OdrlPolicy getUsagePolicyJsonLD(UsagePolicyRequest request, HttpServletRequest httpRequest) {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);

        log.debug("getUsagePolicyJsonLD(): invoking policyService.getUsagePolicy() for {}", request);
        OdrlPolicy result = policyService.getUsagePolicy(tier1BearerToken, request);

        log.debug("getUsagePolicyJsonLD(): returning '{}' for request '{}'", result, request);
        return result;
    }
}
