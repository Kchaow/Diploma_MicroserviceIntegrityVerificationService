package letunov.microservice.integrity.app.api.change.graph;

import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;

public interface CommitMicroserviceInbound {
    void execute(MicroserviceInfo microserviceInfo, String changeGraphId);
}
