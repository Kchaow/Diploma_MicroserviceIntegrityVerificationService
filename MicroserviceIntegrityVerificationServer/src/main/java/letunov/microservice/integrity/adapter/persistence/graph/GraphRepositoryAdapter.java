package letunov.microservice.integrity.adapter.persistence.graph;

import letunov.microservice.integrity.app.api.repo.GraphRepository;
import letunov.microservice.integrity.domain.graph.Graph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GraphRepositoryAdapter implements GraphRepository {
    private final GraphMongodbRepository graphMongodbRepository;

    @Override
    public Optional<Graph> findById(String id) {
        return graphMongodbRepository.findById(id);
    }

    @Override
    public Graph save(Graph graph) {
        return graphMongodbRepository.save(graph);
    }
}
