package eu.europa.ec.simpl.sdtoolingbe.service.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.client.validation.ValidationClientBuilder;
import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.custom.CustomMultipartFile;
import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.InvalidSDJsonException;
import eu.europa.ec.simpl.data1.common.exception.ResourceAddressValidationException;
import eu.europa.ec.simpl.data1.common.properties.ValidationProperties;
import eu.europa.ec.simpl.data1.common.util.JsonUtil;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import feign.FeignException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RequiredArgsConstructor
@Service
public class ValidationServiceImpl implements ValidationService {

    private final ValidationClientBuilder validationClient;
    private final ValidationProperties validationProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void validateJsonLd(JsonNode sdJsonLd, String schemaContent) {
        if (!validationProperties.isEnabled()) {
            log.warn("validateJsonLd(): ignored cause validationProperties disabled");
            return;
        }

        try {
            log.debug("validateJsonLd(): for sdJsonLd '{}', schemaContent '{}'", sdJsonLd, schemaContent);

            String sdJsonLdAsString = sdJsonLd.toString();
            MultipartFile jsonLdFile = CustomMultipartFile.convertStringWriterToMultipartFile(
                    sdJsonLdAsString, "jsonLdFile.json", StandardCharsets.UTF_8);

            MultipartFile shapeFile = CustomMultipartFile.convertStringWriterToMultipartFile(
                    schemaContent, "shapeFile.ttl", StandardCharsets.UTF_8);

            ResponseEntity<String> response = validationClient.validateSelfDescription(jsonLdFile, shapeFile);
            handleValidationResponseForJsonLd(response);
        } catch (FeignException e) {
            log.error(
                    "validateJsonLd(): failed processing sdJsonLd '{}' for schemaContent '{}': {}",
                    sdJsonLd,
                    schemaContent,
                    e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_VALIDATION_ERROR, e);
        }
    }

    @Override
    public void validateResourceAddress(JsonNode resourceAddress, String sourceAddressSchema) {
        if (!validationProperties.isEnabled()) {
            log.warn("validateResourceAddress(): ignored cause validationProperties disabled");
            return;
        }

        try {
            MultipartFile jsonSchemaFile = CustomMultipartFile.convertStringWriterToMultipartFile(
                    sourceAddressSchema, "jsonSchemaString.json", StandardCharsets.UTF_8);

            String resourceAddressAsString = resourceAddress.toString();
            MultipartFile jsonValueFile = CustomMultipartFile.convertStringWriterToMultipartFile(
                    resourceAddressAsString, "jsonValueString.json", StandardCharsets.UTF_8);

            ResponseEntity<String> response = validationClient.validateResourceAddress(jsonSchemaFile, jsonValueFile);

            handleValidationResponseForResourceAddress(response);
        } catch (FeignException e) {
            log.error("validateResourceAddress() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_VALIDATION_ERROR, null, e);
        }
    }

    private void handleValidationResponseForResourceAddress(ResponseEntity<String> response) {
        String methodName = "handleValidationResponseForResourceAddress() - ";
        if (response == null) {
            log.debug("{}received null response", methodName);
            return;
        }

        log.debug("{}for statusCode {} with response {}", methodName, response.getStatusCode(), response);

        String responseString = response.getBody();
        if (StringUtils.isBlank(responseString)) {
            log.debug("{}invalid responseString: {}", methodName, responseString);
            return;
        }

        processBodyResponseForResourceAddress(responseString);
    }

    private void processBodyResponseForResourceAddress(String responseBody) {
        String methodName = "processBodyResponseForResourceAddress() - ";

        log.debug("{}for response body '{}'", methodName, responseBody);

        JsonNode responseJsonNode = JsonUtil.createJsonNodeFromRemoteServiceResponse(
                responseBody, objectMapper, CommonErrorType.REMOTE_VALIDATION_ERROR);

        if (!responseJsonNode.has("valid")) {
            log.debug("{}no validNode found", methodName);
            return;
        }

        JsonNode validNode = responseJsonNode.get("valid");
        log.debug("{}response validNode {}", methodName, validNode);

        if (validNode.asBoolean()) {
            return;
        }

        String problemIssue = "error obj not found";
        if (responseJsonNode.has("errors")) {
            JsonNode errorsNode = responseJsonNode.get("errors");
            log.debug("{}found response errorsNode {}", methodName, errorsNode);
            problemIssue = errorsNode.toString();
        }
        log.error("{}failed cause: {}", methodName, problemIssue);
        throw new ResourceAddressValidationException(problemIssue);
    }

    private void handleValidationResponseForJsonLd(ResponseEntity<String> response) {
        if (response == null) {
            log.debug("handleValidationResponseForJsonLd(): received null response");
            return;
        }

        log.debug(
                "handleValidationResponseForJsonLd(): for statusCode {} with response {}",
                response.getStatusCode(),
                response);

        String responseString = response.getBody();
        if (StringUtils.isBlank(responseString)) {
            log.debug("handleValidationResponseForJsonLd(): invalid responseString: {}", responseString);
            return;
        }

        processBodyResponseForJsonLd(responseString);
    }

    private void processBodyResponseForJsonLd(String responseBody) {
        log.debug("processBodyResponseForJsonLd(): for response {}", responseBody);

        JsonNode jsonNode = JsonUtil.createJsonNodeFromRemoteServiceResponse(
                responseBody, objectMapper, CommonErrorType.REMOTE_VALIDATION_ERROR);
        JsonNode graphNode = jsonNode.get("@graph");

        if (graphNode == null || !graphNode.isArray()) {
            return;
        }

        // collect every SHACL violation instead of throwing on the first one: nested node-shape failures
        // (e.g. CorpusShape) emit both a generic parent result and the concrete leaf results, and we want
        // to surface all of them at once rather than one per request
        List<String> violations = new ArrayList<>();
        for (JsonNode node : graphNode) {
            String violation = describeJsonLdViolation(node);
            if (violation != null) {
                violations.add(violation);
            }
        }

        if (!violations.isEmpty()) {
            violations.forEach(violation -> log.error("processBodyResponseForJsonLd(): SHACL violation: {}", violation));
            throw new InvalidSDJsonException(String.join("; ", violations));
        }
    }

    private static String describeJsonLdViolation(JsonNode node) {
        String errorDescription = getNodeText(node);
        String errorField = getErrorField(node);

        if (errorDescription == null || errorField == null) {
            return null;
        }

        String valueField = getNodeValue(node);

        if (valueField != null && !valueField.startsWith("_")) {
            return errorDescription + " [" + errorField + "=" + valueField + "]";
        }
        return errorDescription + " [" + errorField + "]";
    }

    private static String getErrorField(JsonNode node) {
        JsonNode errorFieldNode = node.get("sh:resultPath");
        if (errorFieldNode == null || errorFieldNode.get("@id") == null) {
            return null;
        }

        String errorField = errorFieldNode.get("@id").asText();
        return errorField.substring((CommonConstants.ECOSYSTEM + ":").length());
    }

    private static String getNodeValue(JsonNode node) {
        JsonNode valueNode = node.get("sh:value");
        if (valueNode == null || valueNode.get("@value") == null) {
            return null;
        }

        return valueNode.get("@value").asText();
    }

    private static String getNodeText(JsonNode node) {
        JsonNode textNode = node.get("sh:resultMessage");
        if (textNode == null) {
            return null;
        }

        return textNode.asText();
    }
}
