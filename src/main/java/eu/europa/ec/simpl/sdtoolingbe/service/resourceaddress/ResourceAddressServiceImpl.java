package eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import eu.europa.ec.simpl.sdtoolingbe.properties.ResourceAddressProperties;
import eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter.ConnectorAdapterService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class ResourceAddressServiceImpl implements ResourceAddressService {

    private static final String RESOURCE_PATH_TEMPLATE = "resourceaddress/template/TEMPLATE_";

    private static final String RESOURCE_PATH_UI_SCHEMA = "resourceaddress/ui-schema/UI_SCHEMA_";

    private static final String RESOURCE_NAME_SUFFIX = "SOURCE";

    private static final String RESOURCE_EXT = ".json";

    private final ResourceAddressProperties resourceAddressProperties;

    private final ConnectorAdapterService connectorAdapterService;

    @Override
    public List<String> getResourceSharingMethods(OfferType offerType) {
        log.debug("getResourceSharingMethods() for offerType {}", offerType);
        Map<String, List<Template>> map =
                resourceAddressProperties.getTemplateMap().getOrDefault(offerType, Map.of());
        return new ArrayList<>(map.keySet());
    }

    @Override
    public List<Template> getSourceAddressTemplates(OfferType offerType, String sharingMethodId) {
        log.debug("getSourceAddressTemplates() for offerType {} and sharingMethodId '{}'", offerType, sharingMethodId);
        Map<String, List<Template>> map =
                resourceAddressProperties.getTemplateMap().getOrDefault(offerType, Map.of());
        return map.getOrDefault(sharingMethodId, List.of()).stream().toList();
    }

    @Override
    public String getSourceAddressSchema(String templateId) throws ResourceAddressNotFoundException, IOException {
        log.debug("getSourceAddressSchema() for templateId '{}'", templateId);
        Set<OfferType> offerTypes = resourceAddressProperties.getTemplateMap().keySet();
        Set<String> sharingMethods;
        List<Template> templates;
        Template template;
        for (OfferType offerType : offerTypes) {
            sharingMethods =
                    resourceAddressProperties.getTemplateMap().get(offerType).keySet();
            for (String sharingMethod : sharingMethods) {
                templates = resourceAddressProperties
                        .getTemplateMap()
                        .get(offerType)
                        .get(sharingMethod);
                template = findTemplateById(templateId, templates);
                if (template != null) {
                    return getSchemaContent(offerType, sharingMethod, template);
                }
            }
        }
        throw new ResourceAddressNotFoundException(
                "Template not found", "No template found for templateId " + templateId);
    }

    @Override
    public String getSourceAddressUiSchema(String templateId) throws ResourceAddressNotFoundException, IOException {
        log.debug("getSourceAddressUiSchema() for templateId '{}'", templateId);
        Set<OfferType> offerTypes = resourceAddressProperties.getTemplateMap().keySet();
        Set<String> sharingMethods;
        List<Template> templates;
        Template template;
        for (OfferType offerType : offerTypes) {
            sharingMethods =
                    resourceAddressProperties.getTemplateMap().get(offerType).keySet();
            for (String sharingMethod : sharingMethods) {
                templates = resourceAddressProperties
                        .getTemplateMap()
                        .get(offerType)
                        .get(sharingMethod);
                template = findTemplateById(templateId, templates);
                if (template != null) {
                    return getUiSchemaContent(offerType, sharingMethod, template);
                }
            }
        }
        throw new ResourceAddressNotFoundException(
                "Template UI schema not found", "No template UI schema found for templateId " + templateId);
    }

    @Override
    public ResourceAddress getResourceAddress(String bearerToken, String assetId) {
        log.debug(
                "getResourceAddress(): invoking connectorAdapterService.getResourceAddress " + "for assetId '{}'",
                assetId);
        return connectorAdapterService.getResourceAddress(bearerToken, assetId);
    }

    private static String getSchemaContent(OfferType offerType, String sharingMethod, Template template)
            throws IOException {
        String resourcePath = RESOURCE_PATH_TEMPLATE + offerType.name() + "_" + sharingMethod + "_"
                + RESOURCE_NAME_SUFFIX + "_" + template.getId() + RESOURCE_EXT;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("No template schema resource found with path '" + resourcePath + "'");
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static String getUiSchemaContent(OfferType offerType, String sharingMethod, Template template)
            throws IOException {
        String resourcePath = RESOURCE_PATH_UI_SCHEMA + offerType.name() + "_" + sharingMethod + "_"
                + RESOURCE_NAME_SUFFIX + "_" + template.getId() + RESOURCE_EXT;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException(
                        "No template UI schema resource found with path '" + resourcePath + "'");
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static Template findTemplateById(String templateId, List<Template> templates) {
        for (Template template : templates) {
            if (template.getId().equals(templateId)) {
                return template;
            }
        }
        return null;
    }
}
