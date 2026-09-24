package eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPermission;
import java.util.Date;
import org.junit.jupiter.api.Test;

class RestrictedDurationConstraintTest {

    @Test
    void testHasFromDate() {
        RestrictedDurationConstraint constraint = new RestrictedDurationConstraint();
        assertFalse(constraint.hasFromDate());

        constraint.fromDatetime = new Date();
        assertTrue(constraint.hasFromDate());
    }

    @Test
    void testHasToDate() {
        RestrictedDurationConstraint constraint = new RestrictedDurationConstraint();
        assertFalse(constraint.hasToDate());

        constraint.toDatetime = new Date();
        assertTrue(constraint.hasToDate());
    }

    @Test
    void testAddOdrlConstraints() {
        RestrictedDurationConstraint constraint = new RestrictedDurationConstraint();
        OdrlPermission permission = new OdrlPermission();

        constraint.addOdrlConstraints(permission);
        assertTrue(permission.getConstraints().isEmpty());

        constraint.fromDatetime = new Date();
        constraint.toDatetime = new Date();

        constraint.addOdrlConstraints(permission);
        assertEquals(2, permission.getConstraints().size());
    }
}
