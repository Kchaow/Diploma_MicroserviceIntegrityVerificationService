package letunov.microservice.integrity.app.impl.verification;

public class IssueDescriptionTemplates {
    public static String MICROSERVICE_DOES_NOT_EXIST = "Microservice '%s' doesn't exist";
    public static String CONTRACT_IS_NOT_PROVIDING = "Microservice '%s' doesn't provide contract '%s'";
    public static String CONTRACT_DIFFERENT_DEPENDENCY = "Contracts has different dependencies";
    public static String CONTRACT_DIFFERENT_VERSION = "Contracts has different versions";
    public static String CONTRACT_DIFFERENT_CHECKSUM = "Contracts has different checksum";
    public static String NOT_REQUIRED_CONTRACT = "Microservice '%s' has unused contract '%s'";

    private IssueDescriptionTemplates() {}
}
