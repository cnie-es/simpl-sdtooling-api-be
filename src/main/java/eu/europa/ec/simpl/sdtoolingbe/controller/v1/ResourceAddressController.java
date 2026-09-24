package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressNotFoundException;
import eu.europa.ec.simpl.data1.common.model.response.problem.BadRequestProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.InternalServerErrorProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.NotFoundProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.UnauthorizedProblem;
import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV1;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(RequestMappingV1.RESOURCE_ADDRESS_CONTROLLER)
@Tag(name = "Resource Address")
public interface ResourceAddressController {

    @Operation(
            summary = "Returns the list of sharing methods by offer type.",
            description = "Returns the list of sharing methods mapped to the required offer type"
                    + " (DATA, APPLICATION, INFRASTRUCTURE).",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A list of sharing methods",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "array", implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "validTypes",
                                                        summary = "Valid sharing methods",
                                                        value = "[\"IONOS_S3\", \"HTTPDATA_PUSH\"]"))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = BadRequestProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Bad Request Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:validationError",
                                                                "title": "Parameter validation error",
                                                                "status": 400,
                                                                "detail": "One or more parameters are invalid",
                                                                "instance": "/resourceAddresses/sharingMethods",
                                                                "issues": [
                                                                    {
                                                                    "type": "urn:problem-type:simpl:invalidParameter",
                                                                    "title": "Invalid parameter",
                                                                    "detail": "The offeringType parameter must \
                                                                    meet specific criteria",
                                                                    "in": "query",
                                                                    "name": "offeringType",
                                                                    "value": "invalidValue"
                                                                    }
                                                                ]
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = UnauthorizedProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Unauthorized",
                                                        summary = "Missing or invalid authorization",
                                                        value =
                                                                """
                                                            {
                                                                "type": "unauthorized",
                                                                "title": "Unauthorized",
                                                                "status": 401,
                                                                "detail": "Missing or invalid Authorization header",
                                                                "instance": "/resourceAddresses/sharingMethods"
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
                                                        name = "NotFound",
                                                        summary = "Resource not found",
                                                        value =
                                                                """
                                                            {
                                                                "type": "resource-not-found",
                                                                "title": "RESOURCE_ADDRESS_NOT_FOUND",
                                                                "status": 404,
                                                                "detail": "UI schema not found",
                                                                "instance": "/resourceAddresses/sharingMethods"
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
                                                        name = "InternalServerError",
                                                        summary = "Unexpected internal error",
                                                        value =
                                                                """
                                                            {
                                                                "type": "internal-error",
                                                                "title": "Unexpected internal error",
                                                                "status": 500,
                                                                "detail": "GENERAL_ERROR: Please try again later",
                                                                "instance": "/resourceAddresses/sharingMethods"
                                                            }
                                                            """)))
            })
    @GetMapping(path = "/sharingMethods", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<String>> getResourceSharingMethods(
            @Parameter(description = "The offering type identifier", required = true) @RequestParam
                    OfferType offeringType);

    @Operation(
            summary = "Returns the list of source address templates by sharing method and offering type.",
            description = "Returns the list of templates mapped to the required sharing method"
                    + " and offering type (DATA, APPLICATION, INFRASTRUCTURE).",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A list of templates",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "array", implementation = Template.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "templates",
                                                        summary = "List of templates",
                                                        value =
                                                                """
                                                            [
                                                                {"id":"TPL-1", "label":"TPL 1"},
                                                                {"id":"TPL-2", "label":"TPL 2"},
                                                                {"id":"TPL-3", "label":"TPL 3"}
                                                            ]
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
                                                        name = "Bad Request Error Example",
                                                        value =
                                                                """
                                    {
                                        "type": "urn:problem-type:simpl:validationError",
                                        "title": "Parameter validation error",
                                        "status": 400,
                                        "detail": "One or more parameters are invalid",
                                        "instance": "/resourceAddresses/sharingMethods/{sharingMethodId}/templates",
                                        "issues": [
                                            {
                                                "type": "urn:problem-type:simpl:invalidParameter",
                                                "title": "Invalid parameter",
                                                "detail": "The sharingMethodId parameter must meet specific criteria",
                                                "in": "path",
                                                "name": "sharingMethodId",
                                                "value": "invalidValue"
                                            }
                                        ]
                                    }
                                    """))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = UnauthorizedProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Unauthorized",
                                                        summary = "Missing or invalid authorization",
                                                        value =
                                                                """
                                        {
                                            "type": "unauthorized",
                                            "title": "Unauthorized",
                                            "status": 401,
                                            "detail": "Missing or invalid Authorization header",
                                            "instance": "/resourceAddresses/sharingMethods/{sharingMethodId}/templates"
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
                                                        name = "NotFound",
                                                        summary = "Resource not found",
                                                        value =
                                                                """
                                        {
                                            "type": "resource-not-found",
                                            "title": "RESOURCE_ADDRESS_NOT_FOUND",
                                            "status": 404,
                                            "detail": "Template not found",
                                            "instance": "/resourceAddresses/sharingMethods/{sharingMethodId}/templates"
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
                                                        name = "InternalServerError",
                                                        summary = "Unexpected internal error",
                                                        value =
                                                                """
                                        {
                                            "type": "internal-error",
                                            "title": "Unexpected internal error",
                                            "status": 500,
                                            "detail": "GENERAL_ERROR: Please try again later",
                                            "instance": "/resourceAddresses/sharingMethods/{sharingMethodId}/templates"
                                        }
                                        """)))
            })
    @GetMapping(path = "/sharingMethods/{sharingMethodId}/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Template>> getSourceAddressTemplates(
            @Parameter(description = "The offering type identifier", required = true) @RequestParam
                    OfferType offeringType,
            @Parameter(
                            description = "The resource sharing method identifier",
                            required = true,
                            schema =
                                    @Schema(
                                            type = "string",
                                            allowableValues = {"HTTPDATA_PUSH", "IONOS_S3", "REST_API"}))
                    @PathVariable("sharingMethodId")
                    String sharingMethodId);

    @Operation(
            summary = "Returns a source address template schema based on templateId",
            description = "Returns a template (JSON schema) describing the fields and constraints"
                    + " to create the sourceAddress.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Template (JSON schema) for the sourceAddress",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "IonosS3",
                                                        summary = "IonosS3 template example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "object",
                                                                "properties": {
                                                                    "bucketName": {
                                                                        "type": "string"
                                                                    },
                                                                    "region": {
                                                                        "type": "string"
                                                                    }
                                                                },
                                                                "required": ["bucketName"]
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
                                                        name = "Bad Request Error Example",
                                                        value =
                                                                """
                                        {
                                            "type": "urn:problem-type:simpl:validationError",
                                            "title": "Parameter validation error",
                                            "status": 400,
                                            "detail": "One or more parameters are invalid",
                                            "instance": "/resourceAddresses/templates/{templateId}/schema",
                                            "issues": [
                                                {
                                                    "type": "urn:problem-type:simpl:invalidParameter",
                                                    "title": "Invalid parameter",
                                                    "detail": "The templateId parameter must meet specific criteria",
                                                    "in": "path",
                                                    "name": "templateId",
                                                    "value": "invalidValue"
                                                }
                                            ]
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
                                                        name = "NotFound",
                                                        summary = "Resource not found",
                                                        value =
                                                                """
                                                    {
                                                        "type": "resource-not-found",
                                                        "title": "RESOURCE_ADDRESS_NOT_FOUND",
                                                        "status": 404,
                                                        "detail": "UI schema not found",
                                                        "instance": "/resourceAddresses/templates/{templateId}/schema"
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
                                                        name = "InternalServerError",
                                                        summary = "Unexpected internal error",
                                                        value =
                                                                """
                                                    {
                                                        "type": "internal-error",
                                                        "title": "Unexpected internal error",
                                                        "status": 500,
                                                        "detail": "GENERAL_ERROR: Please try again later",
                                                        "instance": "/resourceAddresses/templates/{templateId}/schema"
                                                    }
                                                    """)))
            })
    @GetMapping(path = "/templates/{templateId}/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getSourceAddressSchema(
            @Parameter(
                            description = "The template id",
                            required = true,
                            schema = @Schema(type = "string", example = "TPL-1"))
                    @PathVariable("templateId")
                    String templateId)
            throws ResourceAddressNotFoundException, IOException;

    @Operation(
            summary = "Returns a source address UI schema based in templateId.",
            description = "Returns a UI schema that defines the constraints for the form"
                    + " representation of the sourceAddress.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "UI schema for the sourceAddress",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "amazonS3UI",
                                                        summary = "UI schema example",
                                                        value =
                                                                """
                                                            {
                                                                "ui:order": ["bucketName", "region"]
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
                                                        name = "Bad Request Error Example",
                                                        value =
                                                                """
                                        {
                                            "type": "urn:problem-type:simpl:validationError",
                                            "title": "Parameter validation error",
                                            "status": 400,
                                            "detail": "One or more parameters are invalid",
                                            "instance": "/resourceAddresses/templates/{templateId}/uiSchema",
                                            "issues": [
                                                {
                                                    "type": "urn:problem-type:simpl:invalidParameter",
                                                    "title": "Invalid parameter",
                                                    "detail": "The templateId parameter must meet specific criteria",
                                                    "in": "path",
                                                    "name": "templateId",
                                                    "value": "invalidValue"
                                                }
                                            ]
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
                                                        name = "NotFound",
                                                        summary = "Resource not found",
                                                        value =
                                                                """
                                                    {
                                                        "type": "resource-not-found",
                                                        "title": "RESOURCE_ADDRESS_NOT_FOUND",
                                                        "status": 404,
                                                        "detail": "UI schema not found",
                                                        "instance": "/resourceAddresses/templates/{templateId}/uiSchema"
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
                                                        name = "InternalServerError",
                                                        summary = "Unexpected internal error",
                                                        value =
                                                                """
                                                    {
                                                        "type": "internal-error",
                                                        "title": "Unexpected internal error",
                                                        "status": 500,
                                                        "detail": "GENERAL_ERROR: Please try again later",
                                                        "instance": "/resourceAddresses/templates/{templateId}/uiSchema"
                                                    }
                                                    """)))
            })
    @GetMapping(path = "/templates/{templateId}/uiSchema", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getSourceAddressUiSchema(
            @Parameter(
                            description = "The template id",
                            required = true,
                            schema = @Schema(type = "string", example = "TPL-1"))
                    @PathVariable("templateId")
                    String templateId)
            throws ResourceAddressNotFoundException, IOException;

    @Operation(
            summary = "Retrieves the resource address associated to the given assetId",
            description = "Retrieves the resource address associated to the given assetId")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the resource address associated to the given assetId",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceAddress.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Resource Address",
                                                        description =
                                                                "Resource description address to the given assetId",
                                                        value =
                                                                """
                                                            {
                                                                "templateId": "5",
                                                                "value": "{\\"type\\":\\"MinioS3\\",\
 \\"endpoint\\":\\"https://minio01.integrated.simpl-europe.eu\\",\
 \\"bucketName\\":\\"provider-bucket\\",\
 \\"objectName\\":\\"example-s3.txt\\"}"
                                                            }"""))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = UnauthorizedProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Unauthorized",
                                                        summary = "Missing or invalid authorization",
                                                        value =
                                                                """
                                                {
                                                    "type": "unauthorized",
                                                    "title": "Unauthorized",
                                                    "status": 401,
                                                    "detail": "Missing or invalid Authorization header",
                                                    "instance": "/resourceAddresses/assets/{assetId}"
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
                                                        name = "Not found Error Example",
                                                        summary = "AssetId Not Found",
                                                        value =
                                                                """
                        {
                            "type": "urn:problem-type:simpl:resourceAddressNotFound",
                            "title": "Resource Address Not Found",
                            "status": 404,
                            "detail": "No resource address found for assetId",
                            "instance": "/resourceAddresses/assets/{assetId}"
                        }
                        """))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = {
                            @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(implementation = InternalServerErrorProblem.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Internal Server Error Example",
                                                    description = "Internal Server Error Example",
                                                    value =
                                                            """
                                                            {
                                                                "type": "urn:problem-type:simpl:internalServerError",
                                                                "title": "Internal Server Error",
                                                                "status": 500,
                                                                "detail": "Unexpected internal error",
                                                                "instance": "/resourceAddresses/assets/{assetId}"
                                                            }
                                                            """))
                        })
            })
    @GetMapping(
            value = "/assets/{assetId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    ResponseEntity<ResourceAddress> getResourceAddress(
            HttpServletRequest httpServletRequest,
            @Parameter(
                            description = "Identifier of asset within connector",
                            required = true,
                            schema = @Schema(type = "string", example = "123"))
                    @PathVariable("assetId")
                    String assetId);
}
