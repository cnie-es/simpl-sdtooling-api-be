package eu.europa.ec.simpl.sdtoolingbe.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OfferingTypePropertiesTest {

    private static final String HASH_TEMPLATE_LIST =
            """
					[
					                  {
					                    "key": "application-offeringShape",
					                    "value": "application"
					                  },
					                  {
					                    "key": "data-offeringShape",
					                    "value": "data"
					                  },
					                  {
					                    "key": "infrastructure-offeringShape",
					                    "value": "infrastructure"
					                  }
					                ]
					""";

    @Test
    void testModel() {
        OfferingTypeProperties properties = new OfferingTypeProperties();
        ReflectionTestUtils.setField(properties, "offeringTypeTemplateList", HASH_TEMPLATE_LIST);

        List<Template> expectedTemplateList = Template.loadTemplates(HASH_TEMPLATE_LIST);
        List<Template> actualTemplateList = properties.getOfferingTypeTemplateList();

        for (int i = 0; i < expectedTemplateList.size(); i++) {
            Template expectedTemplate = expectedTemplateList.get(i);
            Template actualTemplate = actualTemplateList.get(i);

            assertEquals(expectedTemplate.getKey(), actualTemplate.getKey());
            assertEquals(expectedTemplate.getValue(), actualTemplate.getValue());
        }
    }
}
