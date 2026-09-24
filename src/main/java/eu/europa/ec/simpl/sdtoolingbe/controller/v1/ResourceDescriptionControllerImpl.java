package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnauthorizedException;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.service.resourcedescription.ResourceDescriptionService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the ResourceDescriptionController interface.
 * Handles REST endpoints for resource descriptions.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ResourceDescriptionControllerImpl extends AbstractController implements ResourceDescriptionController {

    private final ResourceDescriptionService resourceDescriptionService;

    /**
     * Retrieves all resource descriptions.
     *
     * @param orderBy Field to order the results by
     * @return ResponseEntity containing a JSON string with all resource descriptions
     */
    @LogRequest
    @Override
    public ResponseEntity<String> getAllResourceDescriptions(
            @Schema(
                            requiredMode = Schema.RequiredMode.REQUIRED,
                            description = "Field to order the results by",
                            example = "publicationDate")
                    String orderBy,
            HttpServletRequest request)
            throws RemoteServiceUnauthorizedException {
        log.debug("Getting all resource descriptions ordered by: {}", orderBy);
        String tier1BearerToken = AuthBearerUtil.getBearerValue(request);
        String result = resourceDescriptionService.getAllResourceDescriptions(orderBy, tier1BearerToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Retrieves a specific resource description by ID.
     *
     * @param resourceDescriptionId ID of the resource description to retrieve
     * @return ResponseEntity containing a JSON string with the resource description details
     */
    @LogRequest
    @Override
    public ResponseEntity<String> getResourceDescription(String resourceDescriptionId, HttpServletRequest request)
            throws RemoteServiceUnauthorizedException {
        log.debug("Getting resource description with ID: {}", resourceDescriptionId);
        String tier1BearerToken = AuthBearerUtil.getBearerValue(request);
        String result = resourceDescriptionService.getResourceDescription(resourceDescriptionId, tier1BearerToken);
        return ResponseEntity.ok(result);
    }

    /**
     * Revokes a specific resource description by ID.
     *
     * @param resourceDescriptionId ID of the resource description to retrieve
     * @return ResponseEntity containing a JSON string with status of the revocation
     */
    @LogRequest
    @Override
    public ResponseEntity<String> revokeResourceDescription(String resourceDescriptionId, HttpServletRequest request)
            throws RemoteServiceUnauthorizedException {
        log.debug("Revoke resource description with ID: {}", resourceDescriptionId);
        String tier1BearerToken = AuthBearerUtil.getBearerValue(request);
        String result = resourceDescriptionService.revokeResourceDescription(resourceDescriptionId, tier1BearerToken);
        return ResponseEntity.ok(result);
    }
}
