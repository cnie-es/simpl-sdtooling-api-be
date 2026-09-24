package eu.europa.ec.simpl.sdtoolingbe.constant;

public final class RequestMappingV1 {

    public static final String VERSION = "/v1";

    public static final String BASE = VERSION;

    public static final String CONTRACT_CONTROLLER = BASE + "/contract";

    public static final String POLICY_CONTROLLER = BASE + "/policies";
    public static final String RESOURCE_ADDRESS_CONTROLLER = BASE + "/resourceAddresses";
    public static final String RESOURCE_DESCRIPTION_CONTROLLER = BASE + "/resourceDescriptions";
    public static final String SD_CONTROLLER = BASE + "/selfDescriptions";
    public static final String SCHEMA_CONTROLLER = BASE + "/schemas";

    private RequestMappingV1() {}
}
