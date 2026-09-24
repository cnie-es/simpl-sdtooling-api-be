package eu.europa.ec.simpl.sdtoolingbe.service.connectoradapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.configuration.Participant;
import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.exception.ResourceNotFoundException;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadata;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.ExceptionUtil;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import eu.europa.ec.simpl.sdtoolingbe.client.connectoradapter.ConnectorAdapterClient;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConnectorAdapterServiceImpl implements ConnectorAdapterService {

    private final ConnectorAdapterClient connectorAdapterClient;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    public JsonNode registerV1(String bearerToken, JsonNode sdJsonLd, SchemaMetadata schemaMetadata) {
        try {
            log.debug(
                    "registerV1(): invoking connectorAdapterClient.registerV1() with sdJsonLd {} and schemaMetadata {}",
                    sdJsonLd,
                    schemaMetadata);
            ResponseEntity<String> response = connectorAdapterClient.registerV1(
                    AuthBearerUtil.toBearerString(bearerToken), sdJsonLd.toString(), schemaMetadata.toOfferType());
            log.debug("registerV1(): received response {}", response);

            return objectMapper.readTree(response.getBody());

        } catch (FeignException e) {
            log.error("registerV1() failed cause FeignException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR, "register operation failed", e);
        } catch (JsonProcessingException e) {
            log.error("registerV1() failed cause JsonProcessingException", e);
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR,
                    "register operation failed cause received an invalid body response",
                    e);
        }
    }

    @Override
    public JsonNode registerV2(String bearerToken, JsonNode payload, SchemaMetadata schemaMetadata) {
        try {
            log.debug(
                    "registerV2(): invoking connectorAdapterClient.registerV2() with payload {} and schemaMetadata {}",
                    payload,
                    schemaMetadata);
            ResponseEntity<String> response = connectorAdapterClient.registerV2(
                    AuthBearerUtil.toBearerString(bearerToken), payload.toString(), schemaMetadata.toOfferType());
            log.debug("registerV2(): received response {}", response);

            return objectMapper.readTree(response.getBody());

        } catch (FeignException e) {
            log.error("registerV2() failed cause FeignException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR, "register operation failed", e);
        } catch (JsonProcessingException e) {
            log.error("registerV2() failed cause JsonProcessingException", e);
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR,
                    "register operation failed cause received an invalid body response",
                    e);
        }
    }

    @Override
    @Cacheable
    public Participant getParticipant(String bearerToken) {
        try {
            log.debug("getParticipant(): invoking connectorAdapterClient.getParticipant()");
            ResponseEntity<Participant> response =
                    connectorAdapterClient.getParticipant(AuthBearerUtil.toBearerString(bearerToken));
            log.debug("getParticipant(): received response {}", response);

            return response.getBody();

        } catch (FeignException e) {
            log.error("getParticipant() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR, "get participant operation failed", e);
        }
    }

    @Override
    public ResourceAddress getResourceAddress(String bearerToken, String assetId) {
        try {
            log.debug(
                    "getResourceAddress(): invoking connectorAdapterClient.getResourceAddress() " + "for assetId '{}'",
                    assetId);
            ResponseEntity<ResourceAddress> response =
                    connectorAdapterClient.getResourceAddress(AuthBearerUtil.toBearerString(bearerToken), assetId);
            log.debug("getResourceAddress(): received response {}", response);

            // connector adapter response validation
            ResourceAddress resourceAddress = response.getBody();
            Set<ConstraintViolation<ResourceAddress>> violations = validator.validate(resourceAddress);
            if (!violations.isEmpty()) {
                throw ExceptionUtil.toRemoteServiceUnexpectedResponseException(
                        CommonErrorType.REMOTE_CONNECTOR_ADAPTER_UNEXPECTED_RESPONSE_ERROR, violations);
            }

            return resourceAddress;

        } catch (FeignException.NotFound e) {
            log.error("getResourceAddress() failed: resource address not found for assetId '{}'", assetId, e);
            throw new ResourceNotFoundException(
                    "resource address not found",
                    String.format("resource address with assetId '%s' not found", assetId),
                    null,
                    e);
        } catch (FeignException e) {
            log.error("getResourceAddress() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CONNECTOR_ADAPTER_ERROR, "get resource address operation failed", e);
        }
    }
}
