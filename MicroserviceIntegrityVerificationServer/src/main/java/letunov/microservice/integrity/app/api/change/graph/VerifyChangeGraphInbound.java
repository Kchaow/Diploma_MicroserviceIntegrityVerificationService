package letunov.microservice.integrity.app.api.change.graph;

public interface VerifyChangeGraphInbound {
    VerificationResult execute(String id);
}
