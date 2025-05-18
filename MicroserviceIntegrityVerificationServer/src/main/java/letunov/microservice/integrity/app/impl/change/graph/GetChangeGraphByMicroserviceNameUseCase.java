package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.change.graph.GetChangeGraphByMicroserviceNameInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChangeGraphByMicroserviceNameUseCase implements GetChangeGraphByMicroserviceNameInbound {
    private final ChangeGraphRepository changeGraphRepository;

    @Override
    @Transactional
    public List<String> execute(String name) {
        return changeGraphRepository.findByAssociatedMicroservicesContains(name).stream()
            .map(ChangeGraph::getId)
            .toList();
    }
}
