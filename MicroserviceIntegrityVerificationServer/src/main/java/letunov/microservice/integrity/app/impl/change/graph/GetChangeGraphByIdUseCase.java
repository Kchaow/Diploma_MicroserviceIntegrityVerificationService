package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.graph.GetChangeGraphByIdInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetChangeGraphByIdUseCase implements GetChangeGraphByIdInbound {
    private final ChangeGraphRepository changeGraphRepository;

    @Override
    public ChangeGraph execute(String id) {
        var changeGraph = changeGraphRepository.findById(id)
            .orElseThrow(() -> new RuntimeException());
        if (changeGraph.getChangeGraphStatus() != ChangeGraphStatus.DONE) {
            throw new RuntimeException();
        }
        return changeGraph;
    }
}
