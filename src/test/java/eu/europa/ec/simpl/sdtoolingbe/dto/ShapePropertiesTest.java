package eu.europa.ec.simpl.sdtoolingbe.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ShapePropertiesTest {

    private static final String PREFIX = "ex";
    private static final String PATH_URI = "http://example.org/path";
    private static final String CLASS_URI = "http://example.org/class";
    private static final String VALUE_1 = "value1";
    private static final String CONFIG_VALUE = "config";
    private static final String SIMPLE_PATH_URI = "http://example.org/simplePath";
    private static final String SIMPLE_CLASS_URI = "http://example.org/simpleClass";
    private static final String LANG_EN = "en";
    private static final String DATATYPE_STRING = "string";
    private static final String DATATYPE_INTEGER = "integer";
    private static final String DESCRIPTION_TEXT = "A description";
    private static final String EXAMPLE_TEXT = "Example value";
    private static final String CHILD_ID = "childId";
    private static final String NAME_FULL = "testName";
    private static final String NAME_SIMPLE = "simpleName";

    @Test
    void testFullConstructor() {
        ClassConstraint path = new ClassConstraint(PREFIX, PATH_URI);
        ClassConstraint clazz = new ClassConstraint(PREFIX, CLASS_URI);
        Map<String, String> datatype = Map.of(LANG_EN, DATATYPE_STRING);
        List<ClassConstraint> in = List.of(new ClassConstraint(PREFIX, VALUE_1));
        List<ClassConstraint> configure = List.of(new ClassConstraint(PREFIX, CONFIG_VALUE));
        List<ConstraintOption> validations = List.of(new ConstraintOption("minLength", "1"));
        Map<String, String> description = Map.of(LANG_EN, DESCRIPTION_TEXT);

        ShapeProperties props = new ShapeProperties(
                path,
                NAME_FULL,
                datatype,
                clazz,
                1,
                5,
                in,
                2,
                validations,
                CHILD_ID,
                description,
                EXAMPLE_TEXT,
                configure,
                List.of());

        assertEquals(path, props.getPath());
        assertEquals(NAME_FULL, props.getName());
        assertEquals(datatype, props.getDatatype());
        assertEquals(clazz, props.getClazz());
        assertEquals(1, props.getMinCount());
        assertEquals(5, props.getMaxCount());
        assertEquals(in, props.getIn());
        assertEquals(2, props.getOrder());
        assertEquals(validations, props.getValidations());
        assertEquals(CHILD_ID, props.getChildren());
        assertEquals(description, props.getDescription());
        assertEquals(EXAMPLE_TEXT, props.getExample());
        assertEquals(configure, props.getConfigure());
        assertTrue(props.getOr().isEmpty());
    }

    @Test
    void testSimpleConstructor() {
        ClassConstraint path = new ClassConstraint(PREFIX, SIMPLE_PATH_URI);
        ClassConstraint clazz = new ClassConstraint(PREFIX, SIMPLE_CLASS_URI);
        Map<String, String> datatype = Map.of(LANG_EN, DATATYPE_INTEGER);

        ShapeProperties props = new ShapeProperties(path, NAME_SIMPLE, datatype, clazz, 0, 3);

        assertEquals(path, props.getPath());
        assertEquals(NAME_SIMPLE, props.getName());
        assertEquals(datatype, props.getDatatype());
        assertEquals(clazz, props.getClazz());
        assertEquals(0, props.getMinCount());
        assertEquals(3, props.getMaxCount());

        assertNull(props.getIn());
        assertNull(props.getOrder());
        assertNull(props.getValidations());
        assertNull(props.getChildren());
        assertNull(props.getDescription());
        assertNull(props.getExample());
        assertNull(props.getConfigure());
        assertNull(props.getOr());
    }
}
