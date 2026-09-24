package eu.europa.ec.simpl.sdtoolingbe.service.resourcedescription;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.data1.common.exception.RemoteServiceErrorException;
import eu.europa.ec.simpl.data1.common.tier2.client.catalogueadapter.CatalogueAdapterTier2Client;
import eu.europa.ec.simpl.data1.common.tier2.client.catalogueadapter.CatalogueAdapterTier2ClientBuilder;
import feign.FeignException;
import java.net.ConnectException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceDescriptionServiceTest {

    @Mock
    private CatalogueAdapterTier2ClientBuilder catalogueAdapterTier2ClientBuilder;

    @Mock
    private CatalogueAdapterTier2Client catalogueAdapterTier2Client;

    @InjectMocks
    private ResourceDescriptionServiceImpl resourceDescriptionService;

    @BeforeEach
    void setUp() throws ConnectException {
        when(catalogueAdapterTier2ClientBuilder.build(any())).thenReturn(catalogueAdapterTier2Client);
    }

    private static Stream<Arguments> getAllResourceDescriptionsParameters() {
        String orderBy = "test";
        String tier1AccessToken = "tier1-token";

        return Stream.of(Arguments.of(orderBy, tier1AccessToken));
    }

    @ParameterizedTest
    @MethodSource("getAllResourceDescriptionsParameters")
    void testGetAllResourceDescriptionsWithSuccess(String orderBy, String tier1AccessToken) {
        String response = "{\"mock-result\":\"success\"}";

        String result = resourceDescriptionService.getAllResourceDescriptions(orderBy, tier1AccessToken);

        assertNotNull(response, result);
    }

    @ParameterizedTest
    @MethodSource("getAllResourceDescriptionsParameters")
    void testGetAllResourceDescriptionsWithFeignException(String search, String tier1AccessToken) {

        // Mock FeignException
        FeignException exception = Mockito.mock(FeignException.class);

        // Mock FeignException behavior
        when(catalogueAdapterTier2Client.getAllResourceDescription(Mockito.any(), Mockito.any()))
                .thenThrow(exception);

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.getAllResourceDescriptions(search, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling getAllResourceDescriptions");
    }

    @ParameterizedTest
    @MethodSource("getAllResourceDescriptionsParameters")
    void testGetAllResourceDescriptionsWithConnectException(String search, String tier1AccessToken)
            throws ConnectException {

        when(catalogueAdapterTier2ClientBuilder.build(any())).thenThrow(new ConnectException());

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.getAllResourceDescriptions(search, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling getAllResourceDescriptions");
    }

    private static Stream<Arguments> getResourceDescriptionParameters() {
        String resourceDescriptionId = "test-id";
        String tier1AccessToken = "tier1-token";

        return Stream.of(Arguments.of(resourceDescriptionId, tier1AccessToken));
    }

    @ParameterizedTest
    @MethodSource("getResourceDescriptionParameters")
    void testGetResourceDescriptionWithSuccess(String resourceDescriptionId, String tier1AccessToken) {
        String response = "{\"mock-result\":\"success\"}";

        String result = resourceDescriptionService.getResourceDescription(resourceDescriptionId, tier1AccessToken);

        assertNotNull(response, result);
    }

    @ParameterizedTest
    @MethodSource("getResourceDescriptionParameters")
    void testGetResourceDescriptionWithFeignException(String resourceDescriptionId, String tier1AccessToken) {

        // Mock FeignException
        FeignException exception = Mockito.mock(FeignException.class);

        // Mock FeignException behavior
        when(catalogueAdapterTier2Client.getResourceDescription(Mockito.any(), Mockito.any()))
                .thenThrow(exception);

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.getResourceDescription(resourceDescriptionId, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling getResourceDescription");
    }

    @ParameterizedTest
    @MethodSource("getResourceDescriptionParameters")
    void testGetResourceDescriptionWithConnectException(String resourceDescriptionId, String tier1AccessToken)
            throws ConnectException {

        when(catalogueAdapterTier2ClientBuilder.build(any())).thenThrow(new ConnectException());

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.getResourceDescription(resourceDescriptionId, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling getResourceDescription");
    }

    private static Stream<Arguments> revokeResourceDescriptionParameters() {
        String resourceDescriptionId = "did:web:registry.gaia-x.eu:DataOffering:3mwO1...";
        String tier1AccessToken = "tier1-token";

        return Stream.of(Arguments.of(resourceDescriptionId, tier1AccessToken));
    }

    @ParameterizedTest
    @MethodSource("revokeResourceDescriptionParameters")
    void testRevokeResourceDescriptionWithSuccess(String resourceDescriptionId, String tier1AccessToken) {
        String response =
                "{\"id\":\"did:web:registry.gaia-x.eu:DataOffering:3mwO1...\",\"status\":\"revoked\",\"statusDatetime\":\"2025-03-20T10:31:00Z\"}";

        when(catalogueAdapterTier2Client.revokeResourceDescription(Mockito.any(), Mockito.any()))
                .thenReturn(response);

        String result = resourceDescriptionService.revokeResourceDescription(resourceDescriptionId, tier1AccessToken);

        assertNotNull(result, "Expected a non-null result when calling revokeResourceDescription");
    }

    @ParameterizedTest
    @MethodSource("revokeResourceDescriptionParameters")
    void testRevokeResourceDescriptionWithFeignException(String resourceDescriptionId, String tier1AccessToken) {

        // Mock FeignException
        FeignException exception = Mockito.mock(FeignException.class);

        // Mock FeignException behavior
        when(catalogueAdapterTier2Client.revokeResourceDescription(Mockito.any(), Mockito.any()))
                .thenThrow(exception);

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.revokeResourceDescription(resourceDescriptionId, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling revokeResourceDescription");
    }

    @ParameterizedTest
    @MethodSource("revokeResourceDescriptionParameters")
    void testRevokeResourceDescriptionWithConnectException(String resourceDescriptionId, String tier1AccessToken)
            throws ConnectException {

        when(catalogueAdapterTier2ClientBuilder.build(any())).thenThrow(new ConnectException());

        // Call the method and assert that a RemoteServiceErrorException is thrown
        assertThrows(
                RemoteServiceErrorException.class,
                () -> resourceDescriptionService.revokeResourceDescription(resourceDescriptionId, tier1AccessToken),
                "Expected RemoteServiceErrorException when calling revokeResourceDescription");
    }
}
