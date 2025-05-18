package letunov.microservice.integrity.app.impl.change.graph;

import letunov.microservice.integrity.app.api.change.graph.VerificationResult;
import letunov.microservice.integrity.app.api.change.graph.VerifyChangeGraphInbound;
import letunov.microservice.integrity.app.api.repo.ChangeGraphRepository;
import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.app.impl.graph.GetGraphDelegate;
import letunov.microservice.integrity.domain.contract.Contract;
import letunov.microservice.integrity.domain.contract.Edge;
import letunov.microservice.integrity.domain.contract.Node;
import letunov.microservice.integrity.domain.graph.ChangeGraph;
import letunov.microservice.integrity.domain.graph.Graph;
import letunov.microservice.integrity.domain.graph.GraphElementStatus;
import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.microservice.ConsumingRelationship;
import letunov.microservice.integrity.domain.microservice.Microservice;
import letunov.microservice.integrity.domain.microservice.UsesRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static letunov.microservice.integrity.domain.graph.GraphElementStatus.OK;

@Component
@RequiredArgsConstructor
public class VerifyChangeGraphUseCase implements VerifyChangeGraphInbound {
    private final ChangeGraphRepository changeGraphRepository;
    private final MicroserviceRepository microserviceRepository;
    private final GetGraphDelegate getGraphDelegate;
    private final MicroserviceVerificationService microserviceVerificationService;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public VerificationResult execute(String id) {
        ChangeGraph changeGraph = changeGraphRepository.findById(id)
            .orElseThrow(() -> new RuntimeException());
        if (changeGraph.getCommitedMicroservices().size() != changeGraph.getAssociatedMicroservices().size()) {
            throw new RuntimeException();
        }

        Map<String, Microservice> microservicesMap = convertToMicroservicesMap(changeGraph.getCommitedMicroservices());
        List<Microservice> microservicesThatRequireCurrent = getMicroservicesThatRequireCurrent(microservicesMap);
        List<Microservice> microservicesRequiredByCurrent = getRequiredMicroservices(microservicesMap);

        List<Microservice> microservicesToGetGraph = new ArrayList<>();
        microservicesToGetGraph.addAll(microservicesMap.values());
        microservicesToGetGraph.addAll(microservicesThatRequireCurrent);
        microservicesToGetGraph.addAll(microservicesRequiredByCurrent);

        List<Issue> issues = microserviceVerificationService.verify(microservicesMap.values().stream().toList());

        Graph graph = getGraphDelegate.execute(microservicesToGetGraph, issues);
        changeGraph.setGraph(graph);
        changeGraph.setChangeGraphStatus(ChangeGraphStatus.DONE);
        changeGraphRepository.save(changeGraph);


        return new VerificationResult(getGraphStatus(graph));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private GraphElementStatus getGraphStatus(Graph graph) {
        return Stream.concat(
            graph.getEdges().stream().map(Edge::getStatus),
            graph.getNodes().stream().map(Node::getStatus)
        )
            .filter(Objects::nonNull)
            .max(Comparator.comparingInt(GraphElementStatus::getLevel))
            .orElse(OK);
    }

    private List<Microservice> getRequiredMicroservices(Map<String, Microservice> microservicesMap) {
        return microservicesMap.values().stream()
            .flatMap(microservice -> microservice.getUsesMicroservices().stream())
            .map(UsesRelationship::getMicroservice)
            .filter(microservice -> !microservicesMap.containsKey(microservice.getName()))
            .distinct()
            .toList();
    }

    private Map<String, Microservice> convertToMicroservicesMap(List<MicroserviceInfo> microserviceInfoList) {
        Map<String, Microservice> microservices = microserviceInfoList.stream()
            .map(microserviceInfo -> new Microservice()
                .setName(microserviceInfo.getName())
                .setProvidingContracts(getProvidingContractsFrom(microserviceInfo.getProvidingContractInfoList()))
                .setConsumingContracts(getConsumingContractsFrom(microserviceInfo.getConsumingContractInfoList())))
            .collect(Collectors.toMap(Microservice::getName, identity()));
        return microservices.values().stream()
            .map(microservice -> microservice.setUsesMicroservices(getUsesMicroservices(microservice.getConsumingContracts(), microservices)))
            .collect(toMap(Microservice::getName, identity()));
    }

    private List<UsesRelationship> getUsesMicroservices(List<ConsumingRelationship> consumingContractInfoList, Map<String, Microservice> updatedMicroservices) {
        var usesMicroservices = consumingContractInfoList.stream()
            .map(ConsumingRelationship::getServiceName)
            .collect(toSet());
        Map<String, Microservice> microserviceMap = microserviceRepository.findByNameInGroupByName(usesMicroservices);
        return consumingContractInfoList.stream()
            .filter(consumingContractInfo -> microserviceMap.containsKey(consumingContractInfo.getServiceName()) ||
                updatedMicroservices.containsKey(consumingContractInfo.getServiceName()))
            .map(consumingRelationship -> new UsesRelationship()
                .setContractName(consumingRelationship.getContract().getName())
                .setMicroservice(
                    ofNullable(updatedMicroservices.get(consumingRelationship.getServiceName()))
                        .orElse(microserviceMap.get(consumingRelationship.getServiceName()))
                ))
            .toList();
    }

    private List<ConsumingRelationship> getConsumingContractsFrom(List<ConsumingContractInfo> consumingContractInfoList) {
        return consumingContractInfoList.stream()
            .map(this::createConsumingRelationship)
            .toList();
    }

    private ConsumingRelationship createConsumingRelationship(ConsumingContractInfo consumingContractInfo) {
        return new ConsumingRelationship()
            .setServiceName(consumingContractInfo.getServiceName())
            .setContract(contractRepository.findIdentical(consumingContractInfo)
                .orElse(createContract(consumingContractInfo)));
    }



    private List<Contract> getProvidingContractsFrom(List<ProvidingContractInfo> providingContractInfoList) {
        return providingContractInfoList.stream()
            .map(providingContractInfo -> contractRepository.findIdentical(providingContractInfo)
                .orElse(createContract(providingContractInfo)))
            .toList();
    }

    private Contract createContract(ProvidingContractInfo providingContractInfo) {
        return new Contract()
            .setName(providingContractInfo.getName())
            .setArtifactId(providingContractInfo.getDependencyInfo().getArtifactId())
            .setGroupId(providingContractInfo.getDependencyInfo().getGroupId())
            .setVersion(providingContractInfo.getDependencyInfo().getVersion())
            .setChecksum(providingContractInfo.getChecksum());
    }

    private Contract createContract(ConsumingContractInfo consumingContractInfo) {
        return new Contract()
            .setName(consumingContractInfo.getName())
            .setArtifactId(consumingContractInfo.getDependencyInfo().getArtifactId())
            .setGroupId(consumingContractInfo.getDependencyInfo().getGroupId())
            .setVersion(consumingContractInfo.getDependencyInfo().getVersion())
            .setChecksum(consumingContractInfo.getChecksum());
    }

    private List<Microservice> getMicroservicesThatRequireCurrent(Map<String, Microservice> microservicesMap) {
        List<String> microservicesNames = microservicesMap.values().stream()
            .map(Microservice::getName)
            .toList();
        return microserviceRepository.findMicroservicesRequiredMicroservices(microservicesNames).stream()
            .filter(microservice -> !microservicesMap.containsKey(microservice.getName()))
            .toList();
    }
}
