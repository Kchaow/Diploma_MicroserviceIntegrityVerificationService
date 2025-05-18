package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.app.api.graph.*;
import letunov.microservice.integrity.app.api.repo.IssueRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.domain.graph.Graph;
import letunov.microservice.integrity.domain.issue.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GetGraphUseCase implements GetGraphInbound {
    private final MicroserviceRepository microserviceRepository;
    private final IssueRepository issueRepository;
    private final GetGraphDelegate getGraphInfo;

    @Override
    @Transactional(readOnly = true)
    public Graph execute() {
        var microservices = microserviceRepository.findAll();
        List<Issue> issues = issueRepository.findAll();

        return getGraphInfo.execute(microservices, issues);
    }
}
