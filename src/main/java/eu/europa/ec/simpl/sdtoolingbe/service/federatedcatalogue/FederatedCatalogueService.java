package eu.europa.ec.simpl.sdtoolingbe.service.federatedcatalogue;

import com.fasterxml.jackson.databind.JsonNode;

public interface FederatedCatalogueService {

    /**
     * Call the Tier2 gateway using the FederatedCatalogueTier2Client passing the
     * federated catalogue access token (to be changed in the future: catalogue
     * should use tier1 access token)
     *
     * @param sdJsonLd the SD in JSON-LD format to be published
     * @param tier1BearerToken used to request the participant credential
     *                         certificate on the fly
     * @return the SD in JSON-LD
     * @throws Exception if an error occurs during the publication process
     */
    JsonNode publishSD(JsonNode sdJsonLd, String tier1BearerToken);
}
