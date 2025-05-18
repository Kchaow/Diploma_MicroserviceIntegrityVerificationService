package letunov.microservice.integrity.app.api.repo;

import letunov.microservice.integrity.domain.graph.Graph;

import java.util.Optional;

public interface GraphRepository {
    Optional<Graph> findById(String id);

    Graph save(Graph graph);
}
