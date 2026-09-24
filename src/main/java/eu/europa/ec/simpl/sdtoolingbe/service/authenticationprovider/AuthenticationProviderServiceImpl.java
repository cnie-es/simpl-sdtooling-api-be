package eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider;

import eu.europa.ec.simpl.data1.common.enumeration.CommonErrorType;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.data1.common.util.RemoteServiceUtil;
import eu.europa.ec.simpl.sdtoolingbe.client.authenticationprovider.AuthenticationProviderClient;
import eu.europa.ec.simpl.sdtoolingbe.model.client.identityattribute.IdentityAttribute;
import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import feign.FeignException;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthenticationProviderServiceImpl implements AuthenticationProviderService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 9999;

    private AuthenticationProviderClient authenticationProviderClient;

    public AuthenticationProviderServiceImpl(AuthenticationProviderClient authenticationProviderClient) {
        this.authenticationProviderClient = authenticationProviderClient;
    }

    @Override
    public List<IdentityAttribute> getIdentityAttributes(String bearerToken) {
        List<IdentityAttribute> result;
        try {
            String json = authenticationProviderClient.getIdentityAttributes(
                    AuthBearerUtil.toBearerString(bearerToken), DEFAULT_PAGE, DEFAULT_PAGE_SIZE, true, true);
            log.debug("getIdentityAttributes(): received response {}", json);

            result = parseIdentityAttributes(json);
            log.debug("getIdentityAttributes(): returning {}", result);
            return result;
        } catch (FeignException e) {
            log.error("getIdentityAttributes() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_AUTH_PROVIDER_ERROR, null, e);
        }
    }

    private static List<IdentityAttribute> parseIdentityAttributes(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            return StreamSupport.stream(jsonArray.spliterator(), false)
                    .map(JSONObject.class::cast)
                    .filter(item -> item.getBoolean("enabled") && item.getBoolean("assignableToRoles"))
                    .map(item -> new IdentityAttribute(item.getString("name"), item.getString("code")))
                    .toList();
        } catch (JSONException e) {
            log.error("parseIdentityAttributes() failed to parse JSON response: {}", e.getMessage());
            throw new RemoteServiceUnexpectedResponseException(
                    CommonErrorType.REMOTE_AUTH_PROVIDER_ERROR, "failed to parse identity attributes response", e);
        }
    }

    @Override
    public Participant getParticipant(String bearerToken) {
        try {
            return authenticationProviderClient.getParticipant(AuthBearerUtil.toBearerString(bearerToken));
        } catch (FeignException e) {
            log.error("getCredentials() failed", e);
            throw RemoteServiceUtil.toRemoteServiceErrorException(CommonErrorType.REMOTE_AUTH_PROVIDER_ERROR, null, e);
        }
    }
}
