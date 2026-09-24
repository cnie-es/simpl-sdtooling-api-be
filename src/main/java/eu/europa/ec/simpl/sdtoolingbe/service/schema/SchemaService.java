package eu.europa.ec.simpl.sdtoolingbe.service.schema;

import eu.europa.ec.simpl.data1.common.exception.BadRequestException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Deprecated(since = "v1.22.0", forRemoval = true)
/**
 * @deprecated to be removed when v1 schema controller will be delete as well
 */
public interface SchemaService {

    String getTtlFileContent(String ecosystem, String name) throws IOException, BadRequestException;

    Map<String, Map<String, List<String>>> getAvailableTtlFiles();

    Map<String, List<String>> getAvailableTtlFiles(String ecosystem);
}
