package eu.europa.ec.simpl.sdtoolingbe.service.resourceaddress;

import eu.europa.ec.simpl.data1.common.adapter.connector.model.resourceaddress.ResourceAddress;
import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import java.io.IOException;
import java.util.List;

public interface ResourceAddressService {

    List<String> getResourceSharingMethods(OfferType offerType);

    List<Template> getSourceAddressTemplates(OfferType offerType, String sharingMethodId);

    String getSourceAddressSchema(String templateId) throws IOException;

    String getSourceAddressUiSchema(String templateId) throws IOException;

    ResourceAddress getResourceAddress(String bearerToken, String assetId);
}
