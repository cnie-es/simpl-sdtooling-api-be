package eu.europa.ec.simpl.sdtoolingbe.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("removal")
class ShaclFileUtilTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        Path shapesDir = tempDir.resolve("data").resolve("shapes");
        Files.createDirectories(shapesDir);
    }

    // Tests for getShapesDir()
    @Test
    void testGetShapesDir() {
        File result = ShaclFileUtil.getShapesDir();
        assertNotNull(result);
        assertEquals(Paths.get("data", "shapes").toFile(), result);
    }

    // Tests for getShapesFilePath() - Valid cases
    @Test
    void testGetShapesFilePath_ValidJsonFile() {
        assertDoesNotThrow(() -> {
            Path result = ShaclFileUtil.getShapesFilePath("eco1", "type1", "file.json");
            assertNotNull(result);
            assertTrue(result.toString().contains("eco1"));
            assertTrue(result.toString().contains("type1"));
            assertTrue(result.toString().contains("file.json"));
        });
    }

    @Test
    void testGetShapesFilePath_ValidTtlFile() {
        assertDoesNotThrow(() -> {
            Path result = ShaclFileUtil.getShapesFilePath("eco2", "type2", "schema.ttl");
            assertNotNull(result);
            assertTrue(result.toString().endsWith("schema.ttl"));
        });
    }

    @Test
    void testGetShapesFilePath_WithHyphensAndUnderscores() {
        assertDoesNotThrow(() -> {
            Path result = ShaclFileUtil.getShapesFilePath("eco-system_1", "schema-type_2", "file_name-123.json");
            assertNotNull(result);
        });
    }

    @Test
    void testGetShapesFilePath_WithDots() {
        assertDoesNotThrow(() -> {
            Path result = ShaclFileUtil.getShapesFilePath("eco.system", "schema.type", "file.name.json");
            assertNotNull(result);
        });
    }

    // Tests for getShapesFilePath() - Null/Empty validation
    @Test
    void testGetShapesFilePath_NullEcosystem() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath(null, "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid ecosystem"));
    }

    @Test
    void testGetShapesFilePath_EmptyEcosystem() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("", "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid ecosystem"));
    }

    @Test
    void testGetShapesFilePath_NullSchemaType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", null, "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid schemaType"));
    }

    @Test
    void testGetShapesFilePath_EmptySchemaType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "", "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid schemaType"));
    }

    @Test
    void testGetShapesFilePath_NullFilename() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", null);
        });
        assertTrue(exception.getMessage().contains("Invalid filename"));
    }

    @Test
    void testGetShapesFilePath_EmptyFilename() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", "");
        });
        assertTrue(exception.getMessage().contains("Invalid filename"));
    }

    // Tests for path traversal attempts
    @Test
    void testGetShapesFilePath_PathTraversalInEcosystem() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("../etc", "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid ecosystem"));
    }

    @Test
    void testGetShapesFilePath_PathTraversalInSchemaType() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "../../passwd", "file.json");
        });
        assertTrue(exception.getMessage().contains("Invalid schemaType"));
    }

    @Test
    void testGetShapesFilePath_PathTraversalInFilename() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", "../../../etc/passwd");
        });
        assertTrue(exception.getMessage().contains("Invalid filename"));
    }

    @Test
    void testGetShapesFilePath_ForwardSlashInEcosystem() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco/system", "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("path traversal"));
    }

    @Test
    void testGetShapesFilePath_BackslashInSchemaType() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "sche\\ma", "file.json");
        });
        assertTrue(exception.getMessage().contains("path traversal"));
    }

    // Tests for invalid characters
    @Test
    void testGetShapesFilePath_InvalidCharactersInEcosystem() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco$system", "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("alphanumeric"));
    }

    @Test
    void testGetShapesFilePath_SpecialCharactersInSchemaType() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type@123", "file.json");
        });
        assertTrue(exception.getMessage().contains("alphanumeric"));
    }

    @Test
    void testGetShapesFilePath_StartingWithDot() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath(".hidden", "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("alphanumeric"));
    }

    // Tests for file extension validation
    @Test
    void testGetShapesFilePath_InvalidExtension() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", "file.txt");
        });
        assertTrue(exception.getMessage().contains("file extension not allowed"));
    }

    @Test
    void testGetShapesFilePath_NoExtension() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", "file");
        });
        assertTrue(exception.getMessage().contains("file extension not allowed"));
    }

    @Test
    void testGetShapesFilePath_ExecutableExtension() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", "malware.exe");
        });
        assertTrue(exception.getMessage().contains("file extension not allowed"));
    }

    @Test
    void testGetShapesFilePath_CaseInsensitiveExtension() {
        assertDoesNotThrow(() -> {
            Path result = ShaclFileUtil.getShapesFilePath("eco", "type", "file.JSON");
            assertNotNull(result);
        });
    }

    // Tests for length validation
    @Test
    void testGetShapesFilePath_ExceedsMaxLengthEcosystem() {
        String longName = "a".repeat(256);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath(longName, "type", "file.json");
        });
        assertTrue(exception.getMessage().contains("exceeds maximum length"));
    }

    @Test
    void testGetShapesFilePath_ExceedsMaxLengthSchemaType() {
        String longName = "b".repeat(256);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ShaclFileUtil.getShapesFilePath("eco", longName, "file.json");
        });
        assertTrue(exception.getMessage().contains("exceeds maximum length"));
    }

    @Test
    void testGetShapesFilePath_MaxLengthBoundary() {
        String maxName = "a".repeat(250) + ".json";
        assertDoesNotThrow(() -> {
            ShaclFileUtil.getShapesFilePath("eco", "type", maxName);
        });
    }
}
