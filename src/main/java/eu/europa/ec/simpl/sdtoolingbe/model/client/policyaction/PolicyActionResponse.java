package eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction;

import java.util.List;
import lombok.Data;

@Data
public class PolicyActionResponse {

    private final List<PolicyAction> actions;
}
