package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.change.graph.ChangeGraphsInfo;
import letunov.microservice.integrity.app.api.change.graph.GetChangeGraphsInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChangesGraphUseCase implements GetChangeGraphsInbound {
    private final ChangeGraphRepository changeGraphRepository;

    @Override
    public List<ChangeGraphsInfo> execute() {
        return changeGraphRepository.findAll().stream()
            .map(changeGraph -> new ChangeGraphsInfo()
                .setId(changeGraph.getId())
                .setCommitedMicroservices(changeGraph.getCommitedMicroservices().size())
                .setAssociatedMicroservices(changeGraph.getAssociatedMicroservices().size())
                .setStatus(changeGraph.getChangeGraphStatus())
                .setDateTime(changeGraph.getDateTime()))
            .toList();
    }
}
