package eu.europa.ec.simpl.sdtoolingbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * Return object that has the entire shape graph with all properties including prefix info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShaclModelCustom extends ShaclModel {

    private final String shapeFileName;

    public ShaclModelCustom(List<Map<String, String>> prefixList, List<VicShape> shapes, String shapeFileName) {
        super(prefixList, shapes);
        this.shapeFileName = shapeFileName;
    }

    public String getShapeFileName() {
        return shapeFileName;
    }
}
