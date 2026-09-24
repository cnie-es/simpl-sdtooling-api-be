package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlConstraint;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperand;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperator;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class RestrictedNumberConstraint extends UsagePolicyConstraint {

    public static final String TYPE = "RestrictedNumber";

    @Schema(requiredMode = RequiredMode.REQUIRED, description = "restricted number max usage count", example = "10")
    @DecimalMin(value = "1", message = "maxCount must be > 0")
    public int maxCount;

    public RestrictedNumberConstraint() {
        super(TYPE);
    }

    @Schema(
            requiredMode = RequiredMode.REQUIRED,
            description = "type of the constraint",
            example = RestrictedNumberConstraint.TYPE)
    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public void addOdrlConstraints(OdrlPermission permission) {
        OdrlConstraint item =
                new OdrlConstraint(OdrlOperand.COUNT, OdrlOperator.LESS_OR_EQUAL, String.valueOf(maxCount));
        permission.add(item);
    }
}
