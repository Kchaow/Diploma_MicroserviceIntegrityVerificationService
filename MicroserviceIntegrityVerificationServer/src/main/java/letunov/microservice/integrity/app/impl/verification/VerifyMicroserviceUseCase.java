package letunov.microservice.integrity.app.impl.verification;

import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.app.api.repo.GraphRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.app.api.verification.VerificationInfo;
import letunov.microservice.integrity.app.api.verification.VerifyMicroserviceInbound;
import letunov.microservice.integrity.app.impl.graph.FillMicroserviceWithInfoDelegate;
import letunov.microservice.integrity.app.impl.graph.GetGraphDelegate;
import letunov.microservice.integrity.domain.microservice.Microservice;
import letunov.microservice.integrity.domain.microservice.UsesRelationship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyMicroserviceUseCase implements VerifyMicroserviceInbound {
    private final MicroserviceVerificationService microserviceVerificationService;
    private final MicroserviceRepository microserviceRepository;
    private final FillMicroserviceWithInfoDelegate fillMicroservice;
    private final GetGraphDelegate getGraph;
    private final GraphRepository graphRepository;

    @Value("${integration.front-url}")
    private String frontUrl;

    @Override
    @Transactional
    public VerificationInfo execute(MicroserviceInfo microserviceInfo) {
        var microservice = fillMicroservice.execute(
            new Microservice().setName(microserviceInfo.getName()),
            microserviceInfo
        );

        List<Microservice> microservicesToGetGraph = microserviceRepository.findMicroservicesRequiredMicroservice(microservice.getName()).stream()
            .map(microserviceThatRequired -> getFakeCloneWithRelationShip(microserviceThatRequired, microservice))
            .collect(Collectors.toCollection(ArrayList::new)); //Возможны проблемы при обоюдном потреблении
        microservicesToGetGraph.addAll(getRequiredMicroservices(microservice));
        microservicesToGetGraph.add(microservice);

        var issues = microserviceVerificationService.verify(List.of(microservice));
        var graph = getGraph.execute(microservicesToGetGraph, issues);
        graph = graphRepository.save(graph);

//        return new VerificationInfo()
//            .setGraphLink(frontUrl + "/graph/%s".formatted(graph.getId()))
//            .setMessages(graph.getMessages());
        return null;
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private List<Microservice> getRequiredMicroservices(Microservice microservice) {
        return microservice.getUsesMicroservices().stream()
            .map(UsesRelationship::getMicroservice)
            .distinct()
            .toList();
    }

    private Microservice getFakeCloneWithRelationShip(Microservice microservice, Microservice microserviceToVerify) {
        var fake = new Microservice()
            .setName(microservice.getName())
            .setConsumingContracts(microservice.getConsumingContracts())
            .setProvidingContracts(microservice.getProvidingContracts())
            .setUsesMicroservices(microservice.getUsesMicroservices());
        var additionalRelationships = microservice.getConsumingContracts().stream()
            .filter(consumingRelationship -> consumingRelationship.getServiceName().equals(microserviceToVerify.getName()))
                .map(consumingRelationship -> new UsesRelationship()
                    .setMicroservice(microserviceToVerify)
                    .setContractName(consumingRelationship.getContract().getName())
                    .setId(String.valueOf(consumingRelationship.getContract().getName().hashCode())))
            .toList();
        fake.getUsesMicroservices().addAll(additionalRelationships);
        return fake;
    }
}
