package eu.europa.ec.simpl.sdtoolingbe.service.policy;

import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPolicy;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction.PolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyRequest;
import java.util.List;

public interface PolicyService {

    List<PolicyAction> getAccessPolicyActions();

    OdrlPolicy getAccessPolicy(String bearerToken, AccessPolicyRequest request);

    OdrlPolicy getUsagePolicy(String bearerToken, UsagePolicyRequest request);
}
