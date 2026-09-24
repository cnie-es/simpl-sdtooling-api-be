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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintOption {
    private final String key;
    private final Object value;

    public ConstraintOption(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
