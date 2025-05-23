package letunov.microservice.integrity.app.api.change.graph;

import letunov.microservice.integrity.domain.graph.ChangeGraph;

public interface VerifyChangeGraphInbound {
    VerificationResult execute(String id);

    void execute(ChangeGraph changeGraph);
}
