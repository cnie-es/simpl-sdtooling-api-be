package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnauthorizedException;
import eu.europa.ec.simpl.data1.common.model.response.problem.BadRequestProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.InternalServerErrorProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.NotFoundProblem;
import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(RequestMappingV1.RESOURCE_DESCRIPTION_CONTROLLER)
@Tag(name = "Resource Description")
public interface ResourceDescriptionController {

    @Operation(
            summary = "Return all resource descriptions with the participant id as defined in the Client access token.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "returnValidResponse",
                                                        value =
                                                                """
                                                            {
                                                              "totalCount": 1,
                                                              "items": [
                                                                {
                                                                  "n": {
                                                                    "claimsGraphUri": [
                                                                      "did:web:registry.gaia-x.eu:InfrastructureOffering:a94ef06c-a12d-4308-89aa-fffcd8a61126"
                                                                    ],
                                                                    "offeringType": "infrastructure",
                                                                    "name": "text title for infrastructure resource.",
                                                                    "inLanguage": "da",
                                                                    "description": "text description",
                                                                    "serviceAccessPoint": "http://serviceaccesspoint.com"
                                                                  }
                                                                }
                                                              ]
                                                            }
                                                            """)))
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getAllResourceDescriptions(
            @Parameter(description = "Field to order the results by")
                    @RequestParam(required = false, defaultValue = "publicationDate")
                    String orderBy,
            HttpServletRequest request)
            throws RemoteServiceUnauthorizedException;

    @Operation(
            summary = "Return details of given resource description stored in federated catalogue",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        description =
                                                                "Resource description details. Due to ever changing nature of resource description shape it is impossible to provide detailed description of each property.",
                                                        implementation = String.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "returnValidResponse",
                                                        value =
                                                                """
                                                              {
                                                                "claimsGraphUri": [
                                                                  "did:web:registry.gaia-x.eu:InfrastructureOffering:a94ef06c-a12d-4308-89aa-fffcd8a61126"
                                                                ],
                                                                "offeringType": "infrastructure",
                                                                "name": "text title for infrastructure resource.",
                                                                "inLanguage": "da",
                                                                "description": "text description",
                                                                "serviceAccessPoint": "http://serviceaccesspoint.com"
                                                              }
                                                            """)))
            })
    @GetMapping(path = "/{resourceDescriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getResourceDescription(
            @Parameter(
                            description = "Resource description identifier (credentialSubject.@id)",
                            required = true,
                            schema = @Schema(type = "string", example = "did:web:registry.gaia-x.eu:DataOffering:123"))
                    @PathVariable
                    String resourceDescriptionId,
            HttpServletRequest request)
            throws RemoteServiceUnauthorizedException;

    @Operation(
            summary = "Revoke an already published self-description from the federated catalogue",
            description =
                    """
                            Revokes a previously published resource description.
                            After revocation, the self-description shall no longer be visible or discoverable in the federated catalogue.
                            """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully revoked self-description",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "RevokeSuccess",
                                                        value =
                                                                """
                                                            {
                                                                "id": "did:web:registry.gaia-x.eu:DataOffering:3mwO1...",
                                                                "status": "revoked",
                                                                "statusDatetime": "2025-03-20T10:31:00Z"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = BadRequestProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "BadRequestExample",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:badRequest",
                                                                "title": "BadRequest",
                                                                "status": 400,
                                                                "instance": "/resourceDescriptions/{resourceDescriptionId}/revoke",
                                                                "detail": "Invalid resourceDescriptionId format"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = NotFoundProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "NotFoundExample",
                                                        value =
                                                                """
                                                            {
                                                                "type": "resource-not-found",
                                                                "title": "RESOURCE_DESCRIPTION_NOT_FOUND",
                                                                "status": 404,
                                                                "instance": "/resourceDescriptions/{resourceDescriptionId}/revoke",
                                                                "detail": "Resource description not found or already revoked"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = InternalServerErrorProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "InternalServerErrorExample",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:internalServerError",
                                                                "title": "Internal Server Error",
                                                                "status": 500,
                                                                "instance": "/resourceDescriptions/{resourceDescriptionId}/revoke",
                                                                "detail": "Unexpected internal error"
                                                            }
                                                            """)))
            })
    @PostMapping(path = "/{resourceDescriptionId}/revoke", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> revokeResourceDescription(
            @Parameter(
                            description = "Resource description identifier (credentialSubject.@id)",
                            required = true,
                            schema = @Schema(type = "string", example = "did:web:registry.gaia-x.eu:DataOffering:123"))
                    @PathVariable("resourceDescriptionId")
                    String resourceDescriptionId,
            HttpServletRequest request)
            throws RemoteServiceUnauthorizedException;
}
