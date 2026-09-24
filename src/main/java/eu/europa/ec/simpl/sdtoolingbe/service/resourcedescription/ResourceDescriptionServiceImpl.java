package eu.europa.ec.simpl.sdtoolingbe.service.resourcedescription;

import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnauthorizedException;
import eu.europa.ec.simpl.data1.common.tier2.client.catalogueadapter.CatalogueAdapterTier2Client;
import eu.europa.ec.simpl.data1.common.tier2.client.catalogueadapter.CatalogueAdapterTier2ClientBuilder;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import feign.FeignException;
import java.net.ConnectException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ResourceDescriptionServiceImpl implements ResourceDescriptionService {

    @Value("${catalogue-adapter.tier2-gateway.path-prefix}")
    private String catalogueAdapterTier2GatewayPathPrefix;

    private final CatalogueAdapterTier2ClientBuilder catalogueAdapterTier2ClientBuilder;

    public ResourceDescriptionServiceImpl(CatalogueAdapterTier2ClientBuilder catalogueAdapterTier2ClientBuilder) {
        this.catalogueAdapterTier2ClientBuilder = catalogueAdapterTier2ClientBuilder;
    }

    @Override
    public String getAllResourceDescriptions(String orderBy, String tier1BearerToken)
            throws RemoteServiceUnauthorizedException {
        try {
            CatalogueAdapterTier2Client client = catalogueAdapterTier2ClientBuilder.build(tier1BearerToken);
            // TODO: Check if orderBy is needed in the call Get Resource Descriptions
            return client.getAllResourceDescription(catalogueAdapterTier2GatewayPathPrefix, "");
        } catch (FeignException e) {
            log.error("getAllResourceDescriptions() failed cause FeignException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        } catch (ConnectException e) {
            log.error("getAllResourceDescriptions() failed cause ConnectException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        }
    }

    @Override
    public String getResourceDescription(String resourceDescriptionId, String tier1BearerToken)
            throws RemoteServiceUnauthorizedException {

        try {
            CatalogueAdapterTier2Client client = catalogueAdapterTier2ClientBuilder.build(tier1BearerToken);
            return client.getResourceDescription(catalogueAdapterTier2GatewayPathPrefix, resourceDescriptionId);
        } catch (FeignException e) {
            log.error("getResourceDescription() failed cause FeignException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        } catch (ConnectException e) {
            log.error("getResourceDescription() failed cause ConnectException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        }
    }

    @Override
    public String revokeResourceDescription(String resourceDescriptionId, String tier1BearerToken)
            throws RemoteServiceUnauthorizedException {
        try {
            CatalogueAdapterTier2Client client = catalogueAdapterTier2ClientBuilder.build(tier1BearerToken);
            return client.revokeResourceDescription(catalogueAdapterTier2GatewayPathPrefix, resourceDescriptionId);
        } catch (FeignException e) {
            log.error("revokeResourceDescription() failed cause FeignException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        } catch (ConnectException e) {
            log.error("revokeResourceDescription() failed cause ConnectException", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(
                    CommonErrorType.REMOTE_CATALOGUE_ADAPTER_ERROR, null, e);
        }
    }
}
