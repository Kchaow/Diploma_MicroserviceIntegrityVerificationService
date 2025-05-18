package letunov.microservice.integrity.app.api.graph;

import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;

public interface UpdateGraphWithMicroserviceInbound {
    void execute(MicroserviceInfo microserviceInfo);
}
