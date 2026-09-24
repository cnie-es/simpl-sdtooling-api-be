package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
public class UsagePolicyPermission {

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "permission assignee", example = "Consumer")
    @NotBlank(message = "assignee must be valorized")
    public String assignee;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, description = "type of permission action (default USE)")
    @JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    @NotNull(message = "permission action must be valorized")
    public UsagePolicyAction action = UsagePolicyAction.USE;

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "permission contraints")
    @NotEmpty(message = "at least one constraint must be added")
    @UniqueElements(message = "duplicated constraint types are not allowed")
    @Valid
    public List<UsagePolicyConstraint> constraints;
}
