package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress.ResourceAddressService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("resourceAddressControllerV1")
@Log4j2
@RequiredArgsConstructor
public class ResourceAddressControllerImpl extends AbstractController implements ResourceAddressController {

    private final ResourceAddressService resourceAddressService;

    @LogRequest
    @Override
    public ResponseEntity<List<String>> getResourceSharingMethods(OfferType offeringType) {
        log.debug(
                "getResourceSharingMethods(): invoking resourceAddressService.getResourceSharingMethods() with offeringType {}",
                offeringType);
        return ResponseEntity.ok(resourceAddressService.getResourceSharingMethods(offeringType));
    }

    @LogRequest
    @Override
    public ResponseEntity<List<Template>> getSourceAddressTemplates(OfferType offeringType, String sharingMethodId) {
        log.debug(
                "getSourceAddressTemplates(): invoking resourceAddressService.getSourceAddressTemplates() with offeringType {} and sharingMethodId '{}'",
                offeringType,
                sharingMethodId);
        return ResponseEntity.ok(resourceAddressService.getSourceAddressTemplates(offeringType, sharingMethodId));
    }

    @LogRequest
    @Override
    public ResponseEntity<String> getSourceAddressSchema(String templateId)
            throws ResourceAddressNotFoundException, IOException {
        log.debug(
                "getSourceAddressSchema(): invoking resourceAddressService.getSourceAddressSchema() with templateId '{}'",
                templateId);
        return ResponseEntity.ok(resourceAddressService.getSourceAddressSchema(templateId));
    }

    @LogRequest
    @Override
    public ResponseEntity<String> getSourceAddressUiSchema(String templateId)
            throws ResourceAddressNotFoundException, IOException {
        log.debug(
                "getSourceAddressUiSchema(): invoking resourceAddressService.getSourceAddressUiSchema() with templateId '{}'",
                templateId);
        return ResponseEntity.ok(resourceAddressService.getSourceAddressUiSchema(templateId));
    }

    @Override
    public ResponseEntity<ResourceAddress> getResourceAddress(HttpServletRequest httpServletRequest, String assetId) {
        String tier1BearerToken = AuthBearerUtil.getBearerValue(httpServletRequest);

        log.debug(
                "getResourceAddress(): invoking resourceAddressService.getResourceAddress() " + "for assetId '{}'",
                assetId);

        return ResponseEntity.ok(resourceAddressService.getResourceAddress(tier1BearerToken, assetId));
    }
}
