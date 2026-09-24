package eu.europa.ec.simpl.sdtoolingbe.controller.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.sdtoolingbe.service.enrich.EnrichService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("sdControllerV2")
@Log4j2
@RequiredArgsConstructor
public class SDControllerImpl extends AbstractController implements SDController {

    private final EnrichService enrichService;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @LogRequest
    @Override
    @Deprecated(since = "v1.24.0", forRemoval = true)
    /**
     * @deprecated to be removed when FE will be aligned using the enrichAndValidate on SD controller V3
     */
    public ResponseEntity<String> enrichAndValidate(
            String schemaId, String templateId, String sdJsonLd, HttpServletRequest httpRequest) throws IOException {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);
        JsonNode sdJsonLdObj = JsonUtil.createJsonNodeFromSD(sdJsonLd, objectMapper);
        sdJsonLdObj = enrichService.enrichAndValidateV2(tier1BearerToken, sdJsonLdObj, schemaId, templateId);
        return ResponseEntity.ok(sdJsonLdObj.toString());
    }
}
