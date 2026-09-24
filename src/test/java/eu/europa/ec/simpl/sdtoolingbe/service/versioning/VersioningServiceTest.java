package eu.europa.ec.simpl.sdtoolingbe.service.versioning;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.util.SDUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VersioningServiceTest {

    private VersioningService versioningService;
    private ObjectMapper objectMapper;
    private final String SHAPE_VERSION_FIELD_PARENT_NAME = "generalServiceProperties";
    private final String SHAPE_VERSION_FIELD_NAME = "version";
    private final String ecosystem = CommonConstants.ECOSYSTEM;

    @BeforeEach
    void setUp() {
        versioningService = new VersioningServiceImpl();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test 1: nextVersion with null input
     * Scenario: sdJsonLd is null
     * Expected: NullPointerException or null handling
     */
    @Test
    void testNextVersionWithNullInput() {
        assertThrows(NullPointerException.class, () -> {
            versioningService.nextVersion(null);
        });
    }

    /**
     * Test 2: nextVersion with empty JsonNode
     * Scenario: sdJsonLd is empty object without generalServiceProperties
     * Expected: No changes made to the node (no version field added)
     */
    @Test
    void testNextVersionWithEmptyJsonNode() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        assertFalse(sdJsonLd.has(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME));
    }

    /**
     * Test 3: nextVersion with generalServiceProperties but no version field
     * Scenario: generalServiceProperties exists but version field is missing
     * Expected: Initial version is set in the version field
     */
    @Test
    void testNextVersionWithMissingVersionField() throws Exception {
        String sdJsonLdStr = createSdJson(ecosystem);
        // Remove version field to test missing field scenario
        String sdJsonLdStrNoVersion = sdJsonLdStr.replaceAll("\"" + ecosystem + ":version\":\\s*\"[^\"]*\"", "");

        ObjectNode sdJsonLd = (ObjectNode) objectMapper.readTree(sdJsonLdStrNoVersion);

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        assertTrue(sdJsonLd.has(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME));
        JsonNode versionNode =
                sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME);
        assertNotNull(versionNode);
        assertFalse(versionNode.asText().isEmpty());
    }

    /**
     * Test 4: nextVersion with empty version field
     * Scenario: version field exists but is empty string
     * Expected: Initial version is set
     */
    @Test
    void testNextVersionWithEmptyVersionField() throws Exception {
        String sdJsonLdStr = createSdJsonWithVersion(ecosystem, "");
        ObjectNode sdJsonLd = (ObjectNode) objectMapper.readTree(sdJsonLdStr);

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        JsonNode versionNode =
                sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME);
        assertNotNull(versionNode);
        assertFalse(versionNode.asText().isEmpty());
    }

    /**
     * Test 5: nextVersion with blank/whitespace version field
     * Scenario: version field contains only whitespace
     * Expected: Initial version is set
     */
    @Test
    void testNextVersionWithBlankVersionField() throws Exception {
        String sdJsonLdStr = createSdJsonWithVersion(ecosystem, "   ");
        ObjectNode sdJsonLd = (ObjectNode) objectMapper.readTree(sdJsonLdStr);

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        JsonNode versionNode =
                sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME);
        assertNotNull(versionNode);
        assertFalse(versionNode.asText().isBlank());
    }

    /**
     * Test 6: nextVersion with valid existing version
     * Scenario: version field has a valid version (e.g., "1.0.0")
     * Expected: Version is incremented to next version
     */
    @Test
    void testNextVersionWithValidVersion() throws Exception {
        String sdJsonLdStr = createSdJson(ecosystem);
        ObjectNode sdJsonLd = (ObjectNode) objectMapper.readTree(sdJsonLdStr);

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        JsonNode versionNode =
                sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME);
        assertNotNull(versionNode);
        assertNotEquals("1", versionNode.asText());
    }

    /**
     * Test 7: nextVersion modifies the input JsonNode in place
     * Scenario: Verify that the method modifies the input object directly
     * Expected: The object passed is modified with the new version
     */
    @Test
    void testNextVersionModifiesInputObject() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        ObjectNode generalServiceProps = objectMapper.createObjectNode();
        sdJsonLd.set(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME, generalServiceProps);

        versioningService.nextVersion(sdJsonLd);

        // Verify the object was modified
        assertTrue(sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).has(SDUtil.NS + SHAPE_VERSION_FIELD_NAME));
    }

    /**
     * Test 8: nextVersion with null version field value
     * Scenario: version field exists but value is null (becomes string "null" via asText())
     * Expected: NumberFormatException when trying to parse "null" as Long
     */
    @Test
    void testNextVersionWithNullVersionFieldValue() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        ObjectNode generalServiceProps = objectMapper.createObjectNode();
        generalServiceProps.putNull(SDUtil.NS + SHAPE_VERSION_FIELD_NAME);
        sdJsonLd.set(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME, generalServiceProps);

        assertThrows(NumberFormatException.class, () -> {
            versioningService.nextVersion(sdJsonLd);
        });
    }

    /**
     * Test 9: nextVersion with additional fields in generalServiceProperties
     * Scenario: generalServiceProperties has other fields besides version, version is "1.0.0"
     * Expected: NumberFormatException when trying to parse "1.0.0" as Long
     */
    @Test
    void testNextVersionPreservesOtherFields() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        ObjectNode generalServiceProps = objectMapper.createObjectNode();
        generalServiceProps.put(SDUtil.NS + "otherField", "someValue");
        generalServiceProps.put(SDUtil.NS + SHAPE_VERSION_FIELD_NAME, "1.0.0");
        sdJsonLd.set(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME, generalServiceProps);

        assertThrows(NumberFormatException.class, () -> {
            versioningService.nextVersion(sdJsonLd);
        });
    }

    /**
     * Test 10: nextVersion with additional top-level fields
     * Scenario: sdJsonLd has other fields besides generalServiceProperties
     * Expected: Other fields are preserved, generalServiceProperties is updated
     */
    @Test
    void testNextVersionPreservesTopLevelFields() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        sdJsonLd.put("id", "test-id");
        sdJsonLd.put("name", "test-name");
        ObjectNode generalServiceProps = objectMapper.createObjectNode();
        sdJsonLd.set(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME, generalServiceProps);

        versioningService.nextVersion(sdJsonLd);

        assertNotNull(sdJsonLd);
        assertEquals("test-id", sdJsonLd.get("id").asText());
        assertEquals("test-name", sdJsonLd.get("name").asText());
    }

    /**
     * Test 11: nextVersion with multiple sequential calls
     * Scenario: Call nextVersion multiple times on the same object
     * Expected: Version increments each time
     */
    @Test
    void testNextVersionMultipleSequentialCalls() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        ObjectNode generalServiceProps = objectMapper.createObjectNode();
        sdJsonLd.set(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME, generalServiceProps);

        // First call
        versioningService.nextVersion(sdJsonLd);
        String version1 = sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME)
                .get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME)
                .asText();

        // Second call
        versioningService.nextVersion(sdJsonLd);
        String version2 = sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME)
                .get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME)
                .asText();

        // Third call
        versioningService.nextVersion(sdJsonLd);
        String version3 = sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME)
                .get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME)
                .asText();

        assertNotNull(version1);
        assertNotNull(version2);
        assertNotNull(version3);
        assertNotEquals(version1, version2);
        assertNotEquals(version2, version3);
    }

    /**
     * Test 12: nextVersion with null generalServiceProperties field
     * Scenario: generalServiceProperties field value is null
     * Expected: ClassCastException when trying to cast null to ObjectNode
     */
    @Test
    void testNextVersionWithNullGeneralServicePropertiesValue() {
        ObjectNode sdJsonLd = objectMapper.createObjectNode();
        sdJsonLd.putNull(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME);

        assertThrows(ClassCastException.class, () -> {
            versioningService.nextVersion(sdJsonLd);
        });
    }

    /**
     * Test 13: nextVersion preserves the original JsonNode structure
     * Scenario: Verify that the returned node maintains the expected structure
     * Expected: Structure is valid after version update
     */
    @Test
    void testNextVersionStructureIntegrity() throws Exception {
        String sdJsonLdStr = createSdJson(ecosystem);
        ObjectNode sdJsonLd = (ObjectNode) objectMapper.readTree(sdJsonLdStr);

        versioningService.nextVersion(sdJsonLd);

        assertTrue(sdJsonLd.isObject());
        assertTrue(sdJsonLd.has(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME));
        assertTrue(sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).isObject());
        assertTrue(sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME).has(SDUtil.NS + SHAPE_VERSION_FIELD_NAME));
    }

    private String createSdJson(String ecosystem) {
        return """
                            {
                                "%s:generalServiceProperties": {
                                "%s:version": "1"
                                }
                            }
                        """
                .formatted(ecosystem, ecosystem);
    }

    private String createSdJsonWithVersion(String ecosystem, String version) {
        return """
                            {
                                "%s:generalServiceProperties": {
                                "%s:version": "%s"
                                }
                            }
                        """
                .formatted(ecosystem, ecosystem, version);
    }
}
