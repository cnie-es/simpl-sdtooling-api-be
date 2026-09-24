package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(RequestMappingV1.SD_CONTROLLER)
@Tag(name = "Self Description")
public interface SDController {

    @Operation(
            summary = "Enriches and validates a self-description using the provided metadata and additional properties",
            description =
                    """
                **DEPRECATED: This endpoint is deprecated since v1.22.0 and will be removed in a future version.**

                **Please use the V2 version of this endpoint**

                ---

                Adds hashing, registers asset and policies into the provider EDC, creates a contract definition on EDC connector and validates the resulting SD json enriched with all info. Allows passing additional properties via a JSON string.
                """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully enriched and validated SD JSON-LD",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "object"),
                                        examples =
                                                @ExampleObject(
                                                        name = "SD json file enriched and validated",
                                                        description = "SD json file enriched and validated",
                                                        value =
                                                                """
                                                            {
                                                                "@context": "http://www.w3.org/ns/odrl.jsonld",
                                                                "@type": "Set",
                                                                "assigner": {
                                                                    "role": "http://www.w3.org/ns/odrl/2/assigner",
                                                                    "uid": "provider"
                                                                },
                                                                "permission": [
                                                                    {
                                                                        "action": [
                                                                            "http://simpl.eu/odrl/actions/search"
                                                                        ],
                                                                        "assignee": {
                                                                            "role": "http://www.w3.org/ns/odrl/2/assignee",
                                                                            "uid": "CONSUMER"
                                                                        },
                                                                        "constraint": [
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/gteq",
                                                                                "rightOperand": "2024-08-01T00:00:00Z"
                                                                            },
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/lteq",
                                                                                "rightOperand": "2024-08-31T23:59:59Z"
                                                                            }
                                                                        ],
                                                                        "target": ""
                                                                    }
                                                                ],
                                                                "profile": "http://www.w3.org/ns/odrl/2/odrl.jsonld",
                                                                "target": "",
                                                                "uid": "68777919-f260-4305-9c41-d4266ac0a639"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Bad Request Error Example",
                                                        description = "Bad Request Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:validationError",
                                                                "title": "Parameter validation error",
                                                                "status": 400,
                                                                "detail": "One or more parameters are invalid",
                                                                "issues": [
                                                                    {
                                                                        "type": "urn:problem-type:simpl:invalidParameter",
                                                                        "title": "Invalid parameter",
                                                                        "detail": "The schemaId parameter must meet specific criteria",
                                                                        "in": "query",
                                                                        "name": "schemaId",
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
                                        examples =
                                                @ExampleObject(
                                                        name = "Unauthorized Error Example",
                                                        description = "Unauthorized Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:unauthorized",
                                                                "title": "Unauthorized",
                                                                "status": 401,
                                                                "detail": "Missing or invalid Authorization header",
                                                                "instance": "/selfDescriptions/enriched"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "406",
                        description = "Not Acceptable",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Not Acceptable Error Example",
                                                        description = "Not Acceptable Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:notAcceptable",
                                                                "title": "Not Acceptable",
                                                                "status": 406,
                                                                "detail": "The requested media type could not be provided",
                                                                "instance": "/selfDescriptions/enriched"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Unsupported Media Type Error Example",
                                                        description = "Unsupported Media Type Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:unsupportedMediaType",
                                                                "title": "Unsupported Media Type",
                                                                "status": 415,
                                                                "detail": "The media type in the request payload is unsupported",
                                                                "instance": "/selfDescriptions/enriched"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
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
                                                                "instance": "/selfDescriptions/enriched"
                                                            }
                                                            """)))
            })
    @PostMapping(path = "/enriched", produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated(since = "v1.22.0", forRemoval = true)
    /**
     * @deprecated to be removed when FE will be aligned using the enrichAndValidate on SD controller V2
     */
    ResponseEntity<String> enrichAndValidate(
            @Parameter(description = "schemaId", required = true, example = "data-offeringShape.ttl") @RequestParam
                    String schemaId,
            @Parameter(description = "resource address template id", required = true, example = "1") @RequestParam
                    String templateId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Request body containing SD JSON-LD",
                            required = true,
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema =
                                                    @Schema(
                                                            type = "object",
                                                            example =
                                                                    """
                                                            {
                                                                "@context": "http://www.w3.org/ns/odrl.jsonld",
                                                                "@type": "Set",
                                                                "assigner": {
                                                                    "role": "http://www.w3.org/ns/odrl/2/assigner",
                                                                    "uid": "provider"
                                                                },
                                                                "permission": [
                                                                    {
                                                                        "action": ["http://simpl.eu/odrl/actions/search"],
                                                                        "assignee": {
                                                                            "role": "http://www.w3.org/ns/odrl/2/assignee",
                                                                            "uid": "CONSUMER"
                                                                        },
                                                                        "constraint": [
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/gteq",
                                                                                "rightOperand": "2024-08-01T00:00:00Z"
                                                                            },
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/lteq",
                                                                                "rightOperand": "2024-08-31T23:59:59Z"
                                                                            }
                                                                        ],
                                                                        "target": ""
                                                                    }
                                                                ],
                                                                "profile": "http://www.w3.org/ns/odrl/2/odrl.jsonld",
                                                                "target": "",
                                                                "uid": "68777919-f260-4305-9c41-d4266ac0a639"
                                                            }
                                                        """)))
                    @RequestBody
                    String sdJsonLd,
            HttpServletRequest httpRequest)
            throws IOException;

    @Operation(
            summary = "Publish an enriched, validated and signed self-description",
            description =
                    "Publishes a finalized (enriched, validated and signed) SD json-LD into the federated catalogue",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully published",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "object"),
                                        examples =
                                                @ExampleObject(
                                                        name = "Registration json",
                                                        description = "Registration json",
                                                        value =
                                                                """
                                                            {
                                                                "id": "did:web:registry.gaia-x.eu:DataOffering:3mwO1pOqDQ_fID9jc9Az1TS5JKIS_eng_0006",
                                                                "issuer": "did:web:did.dev.simpl-europa.eu",
                                                                "sdHash": "937295562072eb29046b8eb739eaa796144c9e9fa909b635dc5d0ce7b5ad5e27",
                                                                "status": "active",
                                                                "statusDatetime": "2025-03-13T09:41:11.103238436Z",
                                                                "uploadDatetime": "2025-03-13T09:41:11.103238376Z"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Bad Request Error Example",
                                                        description = "Bad Request Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:validationError",
                                                                "title": "Parameter validation error",
                                                                "status": 400,
                                                                "detail": "One or more parameters are invalid",
                                                                "instance": "/selfDescriptions/publications",
                                                                "issues": [
                                                                    {
                                                                        "type": "urn:problem-type:simpl:invalidParameter",
                                                                        "title": "Invalid parameter",
                                                                        "detail": "The json-file is not valid",
                                                                        "in": "body",
                                                                        "name": "json-file",
                                                                        "value": "invalidValue"
                                                                    }
                                                                ]
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "406",
                        description = "Not Acceptable",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Not Acceptable Error Example",
                                                        description = "Not Acceptable Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:notAcceptable",
                                                                "title": "Not Acceptable",
                                                                "status": 406,
                                                                "detail": "The requested media type could not be provided",
                                                                "instance": "/selfDescriptions/publish"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        examples =
                                                @ExampleObject(
                                                        name = "Unsupported Media Type Error Example",
                                                        description = "Unsupported Media Type Error Example",
                                                        value =
                                                                """
                                                            {
                                                                "type": "urn:problem-type:simpl:unsupportedMediaType",
                                                                "title": "Unsupported Media Type",
                                                                "status": 415,
                                                                "detail": "The media type in the request payload is unsupported",
                                                                "instance": "/selfDescriptions/publish"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
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
                                                                "instance": "/selfDescriptions/publications"
                                                            }
                                                            """)))
            })
    @PostMapping(path = "/publications", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> publish(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Request body containing SD JSON-LD object",
                            required = true,
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema =
                                                    @Schema(
                                                            type = "object",
                                                            example =
                                                                    """
                                                                {
                                                                "@context": "http://www.w3.org/ns/odrl.jsonld",
                                                                "@type": "Set",
                                                                "assigner": {
                                                                    "role": "http://www.w3.org/ns/odrl/2/assigner",
                                                                    "uid": "provider"
                                                                },
                                                                "permission": [
                                                                    {
                                                                        "action": ["http://simpl.eu/odrl/actions/search"],
                                                                        "assignee": {
                                                                            "role": "http://www.w3.org/ns/odrl/2/assignee",
                                                                            "uid": "CONSUMER"
                                                                        },
                                                                        "constraint": [
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/gteq",
                                                                                "rightOperand": "2024-08-01T00:00:00Z"
                                                                            },
                                                                            {
                                                                                "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                                                "operator": "http://www.w3.org/ns/odrl/2/lteq",
                                                                                "rightOperand": "2024-08-31T23:59:59Z"
                                                                            }
                                                                        ],
                                                                        "target": ""
                                                                    }
                                                                ],
                                                                "profile": "http://www.w3.org/ns/odrl/2/odrl.jsonld",
                                                                "target": "",
                                                                "uid": "68777919-f260-4305-9c41-d4266ac0a639"
                                                            }
                                                        """)))
                    @RequestBody
                    String sdJsonLd,
            HttpServletRequest request);
}
