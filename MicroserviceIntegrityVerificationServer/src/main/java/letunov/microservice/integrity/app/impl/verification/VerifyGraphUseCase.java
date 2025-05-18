package letunov.microservice.integrity.app.impl.verification;

import letunov.microservice.integrity.app.api.repo.IssueRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.app.api.verification.VerifyGraphInbound;
import letunov.microservice.integrity.domain.issue.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class VerifyGraphUseCase implements VerifyGraphInbound {
    private final MicroserviceRepository microserviceRepository;
    private final IssueRepository issueRepository;
    private final MicroserviceVerificationService microserviceVerificationService;

    @Override
    @Transactional
    public void execute() {
        Set<Issue> issues = new HashSet<>();
        issueRepository.deleteAll();
        microserviceRepository.findAll().forEach(microservice -> issues.addAll(microserviceVerificationService.verify(List.of(microservice))));
        issueRepository.saveAll(new ArrayList<>(issues));
    }
}
