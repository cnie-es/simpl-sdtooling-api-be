package eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnauthorizedException;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceUnexpectedResponseException;
import eu.europa.ec.simpl.data1.common.util.AuthBearerUtil;
import eu.europa.ec.simpl.sdtoolingbe.client.authenticationprovider.AuthenticationProviderClient;
import eu.europa.ec.simpl.sdtoolingbe.model.client.identityattribute.IdentityAttribute;
import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationProviderServiceTest {

    @Mock
    private AuthenticationProviderClient authenticationProviderClient;

    @InjectMocks
    private AuthenticationProviderServiceImpl authenticationProviderService;

    @Test
    void testGetIdentityAttributes() throws Exception {

        when(authenticationProviderClient.getIdentityAttributes(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(getIdentityAttributesJsonResponse());

        List<IdentityAttribute> result =
                authenticationProviderService.getIdentityAttributes(AuthBearerUtil.toBearerString("testToken"));

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void testGetIdentityAttributesWithNoAttributes() throws Exception {

        when(authenticationProviderClient.getIdentityAttributes(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(getIdentityAttributesJsonEmptyResponse());

        List<IdentityAttribute> result =
                authenticationProviderService.getIdentityAttributes(AuthBearerUtil.toBearerString("testToken"));

        assertEquals(0, result.size());
    }

    @Test
    void testGetIdentityAttributesWithUnauthorizedException() {
        when(authenticationProviderClient.getIdentityAttributes(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceUnauthorizedException.class,
                () -> authenticationProviderService.getIdentityAttributes(AuthBearerUtil.toBearerString("testToken")));
    }

    @Test
    void testGetIdentityAttributesWithServiceUnavailableException() {
        when(authenticationProviderClient.getIdentityAttributes(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenThrow(FeignException.ServiceUnavailable.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> authenticationProviderService.getIdentityAttributes(AuthBearerUtil.toBearerString("testToken")));
    }

    @Test
    void testGetIdentityAttributesWithInvalidJsonResponse() {
        when(authenticationProviderClient.getIdentityAttributes(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn("invalid json");

        assertThrows(
                RemoteServiceUnexpectedResponseException.class,
                () -> authenticationProviderService.getIdentityAttributes(AuthBearerUtil.toBearerString("testToken")));
    }

    @Test
    void testGetCredentialsWithValidResponse() throws Exception {
        Participant participant = Participant.builder().id("participant-1234").build();

        when(authenticationProviderClient.getParticipant(any())).thenReturn(participant);

        Participant result = authenticationProviderService.getParticipant(AuthBearerUtil.toBearerString("testToken"));

        assertEquals(participant.getId(), result.getId());
    }

    @Test
    void testGetCredentialsWithUnauthorizedException() {
        when(authenticationProviderClient.getParticipant(any())).thenThrow(FeignException.Unauthorized.class);

        assertThrows(
                RemoteServiceUnauthorizedException.class,
                () -> authenticationProviderService.getParticipant(AuthBearerUtil.toBearerString("testToken")));
    }

    private static String getIdentityAttributesJsonResponse() {
        return """
    		{
    		"self": "/tier1/v2/identityAttributes?page=0&size=10",
    		"pageSize": 10,
    		"page": 0,
    		"total": 8,
    		"items": [
    			{
    				"id": "019be557-13d8-70f3-a9d0-24982f16af60",
    				"code": "APP_PROVIDER",
    				"name": "Application Provider",
    				"description": "Identity attribute used to tag the application provider",
    				"assignableToRoles": false,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:07.992178Z",
    				"updateTimestamp": "2026-02-02T08:54:46.943394Z"
    			},
    			{
    				"id": "019be557-13d8-7728-9d6f-c5cc2e2b384f",
    				"code": "DATA_PROVIDER",
    				"name": "Data Provider",
    				"description": "Identity attribute used to tag the data provider",
    				"assignableToRoles": false,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:07.957803Z",
    				"updateTimestamp": "2026-02-02T08:54:46.939568Z"
    			},
    			{
    				"id": "019be557-13d9-7528-a8d2-a3e345c1482f",
    				"code": "INFRA_PROVIDER",
    				"name": "Infrastructure Provider",
    				"description": "Identity attribute used to tag the infrastructure provider",
    				"assignableToRoles": false,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:07.996301Z",
    				"updateTimestamp": "2026-02-02T08:54:46.948008Z"
    			},
    			{
    				"id": "019be557-13d9-7e7b-8e6a-7eaba6bde26f",
    				"code": "DATA_PROVIDER_PUBLISHER",
    				"name": "Data Provider Publisher",
    				"description": "Identity attribute needed for publishing Data Catalogue",
    				"assignableToRoles": true,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:07.997667Z",
    				"updateTimestamp": "2026-02-02T08:54:46.949288Z"
    			},
    			{
    				"id": "019be557-13d9-7f4b-8c23-3aaee55ab639",
    				"code": "APP_PROVIDER_PUBLISHER",
    				"name": "Application Provider Publisher",
    				"description": "Identity attribute needed for publishing Application Catalogue",
    				"assignableToRoles": true,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:08.004046Z",
    				"updateTimestamp": "2026-02-02T08:54:46.950044Z"
    			},
    			{
    				"id": "019be557-13da-78d0-bc78-d1afaf5b0ae4",
    				"code": "INFRA_PROVIDER_PUBLISHER",
    				"name": "Infrastructure Provider Publisher",
    				"description": "Identity attribute needed for publishing Infrastructure",
    				"assignableToRoles": true,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:08.005714Z",
    				"updateTimestamp": "2026-02-02T08:54:46.951156Z"
    			},
    			{
    				"id": "019be557-13da-7d97-980f-c65d2292506c",
    				"code": "DATA_SEARCHER",
    				"name": "Data searcher",
    				"description": "Identity Attributes used for tagging an end user able to act only as a searcher in the catalogue, but he can't start a contract negotiation or transfer process",
    				"assignableToRoles": true,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:08.012796Z",
    				"updateTimestamp": "2026-02-02T08:54:46.952571Z"
    			},
    			{
    				"id": "019be557-1370-70e7-8554-687b9dd701b9",
    				"code": "CONSUMER",
    				"name": "Consumer",
    				"description": "Identity attribute used for tagging an end user able to act as a user of a data consumer participant",
    				"assignableToRoles": false,
    				"enabled": true,
    				"creationTimestamp": "2026-01-22T10:54:07.917713Z",
    				"updateTimestamp": "2026-01-22T10:54:07.917748Z"
    			}
    		]
    	}
    	""";
    }

    private static String getIdentityAttributesJsonEmptyResponse() {
        return """
    		{
    		"self": "/tier1/v2/identityAttributes?page=0&size=0",
    		"pageSize": 0,
    		"page": 0,
    		"total": 0,
    		"items": []
    	}
    	""";
    }
}
