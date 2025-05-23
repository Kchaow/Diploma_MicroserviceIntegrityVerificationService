package letunov.microservice.integrity.adapter.persistence.graph;

import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus.WAIT_FOR_COMMIT;

@Repository
@RequiredArgsConstructor
public class ChangeGraphRepositoryAdapter implements ChangeGraphRepository {
    private final ChangeGraphMongodbRepository changeGraphMongodbRepository;

    @Override
    public ChangeGraph save(ChangeGraph changeGraph) {
        return changeGraphMongodbRepository.save(changeGraph);
    }

    @Override
    public Optional<ChangeGraph> findById(String id) {
        return changeGraphMongodbRepository.findById(id);
    }

    @Override
    public List<ChangeGraph> findAll() {
        return changeGraphMongodbRepository.findAll();
    }

    @Override
    public List<ChangeGraph> findByAssociatedMicroservicesContains(String associatedMicroservice) {
        return changeGraphMongodbRepository.findByAssociatedMicroservicesContainsAndChangeGraphStatusIs(associatedMicroservice, WAIT_FOR_COMMIT);
    }
}
