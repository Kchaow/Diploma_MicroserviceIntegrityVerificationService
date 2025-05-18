package letunov.microservice.integrity.app.api.graph;

import letunov.microservice.integrity.domain.graph.ChangeGraph;

public interface GetChangeGraphByIdInbound {
    ChangeGraph execute(String id);
}
