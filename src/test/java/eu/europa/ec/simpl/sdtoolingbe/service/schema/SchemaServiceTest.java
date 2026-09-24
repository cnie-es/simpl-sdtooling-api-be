package eu.europa.ec.simpl.sdtoolingbe.service.schema;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.exception.BadRequestException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("removal")
class SchemaServiceTest {

    private Map<String, Map<String, List<String>>> ecosystemToCategoryToShaclFileMap;

    @InjectMocks
    private SchemaServiceImpl schemaService;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ecosystemToCategoryToShaclFileMap = new HashMap<>();
        Map<String, List<String>> categoryToFiles = new HashMap<>();
        categoryToFiles.put("category1", List.of("file1.ttl", "file2.ttl"));
        ecosystemToCategoryToShaclFileMap.put("ecosystem1", categoryToFiles);
    }

    @Test
    void testGetTtlFileContentWitEcosystemNotFound() {
        // Arrange
        String ecosystem = "nonexistent";
        String name = "data-offeringShape";
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);

        assertThrows(IllegalStateException.class, () -> schemaService.getTtlFileContent(ecosystem, name));
    }

    @Test
    void testGetTtlFileContentWithFileNotFound() {
        // Arrange
        String ecosystem = "simpl";
        String name = "nonexistent.json";
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);

        assertThrows(IllegalStateException.class, () -> schemaService.getTtlFileContent(ecosystem, name));
    }

    @Test
    void testGetTtlFileContentSuccess() throws Exception {
        String ecosystem = "ecosystem1";
        String filename = "file1.ttl";
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);
        // Mock static method
        try (var shaclFileUtilMock = mockStatic(eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil.class);
                var filesMock = mockStatic(Files.class)) {
            Path fakePath = Path.of("/fake/path/file1.ttl");
            shaclFileUtilMock
                    .when(() -> eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil.getShapesFilePath(
                            ecosystem, "category1", filename))
                    .thenReturn(fakePath);
            filesMock.when(() -> Files.readString(fakePath)).thenReturn("file content");
            String result = schemaService.getTtlFileContent(ecosystem, filename);
            assertEquals("file content", result);
        }
    }

    @Test
    void testGetTtlFileContentIOException() throws Exception {
        String ecosystem = "ecosystem1";
        String filename = "file1.ttl";
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);
        try (var shaclFileUtilMock = mockStatic(eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil.class);
                var filesMock = mockStatic(Files.class)) {
            Path fakePath = Path.of("/fake/path/file1.ttl");
            shaclFileUtilMock
                    .when(() -> eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil.getShapesFilePath(
                            ecosystem, "category1", filename))
                    .thenReturn(fakePath);
            filesMock.when(() -> Files.readString(fakePath)).thenThrow(new IllegalArgumentException("bad path"));
            assertThrows(IllegalArgumentException.class, () -> schemaService.getTtlFileContent(ecosystem, filename));
        }
    }

    @Test
    void testGetAvailableTtlFilesGlobalReturnsCorrectMap() {
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);
        Map<String, Map<String, List<String>>> result = schemaService.getAvailableTtlFiles();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("ecosystem1"));
        assertEquals(List.of("file1.ttl", "file2.ttl"), result.get("ecosystem1").get("category1"));
    }

    @Test
    void testGetAvailableTtlFilesGlobalReturnsEmptyMapIfNull() {
        ReflectionTestUtils.setField(schemaService, "ecosystemToCategoryToShaclFileMap", null);
        Map<String, Map<String, List<String>>> result = schemaService.getAvailableTtlFiles();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAvailableTtlFilesByEcosystemReturnsCorrectMap() {
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);
        Map<String, List<String>> result = schemaService.getAvailableTtlFiles("ecosystem1");
        assertEquals(1, result.size());
        assertTrue(result.containsKey("category1"));
        assertEquals(List.of("file1.ttl", "file2.ttl"), result.get("category1"));
    }

    @Test
    void testGetAvailableTtlFilesByEcosystemReturnsEmptyMapIfNull() {
        ReflectionTestUtils.setField(schemaService, "ecosystemToCategoryToShaclFileMap", null);
        Map<String, List<String>> result = schemaService.getAvailableTtlFiles("ecosystem1");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAvailableTtlFilesByEcosystemReturnsEmptyMapIfEcosystemNotPresent() {
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);
        Map<String, List<String>> result = schemaService.getAvailableTtlFiles("notfound");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTtlFileContentFileNotInAnyCategory() {
        // Arrange
        String ecosystem = "ecosystem1";
        String filename = "notpresent.ttl"; // not in any category list
        ReflectionTestUtils.setField(
                schemaService, "ecosystemToCategoryToShaclFileMap", ecosystemToCategoryToShaclFileMap);

        assertThrows(BadRequestException.class, () -> schemaService.getTtlFileContent(ecosystem, filename));
    }
}
