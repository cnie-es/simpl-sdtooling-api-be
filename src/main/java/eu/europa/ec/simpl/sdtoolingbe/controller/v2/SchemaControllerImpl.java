/*
 *   This file is part of the SD Creation Wizard API project.
 *   Originally derived from the sd-creation-wizard-api project (https://gitlab.eclipse.org/eclipse/xfsc/self-description-tooling/sd-creation-wizard-api),
 *   licensed under the Apache License Version 2.0.
 *   Substantial modifications have been made by Sovereign X to extend and refactor the functionality.
 *   All modifications are © 2024 Sovereign X and released under the EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 *   Portions of the original code remain under the Apache License, Version 2.0.
 *   See LICENSE and NOTICE files for details.
 */

package eu.europa.ec.simpl.sdtoolingbe.controller.v2;

import eu.europa.ec.simpl.data1.common.controller.AbstractController;
import eu.europa.ec.simpl.data1.common.logging.LogRequest;
import eu.europa.ec.simpl.data1.common.model.schemasync.SchemaMetadataWrapper;
import eu.europa.ec.simpl.data1.common.service.schemasyncclient.SchemaSyncClientService;
import eu.europa.ec.simpl.data1.common.service.schemasyncrepo.SchemaSyncRepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("schemaControllerV2")
@Log4j2
@RequiredArgsConstructor
public class SchemaControllerImpl extends AbstractController implements SchemaController {

    private final SchemaSyncRepoService schemaSyncRepoService;
    private final SchemaSyncClientService schemaSyncClientService;

    @LogRequest
    @Override
    public ResponseEntity<String> getSchemaContent(String schemaId) {
        log.debug("getSchemaContent(): invoking schemaSyncRepoService.getSchemaContent() for schemaId '{}'", schemaId);
        String content = schemaSyncRepoService.getSchemaContent(schemaId);
        return ResponseEntity.ok(content);
    }

    @LogRequest
    @Override
    public ResponseEntity<SchemaMetadataWrapper> getSchemas(String resourceType) {
        log.debug(
                "getSchemas(): invoking schemaSyncRepoService.getSchemaMetadatasWrapped() with resourceType '{}'",
                resourceType);
        return ResponseEntity.ok(schemaSyncRepoService.getSchemaMetadatasWrapped(resourceType));
    }

    @LogRequest
    @Override
    public ResponseEntity<String> getSchemaByVersion(String schemaId, String version) {
        log.debug(
                "getSchemas(): invoking schemaSyncClientService.getOrFetchSchemaVersionedContent() with schemaId '{}' and version '{}'",
                schemaId,
                version);
        String content = schemaSyncClientService.getOrFetchSchemaVersionedContent(schemaId, version);
        return ResponseEntity.ok(content);
    }
}
