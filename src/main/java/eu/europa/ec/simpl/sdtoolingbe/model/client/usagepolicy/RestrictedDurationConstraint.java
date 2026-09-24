package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.europa.ec.simpl.data1.common.json.ISO8601DateJsonDeserializer;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlConstraint;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperand;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperator;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import eu.europa.ec.simpl.data1.common.util.DateTimeUtil;
import eu.europa.ec.simpl.data1.common.validation.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ValidDateRange(
        start = "fromDatetime",
        end = "toDatetime",
        message = "invalid data range: fromDatetime cannot be greater than toDatetime")
public class RestrictedDurationConstraint extends UsagePolicyConstraint {

    public static final String TYPE = "RestrictedDuration";

    @Schema(
            requiredMode = RequiredMode.NOT_REQUIRED,
            description = "duration start datetime in ISO8601 format with optional timezone (default is GMT)",
            type = "string",
            example = "2024-08-01T00:00:00Z")
    @JsonDeserialize(using = ISO8601DateJsonDeserializer.class)
    // @NotNull(message="permission fromDatetime must be valorized") opzionale
    public Date fromDatetime;

    @Schema(
            requiredMode = RequiredMode.NOT_REQUIRED,
            description = "duration end datetime in ISO8601 format with optional timezone (default is GMT)",
            type = "string",
            example = "2024-08-31T23:59:59Z",
            examples = {"2024-08-31T23:59:59", "2024-08-31T23:59:59Z", "2024-08-31T23:59:59+0100"})
    @JsonDeserialize(using = ISO8601DateJsonDeserializer.class)
    // @NotNull(message="permission toDatetime must be valorized") opzionale
    public Date toDatetime;

    public RestrictedDurationConstraint() {
        super(TYPE);
    }

    @Schema(
            requiredMode = RequiredMode.REQUIRED,
            description = "type of the constraint",
            example = RestrictedDurationConstraint.TYPE)
    @Override
    public String getType() {
        return super.getType();
    }

    public boolean hasFromDate() {
        return fromDatetime != null;
    }

    public boolean hasToDate() {
        return toDatetime != null;
    }

    @Override
    public void addOdrlConstraints(OdrlPermission permission) {
        OdrlConstraint item;
        if (fromDatetime != null) {
            item = new OdrlConstraint(
                    OdrlOperand.DATETIME,
                    OdrlOperator.GREAT_OR_EQUAL,
                    DateTimeUtil.dateTimeToString(fromDatetime, null));
            permission.add(item);
        }

        if (toDatetime != null) {
            item = new OdrlConstraint(
                    OdrlOperand.DATETIME, OdrlOperator.LESS_OR_EQUAL, DateTimeUtil.dateTimeToString(toDatetime, null));
            permission.add(item);
        }
    }
}
