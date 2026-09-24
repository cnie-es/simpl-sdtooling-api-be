package eu.europa.ec.simpl.sdtoolingbe.enumeration;

import eu.europa.ec.simpl.data1.common.enumeration.ErrorType;
import lombok.Getter;

public enum AppErrorType implements ErrorType {
    REMOTE_ASSET_ORCHESTRATOR_ERROR("Remote Asset Orchestrator Error"),
    UNEXPECTED_ASSET_ORCHESTRATOR_RESPONSE_ERROR("Unexpected Asset Orchestrator Response Error");

    @Getter
    private final String problemTitle;

    AppErrorType(String problemTitle) {
        this.problemTitle = problemTitle;
    }
}
