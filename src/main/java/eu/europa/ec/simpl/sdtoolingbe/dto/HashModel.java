package eu.europa.ec.simpl.sdtoolingbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Log4j2
public class HashModel {

    private String hashObj;
    private String hashDoc;
    private String hashAlg;
    private String hashValue;
    private String hashUrl;

    public static List<HashModel> loadModels(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON string to List<HashModel>: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
