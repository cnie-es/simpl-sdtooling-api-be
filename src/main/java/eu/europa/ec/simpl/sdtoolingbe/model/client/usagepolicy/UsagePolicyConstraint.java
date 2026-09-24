package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DeletionConstraint.class, name = DeletionConstraint.TYPE),
    @JsonSubTypes.Type(value = RestrictedDurationConstraint.class, name = RestrictedDurationConstraint.TYPE),
    @JsonSubTypes.Type(value = RestrictedNumberConstraint.class, name = RestrictedNumberConstraint.TYPE)
})
@RequiredArgsConstructor
public abstract class UsagePolicyConstraint {

    private final String type;

    public abstract void addOdrlConstraints(OdrlPermission permission);

    @EqualsAndHashCode.Include
    public String getType() {
        return type;
    }
}
