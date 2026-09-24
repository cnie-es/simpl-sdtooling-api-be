package eu.europa.ec.simpl.sdtoolingbe.service.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlAssigner;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlConstraint;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPolicy;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.DateTimeUtil;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyPermission;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction.PolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.DeletionConstraint;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.RestrictedDurationConstraint;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.RestrictedNumberConstraint;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyConstraint;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyPermission;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    private static final String BEARER_TOKEN = AuthBearerUtil.toBearerString("testToken}");

    private PolicyService policyService;

    @Mock
    private ConnectorAdapterService connectorAdapterService;

    @BeforeEach
    void setUpEach() {
        policyService = new PolicyServiceImpl(connectorAdapterService);

        lenient()
                .when(connectorAdapterService.getParticipant(BEARER_TOKEN))
                .thenReturn(Participant.builder().id("participant-id-0001").build());
    }

    @Test
    void testGetAccessPolicyActions() {
        List<PolicyAction> actions = policyService.getAccessPolicyActions();
        assertNotNull(actions);
        assertFalse(actions.isEmpty());
    }

    @Test
    void testGetAccessPolicy() {
        AccessPolicyRequest request = new AccessPolicyRequest();
        request.setPermissions(List.of(newAccessPolicyPermission(
                "assignee", AccessPolicyAction.CONSUME, "2024-01-01T00:00:00", "2024-03-31T23:59:59")));

        checkAccessPolicyPermissions(request.getPermissions());

        OdrlPolicy policy = policyService.getAccessPolicy(BEARER_TOKEN, request);
        checkOdrlPolicy(policy);
    }

    @Test
    void testGetUsagePolicy() {
        UsagePolicyRequest request = new UsagePolicyRequest();
        request.setPermissions(List.of(newUsagePolicyPermission("assignee", UsagePolicyAction.USE)));

        checkUsagePolicyPermissions(request.getPermissions());

        OdrlPolicy policy = policyService.getUsagePolicy(BEARER_TOKEN, request);
        checkOdrlPolicy(policy);
    }

    private void checkAccessPolicyPermissions(List<AccessPolicyPermission> permissions) {
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
        for (AccessPolicyPermission item : permissions) {
            assertNotNull(item.getAction());

            assertNotNull(item.getAssignee());
            assertFalse(item.getAssignee().isEmpty());

            assertNotNull(item.getFromDatetime());
            assertTrue(item.hasFromDate());

            assertNotNull(item.getToDatetime());
            assertTrue(item.hasToDate());
        }
    }

    private void checkUsagePolicyPermissions(List<UsagePolicyPermission> permissions) {
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
        for (UsagePolicyPermission item : permissions) {
            assertNotNull(item.getAction());

            assertNotNull(item.getAssignee());
            assertFalse(item.getAssignee().isEmpty());

            checkUsagePolicyConstraints(item.getConstraints());
        }
    }

    private void checkUsagePolicyConstraints(List<UsagePolicyConstraint> constraints) {
        assertNotNull(constraints);
        assertFalse(constraints.isEmpty());
        for (UsagePolicyConstraint item : constraints) {
            assertNotNull(item.getType());
            switch (item.getType()) {
                case DeletionConstraint.TYPE:
                    check((DeletionConstraint) item);
                    break;
                case RestrictedDurationConstraint.TYPE:
                    check((RestrictedDurationConstraint) item);
                    break;
                case RestrictedNumberConstraint.TYPE:
                    check((RestrictedNumberConstraint) item);
                    break;
                default:
                    // ignored type
            }
        }
    }

    private void check(RestrictedNumberConstraint constraint) {
        assertEquals(RestrictedNumberConstraint.TYPE, constraint.getType());
        assertTrue(constraint.getMaxCount() > 0);
    }

    private void check(RestrictedDurationConstraint constraint) {
        assertEquals(RestrictedDurationConstraint.TYPE, constraint.getType());
        assertNotNull(constraint.getFromDatetime());
        assertNotNull(constraint.getToDatetime());
        assertNotNull(constraint.getToDatetime());
    }

    private void check(DeletionConstraint constraint) {
        assertEquals(DeletionConstraint.TYPE, constraint.getType());
        assertTrue(constraint.isAfterUse());
    }

    private void checkOdrlPolicy(OdrlPolicy policy) {
        assertNotNull(policy);

        checkOdrlPermissions(policy.getPermissions());
        checkOdrlAssigner(policy.getAssigner());

        assertNotNull(policy.getProfile());
        assertFalse(policy.getProfile().isEmpty());

        assertNotNull(policy.getTarget());
        assertTrue(policy.getTarget().isEmpty());

        assertNotNull(policy.getType());
        assertFalse(policy.getType().isEmpty());

        assertNotNull(policy.getUid());
        assertFalse(policy.getUid().isEmpty());
    }

    private void checkOdrlAssigner(OdrlAssigner assigner) {
        assertNotNull(assigner);
        assertNotNull(assigner.getRole());
        assertFalse(assigner.getRole().isEmpty());
        assertNotNull(assigner.getUid());
        assertFalse(assigner.getUid().isEmpty());
    }

    private void checkOdrlPermissions(List<OdrlPermission> permissions) {
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
        for (OdrlPermission item : permissions) {
            assertNotNull(item.getTarget());
            assertTrue(item.getTarget().isEmpty());

            assertNotNull(item.getAssignee());
            assertFalse(item.getAssignee().getRole().isEmpty());
            assertFalse(item.getAssignee().getUid().isEmpty());

            checkOdrlActions(item.getActions());
            checkOdrlConstraints(item.getConstraints());
        }
    }

    private void checkOdrlActions(List<String> actions) {
        assertNotNull(actions);
        assertFalse(actions.isEmpty());
        for (String item : actions) {
            assertNotNull(item);
            assertFalse(item.isEmpty());
        }
    }

    private void checkOdrlConstraints(List<OdrlConstraint> constraints) {
        assertNotNull(constraints);
        assertFalse(constraints.isEmpty());
        for (OdrlConstraint item : constraints) {
            assertNotNull(item.getLeftOperand());
            assertFalse(item.getLeftOperand().isEmpty());

            assertNotNull(item.getOperator());
            assertFalse(item.getOperator().isEmpty());

            assertNotNull(item.getRightOperand());
            assertFalse(item.getRightOperand().isEmpty());
        }
    }

    private UsagePolicyPermission newUsagePolicyPermission(String assignee, UsagePolicyAction action) {
        UsagePolicyPermission result = new UsagePolicyPermission();
        result.setAssignee(assignee);
        result.setAction(action);
        result.setConstraints(List.of(
                newDeletionConstraint(true),
                newRestrictedDurationConstraint("2024-01-01T00:00:00", "2024-03-31T23:59:59"),
                newRestrictedNumberConstraint(10)));
        return result;
    }

    private RestrictedNumberConstraint newRestrictedNumberConstraint(int maxCount) {
        RestrictedNumberConstraint result = new RestrictedNumberConstraint();
        result.setMaxCount(maxCount);
        return result;
    }

    private UsagePolicyConstraint newDeletionConstraint(boolean afterUse) {
        DeletionConstraint result = new DeletionConstraint();
        result.setAfterUse(afterUse);
        return result;
    }

    private UsagePolicyConstraint newRestrictedDurationConstraint(
            String formDateTimeAsString, String toDateTimeAsString) {
        RestrictedDurationConstraint result = new RestrictedDurationConstraint();
        result.setFromDatetime(DateTimeUtil.dateTimeFromString(formDateTimeAsString));
        result.setToDatetime(DateTimeUtil.dateTimeFromString(toDateTimeAsString));
        return result;
    }

    private AccessPolicyPermission newAccessPolicyPermission(
            String assignee, AccessPolicyAction action, String formDateTimeAsString, String toDateTimeAsString) {
        AccessPolicyPermission result = new AccessPolicyPermission();
        result.setAssignee(assignee);
        result.setAction(action);
        result.setFromDatetime(DateTimeUtil.dateTimeFromString(formDateTimeAsString));
        result.setToDatetime(DateTimeUtil.dateTimeFromString(toDateTimeAsString));
        return result;
    }
}
