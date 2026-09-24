package eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import eu.europa.ec.simpl.sdtoolingbe.properties.ResourceAddressProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceAddressServiceTest {

    // private static final String TEMPLATE_RESOURCE_PATH = "resourceaddress/template/TEMPLATE_";
    // private static final String SCHEMA_RESOURCE_PATH = "resourceaddress/ui-schema/UI_SCHEMA_";
    // private static final String RESOURCE_NAME_SUFFIX = "SOURCE";
    // private static final String RESOURCE_EXT = ".json";

    private static final String RESOURCE_SHARING_METHOD_IONOS_S3 = "IONOS_S3";
    private static final String RESOURCE_SHARING_METHOD_HTTPDATA_PUSH = "HTTPDATA_PUSH";

    @Mock
    private ResourceAddressProperties resourceAddressProperties;

    @Mock
    private ConnectorAdapterService connectorAdapterService;

    private ResourceAddressServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ResourceAddressServiceImpl(resourceAddressProperties, connectorAdapterService);
    }

    @Test
    void testGetResourceSharingMethods() {
        // Prepare test data
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(
                        Template.builder().id("1").label("S3 Template id 1").build(),
                        Template.builder().id("2").label("S3 Template id 2").build()));
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_HTTPDATA_PUSH,
                List.of(Template.builder().id("3").label("HTTP Template id 3").build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute
        List<String> result = service.getResourceSharingMethods(OfferType.DATA);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.contains(RESOURCE_SHARING_METHOD_IONOS_S3));
        assertTrue(result.contains(RESOURCE_SHARING_METHOD_HTTPDATA_PUSH));
    }

    @Test
    void testGetResourceSharingMethodsForNonExistentOfferType() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, Map.of(RESOURCE_SHARING_METHOD_IONOS_S3, List.of()));

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute
        List<String> result = service.getResourceSharingMethods(OfferType.INFRASTRUCTURE);

        // Verify
        assertEquals(0, result.size());
    }

    @Test
    void testGetSourceAddressTemplates() {
        // Prepare test data
        List<Template> templates = Arrays.asList(
                Template.builder().id("1").label("S3 Template id 1").build(),
                Template.builder().id("2").label("S3 Template id 2").build());

        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(RESOURCE_SHARING_METHOD_IONOS_S3, templates);

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute
        List<Template> result = service.getSourceAddressTemplates(OfferType.DATA, RESOURCE_SHARING_METHOD_IONOS_S3);

        // Verify
        assertEquals(2, result.size());
    }

    @Test
    void testGetSourceAddressTemplatesForNonExistentMethod() {
        // Prepare test data
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder().id("1").label("S3 Template id 1").build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute
        List<Template> result = service.getSourceAddressTemplates(OfferType.DATA, "NON_EXISTENT");

        // Verify
        assertEquals(0, result.size());
    }

    @Test
    void testGetSourceAddressTemplatesForNonExistentOfferType() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, Map.of(RESOURCE_SHARING_METHOD_IONOS_S3, List.of()));

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute
        List<Template> result =
                service.getSourceAddressTemplates(OfferType.INFRASTRUCTURE, RESOURCE_SHARING_METHOD_IONOS_S3);

        // Verify
        assertEquals(0, result.size());
    }

    @Test
    void testGetSourceAddressTemplate() throws ResourceAddressNotFoundException, IOException {
        // Prepare test data
        String templateId = "1";

        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder()
                        .id(templateId)
                        .label("S3 Template id " + templateId)
                        .build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Mock the static method
        try (MockedStatic<IOUtils> ioUtilsMock = Mockito.mockStatic(IOUtils.class)) {
            ioUtilsMock
                    .when(() -> IOUtils.toString(Mockito.any(InputStream.class), Mockito.eq(StandardCharsets.UTF_8)))
                    .thenReturn("template content");

            // Execute
            String result = service.getSourceAddressSchema(templateId);

            // Verify
            assertEquals("template content", result);
        }
    }

    @Test
    void testGetSourceAddressTemplateNotFound() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder().id("1").label("S3 Template id 1").build()));
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute and verify
        assertThrows(ResourceAddressNotFoundException.class, () -> service.getSourceAddressSchema("non-existent"));
    }

    @Test
    void testGetSourceAddressTemplateWithIllegalStateException() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder().id("99").label("S3 Template id 99").build()));
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute and verify
        assertThrows(IllegalStateException.class, () -> service.getSourceAddressSchema("99"));
    }

    @Test
    void testGetSourceAddressUiSchema() throws ResourceAddressNotFoundException, IOException {
        // Prepare test data
        String templateId = "1";

        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder()
                        .id(templateId)
                        .label("S3 Template id " + templateId)
                        .build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Mock the static method
        try (MockedStatic<IOUtils> ioUtilsMock = Mockito.mockStatic(IOUtils.class)) {
            ioUtilsMock
                    .when(() -> IOUtils.toString(Mockito.any(InputStream.class), Mockito.eq(StandardCharsets.UTF_8)))
                    .thenReturn("ui schema content");

            // Execute
            String result = service.getSourceAddressUiSchema(templateId);

            // Verify
            assertEquals("ui schema content", result);
        }
    }

    @Test
    void testGetSourceAddressUiSchemaWithIllegalStateException() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder().id("99").label("S3 Template id 99").build()));
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute and verify
        assertThrows(IllegalStateException.class, () -> service.getSourceAddressUiSchema("99"));
    }

    @Test
    void testGetSourceAddressUiSchemaNotFound() {
        // Prepare test data
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder().id("1").label("S3 Template id 1").build()));
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Execute and verify
        assertThrows(ResourceAddressNotFoundException.class, () -> service.getSourceAddressUiSchema("non-existent"));
    }

    @Test
    void testRestApiTemplateHasHttpDataType() throws ResourceAddressNotFoundException, IOException {
        String templateId = "14";

        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                "REST_API",
                List.of(Template.builder().id(templateId).label("Data Template REST API").build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        String schema = service.getSourceAddressSchema(templateId);

        assertTrue(schema.contains("HttpData"), "type must be HttpData for REST_API source transfers");
        assertTrue(schema.contains("proxyPath"), "proxyPath field must be present for REST_API");
        assertFalse(schema.contains("HttpProxy"), "HttpProxy is a destination type, not a source type");
        assertFalse(schema.contains("authType"), "consumer auth fields must not appear in source template");
        assertFalse(schema.contains("apiKey"), "consumer auth fields must not appear in source template");
        assertFalse(schema.contains("bearerToken"), "consumer auth fields must not appear in source template");
    }

    @Test
    void testRestApiUiSchemaHidesProxyFields() throws ResourceAddressNotFoundException, IOException {
        String templateId = "14";

        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                "REST_API",
                List.of(Template.builder().id(templateId).label("Data Template REST API").build()));

        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);

        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        String uiSchema = service.getSourceAddressUiSchema(templateId);

        assertTrue(uiSchema.contains("HIDE"), "proxy fields must be hidden in ui-schema for REST_API");
        assertFalse(uiSchema.contains("authType"), "consumer auth fields must not appear in ui-schema");
        assertFalse(uiSchema.contains("apiKey"), "consumer auth fields must not appear in ui-schema");
        assertFalse(uiSchema.contains("bearerToken"), "consumer auth fields must not appear in ui-schema");
    }

    @Test
    void testGetSourceAddressSchemaThrowsIOException() {
        // Prepare test data
        String templateId = "1";
        Map<String, List<Template>> sharingMethodMap = new HashMap<>();
        sharingMethodMap.put(
                RESOURCE_SHARING_METHOD_IONOS_S3,
                List.of(Template.builder()
                        .id(templateId)
                        .label("S3 Template id 1")
                        .build()));
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, sharingMethodMap);
        when(resourceAddressProperties.getTemplateMap()).thenReturn(templateMap);

        // Mock IOUtils to throw IOException
        try (MockedStatic<IOUtils> ioUtilsMock = Mockito.mockStatic(IOUtils.class)) {
            ioUtilsMock
                    .when(() -> IOUtils.toString(Mockito.any(InputStream.class), Mockito.eq(StandardCharsets.UTF_8)))
                    .thenThrow(new IOException("Simulated IO error"));

            // Execute and verify
            assertThrows(IOException.class, () -> service.getSourceAddressSchema(templateId));
        }
    }

    @Test
    void testGetResourceAddress() {
        // Prepare test data
        String bearerToken = "Bearer test-token";
        String assetId = "asset-123";
        ResourceAddress expected = ResourceAddress.builder()
                .templateId("template-1")
                .value("{\"url\":\"https://example.com/asset-123\"}")
                .build();

        when(connectorAdapterService.getResourceAddress(bearerToken, assetId)).thenReturn(expected);

        // Execute
        ResourceAddress result = service.getResourceAddress(bearerToken, assetId);

        // Verify
        assertNotNull(result);
        assertEquals(expected.getTemplateId(), result.getTemplateId());
        assertEquals(expected.getValue(), result.getValue());
        verify(connectorAdapterService).getResourceAddress(bearerToken, assetId);
    }

    @Test
    void testGetResourceAddressPropagatesException() {
        // Prepare test data
        String bearerToken = "Bearer test-token";
        String assetId = "non-existent-asset";

        when(connectorAdapterService.getResourceAddress(bearerToken, assetId))
                .thenThrow(new RuntimeException("Asset not found"));

        // Execute and verify
        assertThrows(RuntimeException.class, () -> service.getResourceAddress(bearerToken, assetId));
        verify(connectorAdapterService).getResourceAddress(bearerToken, assetId);
    }
}
