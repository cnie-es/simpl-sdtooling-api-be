package eu.europa.ec.simpl.sdtoolingbe.properties;

import eu.europa.ec.simpl.data1.common.properties.factory.YamlPropertySourceFactory;
import eu.europa.ec.simpl.sdtoolingbe.dto.HashModel;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:hash-config.yml", factory = YamlPropertySourceFactory.class)
public class HashProperties {

    @Getter
    @Value("${hash.config.algorithm}")
    private String hashAlgorithm;

    @Value("${hash.config.templates}")
    private String hashTemplateList;

    @Value("${hash.config.models}")
    private String hashModelList;

    public List<Template> getHashTemplateList() {
        return Template.loadTemplates(hashTemplateList);
    }

    public List<HashModel> getHashModelList() {
        return HashModel.loadModels(hashModelList);
    }

    public String getValueFromKey(String key) {
        Template hashConfig = null;
        Optional<Template> botConfigOptional = getHashTemplateList().stream()
                .filter(x -> x.getKey().equalsIgnoreCase(key))
                .findFirst();
        if (botConfigOptional.isPresent()) {
            hashConfig = botConfigOptional.get();
        }

        if (hashConfig != null) {
            return hashConfig.getValue();
        }

        return null;
    }
}
