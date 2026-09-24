/*
 *   This file is part of the SD Creation Wizard API project.
 *   Originally derived from the sd-creation-wizard-api project (https://gitlab.eclipse.org/eclipse/xfsc/self-description-tooling/sd-creation-wizard-api),
 *   licensed under the Apache License Version 2.0.
 *   Substantial modifications have been made by Sovereign X to extend and refactor the functionality.
 *   All modifications are © 2024 Sovereign X and released under the EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 *   Portions of the original code remain under the Apache License, Version 2.0.
 *   See LICENSE and NOTICE files for details.
 */

package eu.europa.ec.simpl.sdtoolingbe.constant;

public final class Constants {

    public static final String NODE_KIND = "nodeKind";
    public static final String TTL = "TTL";
    public static final String JSON = "JSON";
    public static final String IRI = "IRI";
    public static final String URL = "URL";
    public static final String NODE = "node";
    public static final String ALIAS = "alias";
    public static final String VALUE = "value";
    public static final String PREFIX = "prefix";
    public static final String MIN_LENGTH = "minLength";
    public static final String MAX_LENGTH = "maxLength";
    public static final String MIN_INCLUSIVE = "minInclusive";
    public static final String MAX_INCLUSIVE = "maxInclusive";
    public static final String MIN_EXCLUSIVE = "minExclusive";
    public static final String MAX_EXCLUSIVE = "maxExclusive";

    // Private constructor to prevent instantiation of utility class.
    // This class contains only static members.
    private Constants() {
        throw new UnsupportedOperationException("Constants class should not be instantiated.");
    }

    public static final class Dct {

        // Dublin Core Terms
        public static final String DCT_PREFIX = "dct";

        public static final String DCT_ID_VALUE_PREFIX = "https://simpl.example.org/schema/";

        // JSON-LD dct root object
        public static final String JSONLD_DCT_ROOT = DCT_PREFIX + ":conformsTo";

        // JSON-LD dct properties
        public static final String JSONLD_DCT_ID = "@id";
        public static final String JSONLD_DCT_TYPE = "@type";
        public static final String JSONLD_DCT_STANDARD = DCT_PREFIX + ":Standard";
        public static final String JSONLD_DCT_SCHEMA_NAME = DCT_PREFIX + ":schemaName";
        public static final String JSONLD_DCT_TITLE = DCT_PREFIX + ":title";
        public static final String JSONLD_DCT_DESCRIPTION = DCT_PREFIX + ":description";
        public static final String JSONLD_DCT_HAS_VERSION = DCT_PREFIX + ":hasVersion";

        /**
         * Private constructor to prevent instantiation of utility class.
         * This class contains only static members.
         */
        private Dct() {
            throw new UnsupportedOperationException("Dct class should not be instantiated.");
        }
    }
}
