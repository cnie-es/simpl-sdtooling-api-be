/*
 *   This file is part of the SD Creation Wizard API project.
 *   Originally derived from the sd-creation-wizard-api project (https://gitlab.eclipse.org/eclipse/xfsc/self-description-tooling/sd-creation-wizard-api),
 *   licensed under the Apache License Version 2.0.
 *   Substantial modifications have been made by Sovereign X to extend and refactor the functionality.
 *   All modifications are © 2024 Sovereign X and released under the EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 *   Portions of the original code remain under the Apache License, Version 2.0.
 *   See LICENSE and NOTICE files for details.
 */

package eu.europa.ec.simpl.sdtoolingbe.service.schema;

import eu.europa.ec.simpl.data1.common.exception.BadRequestException;
import eu.europa.ec.simpl.sdtoolingbe.constant.Constants;
import eu.europa.ec.simpl.sdtoolingbe.util.ShaclFileUtil;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@Deprecated(since = "v1.22.0", forRemoval = true)
/**
 * @deprecated to be removed when SchemaController V1 will be removed
 */
@SuppressWarnings("removal")
public class SchemaServiceImpl implements SchemaService {

    private Map<String, Map<String, List<String>>> ecosystemToCategoryToShaclFileMap;

    @PostConstruct
    public void init() {
        log.info("init()");
        ecosystemToCategoryToShaclFileMap = createEcosystemToShaclFileMap();
        log.info("init() completed: {}", ecosystemToCategoryToShaclFileMap);
    }

    @Override
    public String getTtlFileContent(String ecosystem, String filename) throws IOException, BadRequestException {
        String methodName = "getTtlFileContent()";
        log.debug("{}: ecosystem='{}', filename='{}'", methodName, ecosystem, filename);

        Map<String, List<String>> mapping = ecosystemToCategoryToShaclFileMap.get(ecosystem);

        if (mapping == null) {
            log.error("{}: Error reading shape files", methodName);
            throw new IllegalStateException("Error reading shape files");
        }

        String schemaType = null;
        for (Map.Entry<String, List<String>> entry : mapping.entrySet()) {
            if (entry.getValue().contains(filename)) {
                schemaType = entry.getKey();
                break;
            }
        }

        if (schemaType == null) {
            log.error("{}: File not found: {} in ecosystem: {}", methodName, filename, ecosystem);
            throw new BadRequestException("File not found: " + filename);
        }

        return Files.readString(ShaclFileUtil.getShapesFilePath(ecosystem, schemaType, filename));
    }

    @Override
    public Map<String, Map<String, List<String>>> getAvailableTtlFiles() {
        if (ecosystemToCategoryToShaclFileMap == null) {
            return Collections.emptyMap();
        }

        return ecosystemToCategoryToShaclFileMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, ecosystemEntry -> ecosystemEntry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        categoryEntry -> new ArrayList<>(categoryEntry.getValue())))));
    }

    @Override
    public Map<String, List<String>> getAvailableTtlFiles(String ecosystem) {
        if (ecosystemToCategoryToShaclFileMap == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = ecosystemToCategoryToShaclFileMap.get(ecosystem);
        if (map == null) {
            return Collections.emptyMap();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
    }

    private static Map<String, Map<String, List<String>>> createEcosystemToShaclFileMap() {
        Collection<File> files = FileUtils.listFiles(
                ShaclFileUtil.getShapesDir(),
                FileFilterUtils.suffixFileFilter(Constants.TTL, IOCase.INSENSITIVE),
                TrueFileFilter.INSTANCE);

        Map<String, Map<String, List<File>>> filesToCategoryAndEcosystemMap = files.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getParentFile().getParentFile().getName(),
                        Collectors.groupingBy(file -> file.getParentFile().getName())));

        Map<String, Map<String, List<String>>> ecosystemToShaclFileMap = new HashMap<>();

        filesToCategoryAndEcosystemMap.forEach((String ecosystem, Map<String, List<File>> categoryMap) -> {
            Map<String, List<String>> filesToCategory = new HashMap<>();
            categoryMap.forEach((String category, List<File> shaclFiles) -> {
                List<String> filenames = new ArrayList<>();
                shaclFiles.forEach(file -> filenames.add(file.getName()));
                filesToCategory.put(category, filenames);
            });

            ecosystemToShaclFileMap.put(ecosystem, filesToCategory);
        });

        return ecosystemToShaclFileMap;
    }
}
