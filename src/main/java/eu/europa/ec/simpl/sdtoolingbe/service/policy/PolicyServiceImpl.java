package eu.europa.ec.simpl.sdtoolingbe.service.policy;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.Odrl;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlAssignee;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlAssigner;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlConstraint;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperand;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperator;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPolicy;
import eu.europa.ec.simpl.data1.common.util.DateTimeUtil;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyPermission;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction.PolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyConstraint;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyPermission;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private static final String ACCESS_POLICY_ACTIONS_PREFIX = "http://simpl.eu/odrl/actions/";

    // al momento mappature statiche
    private static List<PolicyAction> accessPolicyActions;

    private final ConnectorAdapterService connectorAdapterService;

    static {
        accessPolicyActions = new ArrayList<>();
        for (AccessPolicyAction item : AccessPolicyAction.values()) {
            accessPolicyActions.add(new PolicyAction(item.getActionName(), item.name()));
        }
    }

    @Override
    public List<PolicyAction> getAccessPolicyActions() {
        log.debug("getAccessPolicyActions()");
        return new ArrayList<>(accessPolicyActions);
    }

    @Override
    public OdrlPolicy getAccessPolicy(String bearerToken, AccessPolicyRequest request) {
        log.debug("getAccessPolicy() for {}", request);
        OdrlPolicy policy = new OdrlPolicy(UUID.randomUUID().toString(), "");

        log.debug("getAccessPolicy(): invoking connectorAdapterService.getConfiguration()");
        Participant participant = connectorAdapterService.getParticipant(bearerToken);

        // connector adapter configured participant id to be setted as Assigner in the Odrl policy
        policy.setAssigner(new OdrlAssigner(participant.getId()));

        for (AccessPolicyPermission item : request.getPermissions()) {
            policy.add(createOdrlPermission(item));
        }

        return policy;
    }

    @Override
    public OdrlPolicy getUsagePolicy(String bearerToken, UsagePolicyRequest request) {
        log.debug("getUsagePolicy() for {}", request);
        OdrlPolicy policy = new OdrlPolicy(UUID.randomUUID().toString(), "");

        log.debug("getAccessPolicy(): invoking connectorAdapterService.getParticipantId()");
        Participant participant = connectorAdapterService.getParticipant(bearerToken);

        // connector adapter configured participant id to be setted as Assigner in the Odrl policy
        policy.setAssigner(new OdrlAssigner(participant.getId()));

        for (UsagePolicyPermission item : request.getPermissions()) {
            policy.add(createOdrlPermission(item));
        }

        return policy;
    }

    private static OdrlPermission createOdrlPermission(AccessPolicyPermission permission) {
        OdrlPermission result = new OdrlPermission("");

        result.setAssignee(new OdrlAssignee(permission.getAssignee()));

        result.addAction(toAccessPolicyActionFullPath(permission.getAction()));

        OdrlConstraint constraint;
        if (permission.hasFromDate()) {
            constraint = new OdrlConstraint(
                    OdrlOperand.DATETIME,
                    OdrlOperator.GREAT_OR_EQUAL,
                    DateTimeUtil.dateTimeToString(permission.getFromDatetime(), null));
            result.add(constraint);
        }

        if (permission.hasToDate()) {
            constraint = new OdrlConstraint(
                    OdrlOperand.DATETIME,
                    OdrlOperator.LESS_OR_EQUAL,
                    DateTimeUtil.dateTimeToString(permission.getToDatetime(), null));
            result.add(constraint);
        }
        return result;
    }

    private static OdrlPermission createOdrlPermission(UsagePolicyPermission permission) {
        OdrlPermission result = new OdrlPermission("");
        result.setAssignee(new OdrlAssignee(permission.getAssignee()));
        result.addAction(toUsagePolicyActionFullPath(permission.getAction()));

        for (UsagePolicyConstraint item : permission.getConstraints()) {
            item.addOdrlConstraints(result);
        }

        return result;
    }

    private static String toAccessPolicyActionFullPath(AccessPolicyAction action) {
        return ACCESS_POLICY_ACTIONS_PREFIX + action.getActionName();
    }

    private static String toUsagePolicyActionFullPath(UsagePolicyAction action) {
        return Odrl.ODRL_NS_PREFIX_V2 + action.getActionName();
    }
}
