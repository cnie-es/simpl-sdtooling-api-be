package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.model.ld.odrl.OdrlPolicy;
import eu.europa.ec.simpl.sdtoolingbe.constant.RequestMappingV1;
import eu.europa.ec.simpl.sdtoolingbe.model.client.accesspolicy.AccessPolicyRequest;
import eu.europa.ec.simpl.sdtoolingbe.model.client.identityattribute.IdentityAttribute;
import eu.europa.ec.simpl.sdtoolingbe.model.client.policyaction.PolicyAction;
import eu.europa.ec.simpl.sdtoolingbe.model.client.usagepolicy.UsagePolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(RequestMappingV1.POLICY_CONTROLLER)
@Tag(name = "Policy")
public interface PolicyController {

    @Operation(
            summary = "Retrieve identity attributes for consumers",
            description =
                    "Returns the identity attributes assigned to the CONSUMER type by the Governance Authority, useful for defining who can access a resource",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A list of identity attributes",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "array", implementation = IdentityAttribute.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "validTypes",
                                                        summary = "Valid sharing methods",
                                                        value =
                                                                """
                                    [
                                        {
                                            "code": "CONSUMER",
                                            "identifier": "Consumer"
                                        }
                                    ]
                                    """)))
            })
    @GetMapping("/identityAttributes")
    List<IdentityAttribute> getIdentityAttributes(HttpServletRequest httpRequest);

    @Operation(
            summary = "List of available actions for access policies",
            description =
                    "Returns all supported actions that can be used in the definition of an access policy, such as SEARCH, CONSUME, and RESTRICTED_CONSUME",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A list of access policy actions",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "array", implementation = PolicyAction.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "validTypes",
                                                        summary = "Valid sharing methods",
                                                        value =
                                                                """
                                    [
                                        {
                                            "label": "search",
                                            "value": "SEARCH"
                                        },
                                        {
                                            "label": "consume",
                                            "value": "CONSUME"
                                        },
                                        {
                                            "label": "restrictedConsume",
                                            "value": "RESTRICTED_CONSUME"
                                        }
                                    ]
                                    """)))
            })
    @GetMapping(path = "/actions", produces = MediaType.APPLICATION_JSON_VALUE)
    List<PolicyAction> getAccessPolicyActions();

    @Operation(
            summary = "Generate an access policy in ODRL format",
            description =
                    "This endpoint receives a request containing a list of access permissions and returns an ODRL policy. The policy defines who can access a resource, with what type of action (e.g., SEARCH, CONSUME) and within what time frame",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The access policy json-ld",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "object", implementation = OdrlPolicy.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "validTypes",
                                                        summary = "Valid sharing methods",
                                                        value =
                                                                """
                                {
                                    "@context": "http://www.w3.org/ns/odrl.jsonld",
                                    "@type": "Set",
                                    "profile": "http://www.w3.org/ns/odrl/2/odrl.jsonld",
                                    "target": "",
                                    "assigner": {
                                        "uid": "provider",
                                        "role": "http://www.w3.org/ns/odrl/2/assigner"
                                    },
                                    "uid": "68777919-f260-4305-9c41-d4266ac0a639",
                                    "permission": [
                                        {
                                            "target": "",
                                            "assignee": {
                                                "uid": "CONSUMER",
                                                "role": "http://www.w3.org/ns/odrl/2/assignee"
                                            },
                                            "action": [
                                                "http://simpl.eu/odrl/actions/search"
                                            ],
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
                                            ]
                                        },
                                        {
                                            "target": "",
                                            "assignee": {
                                                "uid": "CONSUMER",
                                                "role": "http://www.w3.org/ns/odrl/2/assignee"
                                            },
                                            "action": [
                                                "http://simpl.eu/odrl/actions/consume"
                                            ],
                                            "constraint": [
                                                {
                                                    "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                    "operator": "http://www.w3.org/ns/odrl/2/gteq",
                                                    "rightOperand": "2024-08-01T00:00:00Z"
                                                }
                                            ]
                                        },
                                        {
                                            "target": "",
                                            "assignee": {
                                                "uid": "RESEARCHER",
                                                "role": "http://www.w3.org/ns/odrl/2/assignee"
                                            },
                                            "action": [
                                                "http://simpl.eu/odrl/actions/restrictedConsume"
                                            ],
                                            "constraint": [
                                                {
                                                    "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                    "operator": "http://www.w3.org/ns/odrl/2/gteq",
                                                    "rightOperand": "2024-09-01T00:00:00Z"
                                                },
                                                {
                                                    "leftOperand": "http://www.w3.org/ns/odrl/2/dateTime",
                                                    "operator": "http://www.w3.org/ns/odrl/2/lteq",
                                                    "rightOperand": "2024-10-30T23:59:59Z"
                                                }
                                            ]
                                        }
                                    ]
                                }
                                """)))
            })
    @PostMapping(path = "/access", produces = MediaType.APPLICATION_JSON_VALUE)
    OdrlPolicy getAccessPolicyJsonLD(@Valid @RequestBody AccessPolicyRequest request, HttpServletRequest httpRequest);

    @Operation(
            summary = "Generate a usage policy in ODRL format",
            description =
                    "This endpoint receives a request with usage permissions and returns an ODRL policy, which specifies how a resource can be used, by whom, and with what constraints",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The usage policy json-ld",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(type = "object", implementation = OdrlPolicy.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "validTypes",
                                                        summary = "Valid sharing methods",
                                                        value =
                                                                """
                                {
                                    "@context": "http://www.w3.org/ns/odrl.jsonld",
                                    "@type": "Set",
                                    "profile": "http://www.w3.org/ns/odrl/2/odrl.jsonld",
                                    "target": "",
                                    "assigner": {
                                        "uid": "provider",
                                        "role": "http://www.w3.org/ns/odrl/2/assigner"
                                    },
                                    "uid": "f5cb3167-cd29-4d1d-a0f2-4889caf7c875",
                                    "permission": [
                                        {
                                            "target": "",
                                            "assignee": {
                                                "uid": "consumer",
                                                "role": "http://www.w3.org/ns/odrl/2/assignee"
                                            },
                                            "action": [
                                                "http://www.w3.org/ns/odrl/2/use"
                                            ],
                                            "constraint": [
                                                {
                                                    "leftOperand": "http://www.w3.org/ns/odrl/2/count",
                                                    "operator": "http://www.w3.org/ns/odrl/2/lteq",
                                                    "rightOperand": "10"
                                                },
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
                                            ]
                                        }
                                    ]
                                }
                                """)))
            })
    @PostMapping(path = "/usage", produces = MediaType.APPLICATION_JSON_VALUE)
    OdrlPolicy getUsagePolicyJsonLD(@Valid @RequestBody UsagePolicyRequest request, HttpServletRequest httpRequest);
}
