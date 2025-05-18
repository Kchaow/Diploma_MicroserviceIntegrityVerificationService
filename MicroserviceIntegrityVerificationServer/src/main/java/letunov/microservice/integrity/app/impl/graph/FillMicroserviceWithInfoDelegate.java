package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.domain.contract.Contract;
import letunov.microservice.integrity.domain.microservice.ConsumingRelationship;
import letunov.microservice.integrity.domain.microservice.Microservice;
import letunov.microservice.integrity.domain.microservice.UsesRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class FillMicroserviceWithInfoDelegate {
    private final ContractRepository contractRepository;
    private final MicroserviceRepository microserviceRepository;

    public Microservice execute(Microservice microservice, MicroserviceInfo microserviceInfo) {
        return microservice.setConsumingContracts(getConsumingContractsFrom(microserviceInfo.getConsumingContractInfoList(), microserviceInfo))
            .setProvidingContracts(getProvidingContractsFrom(microserviceInfo.getProvidingContractInfoList()))
            .setUsesMicroservices(getUsesMicroservices(microserviceInfo.getConsumingContractInfoList()));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private List<ConsumingRelationship> getConsumingContractsFrom(List<ConsumingContractInfo> consumingContractInfoList, MicroserviceInfo microserviceInfo) {
        return consumingContractInfoList.stream()
            .map(this::createConsumingRelationship)
            .toList();
    }

    private List<Contract> getProvidingContractsFrom(List<ProvidingContractInfo> providingContractInfoList) {
        return providingContractInfoList.stream()
            .map(providingContractInfo -> contractRepository.findIdentical(providingContractInfo)
                .orElse(createContract(providingContractInfo)))
            .toList();
    }

    private List<UsesRelationship> getUsesMicroservices(List<ConsumingContractInfo> consumingContractInfoList) {
        var usesMicroservices = consumingContractInfoList.stream()
            .map(ConsumingContractInfo::getServiceName)
            .collect(toSet());
        Map<String, Microservice> microserviceMap = microserviceRepository.findByNameInGroupByName(usesMicroservices);
        return consumingContractInfoList.stream()
            .filter(consumingContractInfo -> microserviceMap.containsKey(consumingContractInfo.getServiceName()))
            .map(consumingContractInfo -> new UsesRelationship()
                .setContractName(consumingContractInfo.getName())
                .setMicroservice(microserviceMap.get(consumingContractInfo.getServiceName())))
            .toList();
    }

    private ConsumingRelationship createConsumingRelationship(ConsumingContractInfo consumingContractInfo) {
        return new ConsumingRelationship()
            .setServiceName(consumingContractInfo.getServiceName())
            .setContract(contractRepository.findIdentical(consumingContractInfo)
                .orElse(createContract(consumingContractInfo)));
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
}
