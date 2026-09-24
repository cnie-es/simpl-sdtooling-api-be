package eu.europa.ec.simpl.sdtoolingbe.service.authenticationprovider;

import eu.europa.ec.simpl.sdtoolingbe.model.client.identityattribute.IdentityAttribute;
import eu.europa.ec.simpl.sdtoolingbe.model.client.participant.Participant;
import java.util.List;

public interface AuthenticationProviderService {

    /**
     * Call the AuthenticationProvider microservice of the participant to get all agent
     * identity attributes.
     * @param bearerToken is the jwtToken to be used to authorize the call to
     *                    AuthenticationProvider microservice
     * @return a list of IdentityAttribute
     */
    List<IdentityAttribute> getIdentityAttributes(String bearerToken);

    /**
     * Returns the participant's information (i.e. the participant id).
     * @param bearerToken is the jwtToken to be used to authorize the call to
     *                    AuthenticationProvider microservice
     * @return Participant object
     */
    Participant getParticipant(String bearerToken);
}
