package eu.europa.ec.simpl.sdtoolingbe.properties;

import eu.europa.ec.simpl.data1.common.properties.factory.YamlPropertySourceFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:offeringType-config.yml", factory = YamlPropertySourceFactory.class)
public class OfferingTypeProperties {

    @Value("${offeringType.config.templates}")
    private String offeringTypeTemplateList;

    public List<Template> getOfferingTypeTemplateList() {
        return Template.loadTemplates(offeringTypeTemplateList);
    }

    public String getValueFromKey(String key) {
        if (key == null) {
            return null;
        }
        Optional<Template> botConfigOptional = getOfferingTypeTemplateList().stream()
                .filter(x -> key.startsWith(x.getKey()))
                .findFirst();
        return botConfigOptional.map(Template::getValue).orElse(null);
    }
}
