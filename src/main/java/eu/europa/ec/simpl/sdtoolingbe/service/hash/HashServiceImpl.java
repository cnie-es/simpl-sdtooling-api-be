package eu.europa.ec.simpl.sdtoolingbe.service.hash;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.simpl.data1.common.util.SDUtil;
import eu.europa.ec.simpl.sdtoolingbe.dto.HashModel;
import eu.europa.ec.simpl.sdtoolingbe.properties.HashProperties;
import eu.europa.ec.simpl.sdtoolingbe.util.HashGeneratorUtil;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class HashServiceImpl implements HashService {

    private final String hashAlg;
    private final List<HashModel> hashModelList;

    private final HashProperties hashProperties;

    private final HashGeneratorUtil hashGeneratorUtil;

    public HashServiceImpl(HashProperties hashProperties, HashGeneratorUtil hashGeneratorUtil) {
        this.hashProperties = hashProperties;
        this.hashAlg = hashProperties.getHashAlgorithm();
        this.hashModelList = hashProperties.getHashModelList();
        this.hashGeneratorUtil = hashGeneratorUtil;
    }

    @Override
    public void generateHashFromJsonLd(JsonNode jsonLd) throws IOException {
        log.debug("generateHashFromJsonLd() for jsonLd {}", jsonLd);

        for (HashModel hashModel : hashModelList) {
            generateHashJsonLd(jsonLd, hashModel);
        }
    }

    private void generateHashJsonLd(JsonNode jsonFile, HashModel hashModel) throws IOException {
        log.info("generateHashJsonLd() for jsonFile {} and hashModel {}", jsonFile, hashModel);

        String hashKey = SDUtil.NS + hashModel.getHashObj();

        if (jsonFile.has(hashKey)) {
            JsonNode hashObj = jsonFile.get(hashKey);
            log.debug("generateHashJsonLd(): found object for key '{}': {}", hashKey, hashObj);

            String hashDoc = hashObj.get(SDUtil.NS + hashModel.getHashDoc()).textValue();
            log.info("generateHashJsonLd(): hash document value '{}'", hashModel.getHashDoc());

            String hashValue = hashGeneratorUtil.generateHashValue(hashDoc);
            String hashUrl = hashProperties.getValueFromKey(hashDoc);

            ObjectNode hashObjNew = (ObjectNode) hashObj;

            hashObjNew.put(SDUtil.NS + hashModel.getHashAlg(), hashAlg);
            log.info("generateHashJsonLd(): put hash alg '{}'", hashAlg);

            hashObjNew.put(SDUtil.NS + hashModel.getHashValue(), hashValue);
            log.info("generateHashJsonLd(): put hash value '{}'", hashValue);

            hashObjNew.put(SDUtil.NS + hashModel.getHashUrl(), hashUrl);
            log.info("generateHashJsonLd(): put hash url '{}'", hashUrl);
        }
    }
}
