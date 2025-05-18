package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.app.api.repo.IssueRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.app.api.graph.UpdateGraphWithMicroserviceInbound;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.microservice.Microservice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static letunov.microservice.integrity.domain.issue.IssueType.MICROSERVICE_DOES_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class UpdateGraphWithMicroserviceUseCase implements UpdateGraphWithMicroserviceInbound {
    private final MicroserviceVerificationService microserviceVerificationService;
    private final IssueRepository issueRepository;
    private final ContractRepository contractRepository;
    private final MicroserviceRepository microserviceRepository;
    private final FillMicroserviceWithInfoDelegate fillMicroservice;
    private final GetUsesRelationshipsFromConsumingDelegate getUsesRelationshipsFromConsuming;

    @Transactional
    @Override
    public void execute(MicroserviceInfo microserviceInfo) {
        var microserviceOpt = microserviceRepository.findByName(microserviceInfo.getName());

        clearRelationships(microserviceInfo);

        var microservice = fillMicroservice.execute(
            microserviceOpt.orElse(new Microservice().setName(microserviceInfo.getName())),
            microserviceInfo
        );
        microservice = microserviceRepository.save(microservice);

        if (microserviceOpt.isEmpty()) {
            var microservicesThatRequire = microserviceRepository.findMicroservicesRequiredMicroservice(microservice.getName());
            var finalMicroservice = microservice;
            microservicesThatRequire
                .forEach(consumingMicroservice -> consumingMicroservice.getUsesMicroservices()
                    .addAll(getUsesRelationshipsFromConsuming.execute(consumingMicroservice, finalMicroservice)));
            microserviceRepository.saveAll(microservicesThatRequire);
        }

        verify(microservice);
        contractRepository.deleteAllWithoutRelationships();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void verify(Microservice microservice) {
        clearIssues(microservice);
        var issues = microserviceVerificationService.verify(List.of(microservice));
        issueRepository.saveAll(issues.stream().distinct().toList());
    }

    private void clearRelationships(MicroserviceInfo microserviceInfo) {
        microserviceRepository.clearConsumingRelationships(microserviceInfo.getName());
        microserviceRepository.clearUsesRelationships(microserviceInfo.getName());
        microserviceRepository.clearProvidingRelationships(microserviceInfo.getName());
    }

    private void clearIssues(Microservice microservice) {
        List<Issue> issuesToDelete = new ArrayList<>();

        var doesNotExistIssues = issueRepository.findByIssueTypeAndCausedAsProviderByAsString(MICROSERVICE_DOES_NOT_EXIST, microservice.getName());
        var issuesCausedByMicroservice = issueRepository.findAllCausedBy(microservice.getName());
        issuesToDelete.addAll(doesNotExistIssues);
        issuesToDelete.addAll(issuesCausedByMicroservice);

        issueRepository.deleteAll(issuesToDelete);
    }
}
