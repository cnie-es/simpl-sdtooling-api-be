package eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.europa.ec.simpl.data1.common.json.ISO8601DateJsonDeserializer;
import eu.europa.ec.simpl.data1.common.validation.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
@ValidDateRange(
        start = "fromDatetime",
        end = "toDatetime",
        message = "invalid data range: fromDatetime cannot be greater than toDatetime")
public class AccessPolicyPermission {

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "permission assignee")
    @NotBlank(message = "assignee must be valorized")
    public String assignee;

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "type of permission action")
    @JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    @NotNull(message = "permission action must be valorized")
    public AccessPolicyAction action;

    @Schema(
            requiredMode = RequiredMode.NOT_REQUIRED,
            description = "permission start datetime in ISO8601 format with optional timezone (default is GMT)",
            type = "string",
            example = "2024-08-01T00:00:00Z")
    @JsonDeserialize(using = ISO8601DateJsonDeserializer.class)
    // @NotNull(message="permission fromDatetime must be valorized") opzionale
    public Date fromDatetime;

    @Schema(
            requiredMode = RequiredMode.NOT_REQUIRED,
            description = "permission end datetime in ISO8601 format with optional timezone (default is GMT)",
            type = "string",
            example = "2024-08-31T23:59:59Z",
            examples = {"2024-08-31T23:59:59", "2024-08-31T23:59:59Z", "2024-08-31T23:59:59+0100"})
    @JsonDeserialize(using = ISO8601DateJsonDeserializer.class)
    // @NotNull(message="permission toDatetime must be valorized") opzionale
    public Date toDatetime;

    public boolean hasFromDate() {
        return fromDatetime != null;
    }

    public boolean hasToDate() {
        return toDatetime != null;
    }
}
