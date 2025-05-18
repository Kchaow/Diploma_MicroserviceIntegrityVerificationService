package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.change.graph.ChangeGraphInfo;
import letunov.microservice.integrity.app.api.change.graph.CreateChangeGraphInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Transactional
public class CreateChangeGraphUseCase implements CreateChangeGraphInbound {
    private final ChangeGraphRepository changeGraphRepository;

    @Override
    public ChangeGraphInfo execute(List<String> associatedMicroservices) {
        ChangeGraph changeGraph = new ChangeGraph();
        changeGraph.setAssociatedMicroservices(associatedMicroservices);
        changeGraph.setChangeGraphStatus(ChangeGraphStatus.WAIT_FOR_COMMIT);
        changeGraph.setDateTime(LocalDateTime.now());
        changeGraph = changeGraphRepository.save(changeGraph);

        return new ChangeGraphInfo(changeGraph.getId());
    }
}
