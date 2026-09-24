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
import java.util.List;
import java.util.Map;

/**
 * Return object that has the entire shape graph with all properties including prefix info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShaclModel {

    private final List<Map<String, String>> prefixList;
    private final List<VicShape> shapes;

    public ShaclModel(List<Map<String, String>> prefixList, List<VicShape> shapes) {
        this.prefixList = prefixList;
        this.shapes = shapes;
    }

    public List<Map<String, String>> getPrefixList() {
        return prefixList;
    }

    public List<VicShape> getShapes() {
        return shapes;
    }
}
