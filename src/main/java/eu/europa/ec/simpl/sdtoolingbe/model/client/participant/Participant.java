package eu.europa.ec.simpl.sdtoolingbe.model.client.participant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Participant {

    private String id;
    private String organization;
}
