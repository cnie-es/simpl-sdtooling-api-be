package eu.europa.ec.simpl.sdtoolingbe.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TemplateTest {

    @Test
    void testLoadTemplates() {
        String validJson =
                """
                [
                    {"key": "key1", "value": "value1"},
                    {"key": "key2", "value": "value2"}
                ]
                """;

        List<Template> templates = Template.loadTemplates(validJson);

        assertNotNull(templates, "Expected a non-null list");
        assertEquals(2, templates.size(), "Expected two templates in the list");

        Template firstTemplate = templates.get(0);
        assertEquals("key1", firstTemplate.getKey(), "First template key mismatch");
        assertEquals("value1", firstTemplate.getValue(), "First template value mismatch");

        Template secondTemplate = templates.get(1);
        assertEquals("key2", secondTemplate.getKey(), "Second template key mismatch");
        assertEquals("value2", secondTemplate.getValue(), "Second template value mismatch");
    }

    @Test
    void testLoadTemplatesWithEmptyListCauseInvalidJson() {
        String invalidJson = "{ invalid json }";

        List<Template> templates = Template.loadTemplates(invalidJson);

        assertNotNull(templates, "Expected a non-null list even for invalid JSON");
        assertTrue(templates.isEmpty(), "Expected an empty list for invalid JSON");
    }
}
