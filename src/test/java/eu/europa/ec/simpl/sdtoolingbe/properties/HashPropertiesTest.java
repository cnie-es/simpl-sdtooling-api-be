package eu.europa.ec.simpl.sdtoolingbe.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.europa.ec.simpl.sdtoolingbe.dto.HashModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class HashPropertiesTest {

    private static final String HASH_ALGORITHM = "algorithm";

    private static final String HASH_TEMPLATE_LIST =
            """
                    [
                    	{
                    		"key": "Contract Template 1",
                    			"value": "https://files.dev.simpleurope.eu/static/pdf/ContractTemplate1.pdf"
                    	},
                    	{
                    		"key": "Billing Schema 1",
                    			"value": "https://files.dev.simpleurope.eu/static/pdf/BillingSchema1.pdf"
                    	}
                    ]
                    """;

    private static final String HASH_MODEL_LIST =
            """
                    	[
                    	                {
                    	                  "hashObj": "contractTemplate",
                    	                  "hashDoc": "contractTemplateDocument",
                    	                  "hashAlg": "contractTemplateHashAlg",
                    	                  "hashValue": "contractTemplateHashValue",
                    	                  "hashUrl": "contractTemplateURL"
                    	                },
                    	                {
                    	                  "hashObj": "billingSchema",
                    	                  "hashDoc": "billingSchemaDocument",
                    	                  "hashAlg": "billingSchemaHashAlg",
                    	                  "hashValue": "billingSchemaHashValue",
                    	                  "hashUrl": "billingSchemaURL"
                    	                }
                    	]
                    """;

    private static final String INVALID_JSON =
            """
                    	[
                    	                {
                                          ,
                    	                }
                    	]
                    """;

    @Test
    void testModel() {
        HashProperties properties = new HashProperties();

        ReflectionTestUtils.setField(properties, "hashAlgorithm", HASH_ALGORITHM);
        ReflectionTestUtils.setField(properties, "hashModelList", HASH_MODEL_LIST);
        ReflectionTestUtils.setField(properties, "hashTemplateList", HASH_TEMPLATE_LIST);

        assertEquals(HASH_ALGORITHM, properties.getHashAlgorithm());

        List<Template> expectedTemplateList = Template.loadTemplates(HASH_TEMPLATE_LIST);
        List<Template> actualTemplateList = properties.getHashTemplateList();

        for (int i = 0; i < expectedTemplateList.size(); i++) {
            Template expectedTemplate = expectedTemplateList.get(i);
            Template actualTemplate = actualTemplateList.get(i);

            assertEquals(expectedTemplate.getKey(), actualTemplate.getKey());
            assertEquals(expectedTemplate.getValue(), actualTemplate.getValue());
        }

        List<HashModel> expectedModelList = HashModel.loadModels(HASH_MODEL_LIST);
        List<HashModel> actualModelList = properties.getHashModelList();

        for (int i = 0; i < expectedModelList.size(); i++) {
            HashModel expectedModel = expectedModelList.get(i);
            HashModel actualModel = actualModelList.get(i);

            assertEquals(expectedModel.getHashDoc(), actualModel.getHashDoc());
            assertEquals(expectedModel.getHashUrl(), actualModel.getHashUrl());
            assertEquals(expectedModel.getHashValue(), actualModel.getHashValue());
            assertEquals(expectedModel.getHashAlg(), actualModel.getHashAlg());
            assertEquals(expectedModel.getHashObj(), actualModel.getHashObj());
        }
    }

    @Test
    void testHashModelListInvalid() {
        List<HashModel> expectedModelList = HashModel.loadModels(INVALID_JSON);
        assertTrue(expectedModelList.isEmpty());
    }

    @Test
    void testTemplateListInvalid() {
        List<Template> expectedTemplateList = Template.loadTemplates(INVALID_JSON);
        assertTrue(expectedTemplateList.isEmpty());
    }
}
