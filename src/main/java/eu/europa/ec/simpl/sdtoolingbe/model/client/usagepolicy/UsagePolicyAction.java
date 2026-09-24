package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

public enum UsagePolicyAction {
    USE("use"),
    DO_NOT_USE_THIS_TYPE(null) // just to avoid sonar blocking issue about enum with single value
;

    private String actionName;

    UsagePolicyAction(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}
