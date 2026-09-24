package eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class AccessPolicyRequest {

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "access permissions")
    @NotEmpty(message = "at least one permission must be added")
    @Valid
    public List<AccessPolicyPermission> permissions;
}
