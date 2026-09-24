package eu.europa.ec.simpl.sdtoolingbe.service.versioning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.Versioning;
import eu.europa.ec.simpl.data1.common.util.SDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * VersioningService
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class VersioningServiceImpl implements VersioningService {

    private static final String SHAPE_VERSION_FIELD_PARENT_NAME = "generalServiceProperties";
    private static final String SHAPE_VERSION_FIELD_NAME = "version";

    @Override
    public void nextVersion(JsonNode sdJsonLd) {

        if (sdJsonLd.has(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME)) {
            ObjectNode parentObj = (ObjectNode) sdJsonLd.get(SDUtil.NS + SHAPE_VERSION_FIELD_PARENT_NAME);

            String currentVersion = parentObj.has(SDUtil.NS + SHAPE_VERSION_FIELD_NAME)
                    ? parentObj.get(SDUtil.NS + SHAPE_VERSION_FIELD_NAME).asText()
                    : null;

            if (StringUtils.isBlank(currentVersion)) {
                currentVersion = Versioning.initialVersion();
            } else {
                currentVersion = Versioning.nextVersion(currentVersion);
            }

            parentObj.put(SDUtil.NS + SHAPE_VERSION_FIELD_NAME, currentVersion);
        }
    }
}
