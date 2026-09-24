package eu.europa.ec.simpl.sdtoolingbe.properties;

import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.properties.factory.YamlPropertySourceFactory;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "resource-address.config")
@PropertySource(value = "classpath:resourceAddress-config.yml", factory = YamlPropertySourceFactory.class)
public class ResourceAddressProperties {

    @Setter
    @Getter
    private Map<OfferType, Map<String, List<Template>>> templateMap;

    public ResourceAddressProperties() {
        // Default constructor for Spring configuration properties
    }

    public String getSharingMethodId(String templateId) {
        if (templateMap == null) {
            return null;
        }
        return getTemplateMap().values().stream()
                .flatMap(providerMap -> providerMap.entrySet().stream())
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> containsTemplateId(entry.getValue(), templateId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private static boolean containsTemplateId(List<Template> templates, String templateId) {
        return templates.stream().anyMatch(template -> template.getId().equals(templateId));
    }
}
