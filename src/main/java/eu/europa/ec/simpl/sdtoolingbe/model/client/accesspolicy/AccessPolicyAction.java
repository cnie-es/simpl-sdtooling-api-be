package eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy;

public enum AccessPolicyAction {
    SEARCH("search"),
    CONSUME("consume"),
    RESTRICTED_CONSUME("restrictedConsume");

    private String actionName;

    AccessPolicyAction(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}
