package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlConstraint;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperand;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlOperator;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class DeletionConstraint extends UsagePolicyConstraint {

    public static final String TYPE = "Deletion";

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, description = "after use flag (default is false)")
    public boolean afterUse;

    public DeletionConstraint() {
        super(TYPE);
    }

    @Schema(
            requiredMode = RequiredMode.REQUIRED,
            description = "type of the constraint",
            example = DeletionConstraint.TYPE)
    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public void addOdrlConstraints(OdrlPermission permission) {
        if (afterUse) {
            OdrlConstraint item = new OdrlConstraint(OdrlOperand.DELETION, OdrlOperator.EQUAL, "after_use");
            permission.add(item);
        }
    }
}
