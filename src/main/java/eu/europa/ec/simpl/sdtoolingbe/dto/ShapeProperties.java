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
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ShapeProperties {
    private final ClassConstraint path;
    private final String name;
    private final Map<String, String> datatype;
    private final ClassConstraint clazz;
    private final Integer minCount;
    private final Integer maxCount;
    private final Map<String, String> description;
    private final String example;
    private final List<ClassConstraint> in;
    private final List<ClassConstraint> configure;
    private final Integer order;
    private final List<ConstraintOption> validations;
    private final String children;
    private final List<ShapeProperties> or;

    public ShapeProperties(
            ClassConstraint path,
            String name,
            Map<String, String> datatype,
            ClassConstraint clazz,
            Integer minCount,
            Integer maxCount,
            List<ClassConstraint> in,
            Integer order,
            List<ConstraintOption> validations,
            String children,
            Map<String, String> description,
            String example,
            List<ClassConstraint> configure,
            List<ShapeProperties> or) {
        this.path = path;
        this.name = name;
        this.datatype = datatype;
        this.clazz = clazz;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.in = in;
        this.order = order;
        this.validations = validations;
        this.children = children;
        this.description = description;
        this.example = example;
        this.configure = configure;
        this.or = or;
    }

    public ShapeProperties(
            ClassConstraint path,
            String name,
            Map<String, String> datatype,
            ClassConstraint clazz,
            Integer minCount,
            Integer maxCount) {
        this(path, name, datatype, clazz, minCount, maxCount, null, null, null, null, null, null, null, null);
    }
}
