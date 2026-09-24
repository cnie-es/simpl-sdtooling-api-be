package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.sdtoolingbe.service.enrich.EnrichService;
import eu.europa.ec.simpl.sdtoolingbe.service.federatedcatalogue.FederatedCatalogueService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("sdControllerV1")
@Log4j2
@RequiredArgsConstructor
public class SDControllerImpl extends AbstractController implements SDController {

    private final EnrichService enrichService;
    private final FederatedCatalogueService federatedCatalogueService;
    private final ObjectMapper objectMapper;

    @LogRequest
    @Override
    public ResponseEntity<String> publish(String sdJsonLd, HttpServletRequest request) {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(request);
        JsonNode sdJsonLdObj = JsonUtil.createJsonNodeFromSD(sdJsonLd, objectMapper);
        sdJsonLdObj = federatedCatalogueService.publishSD(sdJsonLdObj, tier1BearerToken);
        return ResponseEntity.ok(sdJsonLdObj.toString());
    }

    @LogRequest
    @Override
    @Deprecated(since = "v1.22.0", forRemoval = true)
    @SuppressWarnings("removal")
    /**
     * @deprecated to be removed when FE will be aligned using the V2 SD controller
     */
    public ResponseEntity<String> enrichAndValidate(
            String schemaId, String templateId, String sdJsonLd, HttpServletRequest httpRequest) throws IOException {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpRequest);
        JsonNode sdJsonLdObj = JsonUtil.createJsonNodeFromSD(sdJsonLd, objectMapper);
        sdJsonLdObj = enrichService.enrichAndValidateV1(tier1BearerToken, sdJsonLdObj, schemaId, templateId);
        return ResponseEntity.ok(sdJsonLdObj.toString());
    }
}
