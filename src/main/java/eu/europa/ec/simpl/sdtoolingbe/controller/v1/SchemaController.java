package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Deprecated(since = "v1.22.0", forRemoval = true)
/**
 * @deprecated to be removed when FE will be aligned using the V2 schema controller
 */
@RequestMapping(RequestMappingV1.SCHEMA_CONTROLLER)
@Tag(
        name = "Schema",
        description =
                """
    **DEPRECATED: All controller endpoints are deprecated since v1.22.0 and will be removed in a future version.**

    **Please use the V2 version of this controller**

    """)
public interface SchemaController {

    /**
     * Returns schema content for the specified schema identifier.
     *
     * @param schemaId the schema identifier to return content for.
     * @return the full content of the respective TTL file.
     */
    @GetMapping(value = "/{schemaId}/content", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(
            summary = "Returns schema content based on schemaId",
            description = "Returns schema content for the specified schema identifier.",
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
                                                        value = "@prefix ex: <http://example.org/> .")))
            })
    ResponseEntity<String> getSchemaContent(
            @PathVariable
                    @Schema(
                            requiredMode = Schema.RequiredMode.REQUIRED,
                            description = "Identifier of the TTL schema",
                            example = "data-offeringShape.ttl")
                    String schemaId);

    /**
     * Returns all schema identifiers in order to allow them to be selected and initiate the self-description creation process.
     *
     * @return a map of all available schema files
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List all available schema ids",
            description =
                    "Returns all schema identifiers in order to allow them to be selected and initiate the self-description creation process.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A map of available TTL schema files",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Object.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Available TTL files",
                                                        description = "Available TTL files",
                                                        value =
                                                                """
                                    {
                                        "simpl": {
                                            "contract": [
                                                "contract-templateShape.ttl"
                                            ],
                                            "service": [
                                                "application-offeringShape.ttl",
                                                "data-offeringShape.ttl",
                                                "infrastructure-offeringShape.ttl"
                                            ]
                                        }
                                    }
                                    """)))
            })
    ResponseEntity<Map<String, List<String>>> getSchemas();
}
