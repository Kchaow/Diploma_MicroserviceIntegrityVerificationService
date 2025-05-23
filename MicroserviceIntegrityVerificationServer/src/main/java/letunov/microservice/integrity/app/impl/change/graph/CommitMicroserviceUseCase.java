package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.change.graph.CommitMicroserviceInbound;
import letunov.microservice.integrity.app.api.change.graph.VerifyChangeGraphInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class CommitMicroserviceUseCase implements CommitMicroserviceInbound {
    private final ChangeGraphRepository changeGraphRepository;
    private final VerifyChangeGraphInbound verifyChangeGraphInbound;

    @Override
    public void execute(MicroserviceInfo microserviceInfo, String changeGraphId) {
        ChangeGraph changeGraph = changeGraphRepository.findById(changeGraphId)
            .orElseThrow(() -> new RuntimeException());
        if (!changeGraph.getAssociatedMicroservices().contains(microserviceInfo.getName())) {
            throw new RuntimeException();
        } else if (containsMicroserviceWithSameName(changeGraph.getCommitedMicroservices(), microserviceInfo)) {
            throw new RuntimeException();
        }

        changeGraphRepository.save(changeGraph);
        changeGraph.getCommitedMicroservices().add(microserviceInfo);
        if (changeGraph.getCommitedMicroservices().size() == changeGraph.getAssociatedMicroservices().size()) {
//            changeGraph.setChangeGraphStatus(ChangeGraphStatus.WAIT_FOR_VERIFY);
            verifyChangeGraphInbound.execute(changeGraph);
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private boolean containsMicroserviceWithSameName(List<MicroserviceInfo> microserviceInfoList, MicroserviceInfo target) {
        return microserviceInfoList.stream().anyMatch(microserviceInfo -> microserviceInfo.getName().equals(target.getName()));
    }
}
