package eu.europa.ec.simpl.sdtoolingbe.service.federatedcatalogue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.tier2.client.federatedcatalogue.FederatedCatalogueTier2Client;
import eu.europa.ec.simpl.data1.common.tier2.client.federatedcatalogue.FederatedCatalogueTier2ClientBuilder;
import feign.FeignException;
import java.net.ConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FederatedCatalogueServiceTest {

    @Mock
    private FederatedCatalogueTier2ClientBuilder federatedCatalogueTier2ClientBuilder;

    @Mock
    private FederatedCatalogueTier2Client federatedCatalogueTier2Client;

    private FederatedCatalogueServiceImpl federatedCatalogueService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        when(federatedCatalogueTier2ClientBuilder.build(any())).thenReturn(federatedCatalogueTier2Client);
        federatedCatalogueService =
                new FederatedCatalogueServiceImpl(federatedCatalogueTier2ClientBuilder, objectMapper);
    }

    @Test
    void testPublishSD() throws Exception {
        String sdJsonLd = "{ \"key\": \"value\" }";
        String tier1AccessToken = "tier1-token";
        String expectedResponse = "{\"result\":\"success\"}";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(federatedCatalogueTier2Client.createSelfDescription(any(), any())).thenReturn(expectedResponse);

        JsonNode result = federatedCatalogueService.publishSD(sdJsonLdObj, tier1AccessToken);

        assertEquals(expectedResponse, result.toString());
    }

    @Test
    void testPublishSDWithFeignException() throws Exception {
        String sdJsonLd = "{ \"key\": \"value\" }";
        String tier1AccessToken = "tier1-token";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(federatedCatalogueTier2Client.createSelfDescription(nullable(String.class), any()))
                .thenThrow(FeignException.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> federatedCatalogueService.publishSD(sdJsonLdObj, tier1AccessToken));
    }

    @Test
    void testPublishSDWithInvalidSDJsonException() throws Exception {
        String sdJsonLd = "{ \"key\": \"value\" }";
        String tier1AccessToken = "tier1-token";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(federatedCatalogueTier2Client.createSelfDescription(nullable(String.class), any()))
                .thenThrow(FeignException.Conflict.class);

        assertThrows(
                RemoteServiceErrorException.class,
                () -> federatedCatalogueService.publishSD(sdJsonLdObj, tier1AccessToken));
    }

    @Test
    void testPublishSDWithConnectException() throws Exception {
        String sdJsonLd = "{ \"key\": \"value\" }";
        String tier1AccessToken = "tier1-token";

        JsonNode sdJsonLdObj = objectMapper.readTree(sdJsonLd);

        when(federatedCatalogueTier2ClientBuilder.build(any())).thenThrow(new ConnectException());

        assertThrows(
                RemoteServiceErrorException.class,
                () -> federatedCatalogueService.publishSD(sdJsonLdObj, tier1AccessToken));
    }
}
