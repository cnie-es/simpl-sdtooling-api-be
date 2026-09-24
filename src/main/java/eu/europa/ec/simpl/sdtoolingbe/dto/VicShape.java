/*
 *   This file is part of the SD Creation Wizard API project.
 *   Originally derived from the sd-creation-wizard-api project (https://gitlab.eclipse.org/eclipse/xfsc/self-description-tooling/sd-creation-wizard-api),
 *   licensed under the Apache License Version 2.0.
 *   Substantial modifications have been made by Sovereign X to extend and refactor the functionality.
 *   All modifications are © 2024 Sovereign X and released under the EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 *   Portions of the original code remain under the Apache License, Version 2.0.
 *   See LICENSE and NOTICE files for details.
 */

package eu.europa.ec.simpl.sdtoolingbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VicShape {
    private final String schema;
    private final String targetClassPrefix;
    private final String targetClassName;
    private final List<ShapeProperties> constraints;

    public VicShape(
            List<ShapeProperties> constraints, String schema, String targetClassPrefix, String targetClassName) {
        this.targetClassPrefix = targetClassPrefix;

        if (constraints == null) {
            this.constraints = new ArrayList<>();
        } else {
            this.constraints = new ArrayList<>(constraints);
        }

        this.schema = schema;
        this.targetClassName = targetClassName;
    }

    public String getSchema() {
        return schema;
    }

    public String getTargetClassPrefix() {
        return targetClassPrefix;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public List<ShapeProperties> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }
}
