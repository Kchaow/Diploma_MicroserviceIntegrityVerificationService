package letunov.microservice.integrity.app.api.repo;

import letunov.microservice.integrity.domain.graph.ChangeGraph;

import java.util.List;
import java.util.Optional;

public interface ChangeGraphRepository {
    ChangeGraph save(ChangeGraph changeGraph);

    Optional<ChangeGraph> findById(String id);

    List<ChangeGraph> findAll();

    List<ChangeGraph> findByAssociatedMicroservicesContains(String associatedMicroservice);
}
