package eu.europa.ec.simpl.sdtoolingbe.controller.v3;

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

@RestController("sdControllerV3")
@Log4j2
@RequiredArgsConstructor
public class SDControllerImpl extends AbstractController implements SDController {

    private final EnrichService enrichService;
    private final ObjectMapper objectMapper;

    @LogRequest
    @Override
    public ResponseEntity<String> enrichAndValidate(String schemaId, String payload, HttpServletRequest httpRequest)
            throws IOException {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);
        JsonNode payloadObj = JsonUtil.createJsonNodeFromPayload(payload, null, objectMapper);
        JsonNode sdJsonLdObj = enrichService.enrichAndValidateV3(tier1BearerToken, payloadObj, schemaId);
        return ResponseEntity.ok(sdJsonLdObj.toString());
    }
}
