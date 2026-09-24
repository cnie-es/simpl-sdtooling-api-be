/*
 *   This file is part of the SD Creation Wizard API project.
 *   Originally derived from the sd-creation-wizard-api project (https://gitlab.eclipse.org/eclipse/xfsc/self-description-tooling/sd-creation-wizard-api),
 *   licensed under the Apache License Version 2.0.
 *   Substantial modifications have been made by Sovereign X to extend and refactor the functionality.
 *   All modifications are © 2024 Sovereign X and released under the EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 *   Portions of the original code remain under the Apache License, Version 2.0.
 *   See LICENSE and NOTICE files for details.
 */

package eu.europa.ec.simpl.sdtoolingbe.controller.v1;

import eu.europa.ec.simpl.data1.common.constant.CommonConstants;
import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.exception.BadRequestException;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.sdtoolingbe.service.schema.SchemaService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Deprecated(since = "v1.22.0", forRemoval = true)
/**
 * @deprecated to be removed when FE will be aligned using the V2 schema controller
 */
@RestController("shapeControllerV1")
@Log4j2
@RequiredArgsConstructor
@SuppressWarnings("removal")
public class SchemaControllerImpl extends AbstractController implements SchemaController {

    private static final String ERRROR_MESSAGE_FILE_NOT_FOUND = "File not found";

    private final SchemaService schemaService;

    @LogRequest
    @Override
    public ResponseEntity<String> getSchemaContent(String schemaId) {
        try {
            log.debug("getSchemaContent(): invoking schemaService.getTtlFileContent() for schemaId '{}'", schemaId);
            String content = schemaService.getTtlFileContent(CommonConstants.ECOSYSTEM, schemaId);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            log.error("File not found: {} ", e.getMessage());
            throw new BadRequestException(ERRROR_MESSAGE_FILE_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal file path: {}", e.getMessage());
            throw new BadRequestException("Illegal file path", e);
        }
    }

    @LogRequest
    @Override
    public ResponseEntity<Map<String, List<String>>> getSchemas() {
        log.debug("getAvailableTtlFiles(): invoking schemaService.getAvailableTtlFiles()");
        return ResponseEntity.ok(schemaService.getAvailableTtlFiles(CommonConstants.ECOSYSTEM));
    }
}
