package eu.europa.ec.simpl.sdtoolingbe.service.resourcedescription;

/**
 * Interface for ResourceDescriptionService providing methods to interact with search functionality.
 */
public interface ResourceDescriptionService {

    /**
     * Get all resource description using the CatalogueAdapterTier2Client.
     *
     * @param orderBy          Search params
     * @param tier1BearerToken Tier1 bearer token for authentication
     * @return Results as a string
     */
    String getAllResourceDescriptions(String orderBy, String tier1BearerToken);

    /**
     * Get resource description using the CatalogueAdapterTier2Client.
     *
     * @param resourceDescriptionId resource description id
     * @param tier1BearerToken      Tier1 bearer token for authentication
     * @return Results as a string
     */
    String getResourceDescription(String resourceDescriptionId, String tier1BearerToken);

    /**
     * Revoke resource description using the CatalogueAdapterTier2Client.
     *
     * @param resourceDescriptionId resource description id
     * @param tier1BearerToken      Tier1 bearer token for authentication
     * @return Results as a string
     */
    String revokeResourceDescription(String resourceDescriptionId, String tier1BearerToken);
}
