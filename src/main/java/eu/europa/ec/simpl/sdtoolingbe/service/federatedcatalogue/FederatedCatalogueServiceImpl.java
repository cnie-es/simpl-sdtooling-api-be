package eu.europa.ec.simpl.sdtoolingbe.service.federatedcatalogue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.tier2.client.federatedcatalogue.FederatedCatalogueTier2Client;
import eu.europa.ec.simpl.data1.common.tier2.client.federatedcatalogue.FederatedCatalogueTier2ClientBuilder;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import feign.FeignException;
import java.net.ConnectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class FederatedCatalogueServiceImpl implements FederatedCatalogueService {

    @Value("${federated-catalogue.tier2-gateway.path-prefix}")
    private String tier2GatewayPathPrefix;

    private final FederatedCatalogueTier2ClientBuilder federatedCatalogueTier2ClientBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public JsonNode publishSD(JsonNode sdJsonLd, String tier1BearerToken) {
        try {
            FederatedCatalogueTier2Client client = federatedCatalogueTier2ClientBuilder.build(tier1BearerToken);
            log.debug(
                    "publishSD(): invoking client.getSelfDescriptions() with {} and sdJsonLd {}",
                    tier2GatewayPathPrefix,
                    sdJsonLd);
            return objectMapper.readTree(client.createSelfDescription(tier2GatewayPathPrefix, sdJsonLd.toString()));
        } catch (FeignException e) {
            // e.getMessage() truncates the body; contentUTF8() carries the full FC verification report (SHACL)
            log.error("publishSD() failed cause FeignException, full response body: {}", e.contentUTF8(), e);
            if (e instanceof FeignException.Conflict || isDuplicateKeyError(e)) {
                // 409: self-description already exists
                throw RemoteServiceUtil.toRemoteServiceErrorException(
                        CommonErrorType.REMOTE_FEDERATED_CATALOGUE_ERROR, "sd already exists for the specified @id", e);
            }
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_FEDERATED_CATALOGUE_ERROR, e);
        } catch (ConnectException e) {
            log.error("publishSD() failed cause ConnectException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_FEDERATED_CATALOGUE_ERROR, e);
        } catch (JsonProcessingException e) {
            log.error("publishSD() failed cause JsonProcessingException", e);
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_FEDERATED_CATALOGUE_ERROR, null, e);
        }
    }

    private boolean isDuplicateKeyError(FeignException e) {
        if (e instanceof FeignException.InternalServerError) {
            String message = e.getMessage();
            return message.contains("DuplicateKeyException");
        }
        return false;
    }
}
