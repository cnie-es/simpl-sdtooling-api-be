package eu.europa.ec.simpl.sdtoolingbe.controller.v2;

import eu.europa.ec.simpl.data1.common.model.response.problem.BadRequestProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.InternalServerErrorProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.NotAcceptableProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.NotFoundProblem;
import eu.europa.ec.simpl.data1.common.model.response.problem.UnauthorizedProblem;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadataWrapper;
import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Schema")
@RequestMapping(RequestMappingV2.SCHEMA_CONTROLLER)
public interface SchemaController {

    @Operation(
            summary = "List all available schema",
            description =
                    "Returns all schema info in order to allow them to be selected and initiate the self-description creation process.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A map of available TTL schema files",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "Available schemas",
                                                    description = "Available schemas",
                                                    value =
                                                            """
                                                                    {
                                                                      "schemas": [
                                                                        {
                                                                          "title": "Application Asset",
                                                                          "name": "health",
                                                                          "description": "Schema for describing a software application.",
                                                                          "resourceType": "APPLICATION",
                                                                          "version": "1.0.0",
                                                                          "schemaId": "applicationHealth"
                                                                        },
                                                                        {
                                                                          "title": "Data Offering",
                                                                          "name": "Data offering",
                                                                          "description": "SHACL shape for data offering self-descriptions.",
                                                                          "resourceType": "DATA",
                                                                          "version": "1.0.0",
                                                                          "schemaId": "dataOfferingShape"
                                                                        }
                                                                      ]
                                                                    }
                                                                    """),
                                            @ExampleObject(
                                                    name = "No schemas available",
                                                    description =
                                                            "No schemas available for the current environment/tenant",
                                                    value =
                                                            """
                                                                    {
                                                                      "schemas": []
                                                                    }
                                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = BadRequestProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:badRequest",
                                                      "title": "Invalid payload",
                                                      "status": 400,
                                                      "detail": "Invalid request",
                                                      "instance": "/schemas",
                                                      "issues": []
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:unauthorized",
                                                      "title": "Unauthorized",
                                                      "status": 401,
                                                      "instance": "/schemas",
                                                      "detail": "Missing or invalid Authorization header"
                                                    }
                                                    """))),
                @ApiResponse(
                        responseCode = "406",
                        description = "Not Acceptable",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = NotAcceptableProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Not Acceptable Example",
                                                        description = "Requested media type is not supported",
                                                        value =
                                                                """
                                                            {
                                                              "type": "urn:problem-type:simpl:notAcceptable",
                                                              "title": "Not Acceptable",
                                                              "status": 406,
                                                              "detail": "The requested media type could not be provided.",
                                                              "instance": "/schemas"
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:internalServerError",
                                                      "title": "Internal Server Error",
                                                      "status": 500,
                                                      "instance": "/schemas",
                                                      "detail": "Unexpected internal error"
                                                    }
                                                    """)))
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SchemaMetadataWrapper> getSchemas(
            @Parameter(description = "resourceType", example = "data") @RequestParam(required = false)
                    String resourceType);

    @Operation(
            summary = "Returns schema content based on schemaId",
            description = "Returns schema content for the specified schema identifier",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "TTL schema file content",
                        content =
                                @Content(
                                        mediaType = "text/turtle",
                                        schema = @Schema(implementation = String.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "ttlExample",
                                                        summary = "Example TTL content",
                                                        value = "@prefix ex: <http://example.org/> ."))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = BadRequestProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:badRequest",
                                                      "title": "Invalid payload",
                                                      "status": 400,
                                                      "detail": "Invalid request",
                                                      "instance": "/schemas/dataOfferingShape/content",
                                                      "issues": []
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:unauthorized",
                                                      "title": "Unauthorized",
                                                      "status": 401,
                                                      "instance": "/schemas/dataOfferingShape/content",
                                                      "detail": "Missing or invalid Authorization header"
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
                                                        name = "Not Found",
                                                        summary = "Schema not found",
                                                        value =
                                                                """
                                                            {
                                                              "type": "urn:problem-type:simpl:notFound",
                                                              "title": "Resource not found",
                                                              "status": 404,
                                                              "detail": "Schema with identifier 'dataOfferingShape' was not found",
                                                              "instance": "/schemas/dataOfferingShape/content"
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "406",
                        description = "Not Acceptable",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = NotAcceptableProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Not Acceptable Example",
                                                        description = "Requested media type is not supported",
                                                        value =
                                                                """
                                                            {
                                                              "type": "urn:problem-type:simpl:notAcceptable",
                                                              "title": "Not Acceptable",
                                                              "status": 406,
                                                              "detail": "The requested media type could not be provided.",
                                                              "instance": "/schemas/dataOfferingShape/content"
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:internalServerError",
                                                      "title": "Internal Server Error",
                                                      "status": 500,
                                                      "instance": "/schemas/dataOfferingShape/content",
                                                      "detail": "Unexpected internal error"
                                                    }
                                                    """)))
            })
    @GetMapping(value = "/{schemaId}/content", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> getSchemaContent(
            @PathVariable
                    @Parameter(
                            description = "Identifier of the TTL schema",
                            required = true,
                            example = "data-offeringShape")
                    @Schema(
                            requiredMode = Schema.RequiredMode.REQUIRED,
                            description = "Identifier of the schema",
                            example = "data-offeringShape")
                    String schemaId);

    /**
     * Returns schema content based on schemaId and version
     *
     * @param schemaId the schema identifier
     * @param version  the schema version
     * @return the full content of the respective TTL file.
     */
    @Operation(
            summary = "Retrieve a specific version of a schema",
            description =
                    "Retrieves the content of a specific version of a schema identified by its schema name and version. The endpoint returns the representation of the schema as published by the Governance Authority",
            operationId = "getSchemaByVersion",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Returns the content of the specified TTL file",
                        content =
                                @Content(
                                        mediaType = "text/turtle",
                                        schema = @Schema(implementation = String.class),
                                        examples =
                                                @ExampleObject(
                                                        """
                                                    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                                                    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
                                                    @prefix sh: <http://www.w3.org/ns/shacl#> .
                                                    @prefix simpl: <http://w3id.org/gaia-x/simpl#> .
                                                    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

                                                    simpl:DataOfferingShape
                                                      rdf:type sh:NodeShape ;
                                                      sh:targetClass simpl:DataOffering ;
                                                      sh:property [
                                                        sh:path simpl:name ;
                                                        sh:datatype xsd:string ;
                                                        sh:minCount 1 ;
                                                        sh:maxCount 1 ;
                                                        sh:description "The name of the data offering." ;
                                                      ] ;
                                                      sh:property [
                                                        sh:path simpl:description ;
                                                        sh:datatype xsd:string ;
                                                        sh:minCount 0 ;
                                                        sh:maxCount 1 ;
                                                        sh:description "A description of the data offering." ;
                                                      ] ;
                                                      sh:property [
                                                        sh:path simpl:format ;
                                                        sh:datatype xsd:string ;
                                                        sh:minCount 1 ;
                                                        sh:maxCount 1 ;
                                                        sh:description "The format of the data offering, e.g. XML, JSON, CSV." ;
                                                      ] .
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:validationError",
                                                      "title": "Invalid payload",
                                                      "status": 400,
                                                      "detail": "missing required arguments",
                                                      "instance": "/schemas/{schemaName}/{version}",
                                                      "issues": [
                                                        {
                                                          "detail": "Field 'schemaName' must not be empty"
                                                        }
                                                      ]
                                                    }
                                                    """))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Schema not found",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = NotFoundProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Schema Not Found Example",
                                                        description = "Schema not found",
                                                        value =
                                                                """
                                                    {
                                                      "type": "urn:problem-type:simpl:notFound",
                                                      "title": "Resource not found",
                                                      "status": 404,
                                                      "detail": "Schema with identifier 'dataOfferingShape' was not found",
                                                      "instance": "/schemas/dataOfferingShape/content"
                                                    }
                                                    """))),
                @ApiResponse(
                        responseCode = "406",
                        description = "Not Acceptable",
                        content =
                                @Content(
                                        mediaType = "application/problem+json",
                                        schema = @Schema(implementation = NotAcceptableProblem.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Not Acceptable Example",
                                                        description = "Requested media type is not supported",
                                                        value =
                                                                """
                                                    {
                                                      "type": "urn:problem-type:simpl:notAcceptable",
                                                      "title": "Not Acceptable",
                                                      "status": 406,
                                                      "detail": "Requested media type 'application/json' is not supported. Use 'text/turtle'.",
                                                      "instance": "/schemas/dataOfferingShape/content"
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
                                                        """
                                                    {
                                                      "type": "urn:problem-type:simpl:internalServerError",
                                                      "title": "Internal Server Error",
                                                      "status": 500,
                                                      "instance": "/schemas/{schemaName}/{version}",
                                                      "detail": "Unexpected internal error"
                                                    }
                                                    """)))
            })
    @GetMapping(value = "/{schemaId}/{version}", produces = "text/turtle")
    ResponseEntity<String> getSchemaByVersion(
            @Schema(
                            requiredMode = Schema.RequiredMode.REQUIRED,
                            description = "Unique identifier of the TTL schema",
                            example = "dataOfferingShape")
                    @PathVariable
                    String schemaId,
            @Schema(
                            requiredMode = Schema.RequiredMode.REQUIRED,
                            description = "Version of the TTL schema",
                            example = "1.0.0")
                    @PathVariable
                    String version);
}
