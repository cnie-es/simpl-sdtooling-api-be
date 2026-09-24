package eu.europa.ec.simpl.sdtoolingbe.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class VicShapeTest {

    private static final String SCHEMA = "https://example.org/schema";
    private static final String PREFIX = "ex";
    private static final String CLASS_NAME = "ExampleClass";
    private static final String PATH_VALUE = "http://example.org/path";
    private static final String CLASS_URI = "http://example.org/class";
    private static final String NAME = "propertyName";

    @Test
    void testConstructorWithConstraints() {
        ShapeProperties shapeProp = new ShapeProperties(
                new ClassConstraint(PREFIX, PATH_VALUE),
                NAME,
                Collections.emptyMap(),
                new ClassConstraint(PREFIX, CLASS_URI),
                1,
                1);

        List<ShapeProperties> constraints = List.of(shapeProp);

        VicShape vicShape = new VicShape(constraints, SCHEMA, PREFIX, CLASS_NAME);

        assertEquals(SCHEMA, vicShape.getSchema());
        assertEquals(PREFIX, vicShape.getTargetClassPrefix());
        assertEquals(CLASS_NAME, vicShape.getTargetClassName());
        assertEquals(1, vicShape.getConstraints().size());
        assertEquals(shapeProp, vicShape.getConstraints().get(0));
    }

    @Test
    void testConstructorWithNullConstraints() {
        VicShape vicShape = new VicShape(null, SCHEMA, PREFIX, CLASS_NAME);

        assertEquals(SCHEMA, vicShape.getSchema());
        assertEquals(PREFIX, vicShape.getTargetClassPrefix());
        assertEquals(CLASS_NAME, vicShape.getTargetClassName());
        assertNotNull(vicShape.getConstraints());
        assertTrue(vicShape.getConstraints().isEmpty());
    }

    @Test
    void testConstraintsAreUnmodifiable() {
        ShapeProperties shapeProp = new ShapeProperties(
                new ClassConstraint(PREFIX, PATH_VALUE),
                NAME,
                Collections.emptyMap(),
                new ClassConstraint(PREFIX, CLASS_URI),
                1,
                1);

        VicShape vicShape = new VicShape(List.of(shapeProp), SCHEMA, PREFIX, CLASS_NAME);

        assertThrows(UnsupportedOperationException.class, () -> {
            vicShape.getConstraints().add(shapeProp);
        });
    }
}
