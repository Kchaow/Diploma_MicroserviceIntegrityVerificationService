package letunov.microservice.integrity.adapter.persistence.contract;


import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.domain.contract.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContractRepositoryAdapter implements ContractRepository {
    private final ContractNeo4jRepository contractNeo4jRepository;

    @Override
    public Map<String, List<Contract>> findProvidedContractsByMicroservices(List<String> microservicesNames) {
        Map<String, List<Contract>> providedContractsByMicroservices = new HashMap<>();
        contractNeo4jRepository.findProvidedContractsByMicroservices(microservicesNames)
            .forEach(microserviceContracts -> providedContractsByMicroservices.put(microserviceContracts.getMicroserviceName(), microserviceContracts.getContracts()));
        return providedContractsByMicroservices;
    }

    @Override
    public Map<String, List<Contract>> findRequiredContractsByMicroservices(String microserviceName) {
        Map<String, List<Contract>> requiredContractsByMicroservices = new HashMap<>();
        contractNeo4jRepository.findRequiredContractsByMicroservices(microserviceName)
            .forEach(microserviceContracts -> requiredContractsByMicroservices.put(microserviceContracts.getMicroserviceName(), microserviceContracts.getContracts()));
        return requiredContractsByMicroservices;
    }

    @Override
    public Optional<Contract> findIdentical(ProvidingContractInfo providingContractInfo) {
        var result = contractNeo4jRepository.findAllByNameAndArtifactIdAndGroupIdAndVersionAndChecksum(providingContractInfo.getName(),
            providingContractInfo.getDependencyInfo().getArtifactId(),
            providingContractInfo.getDependencyInfo().getGroupId(),
            providingContractInfo.getDependencyInfo().getVersion(),
            providingContractInfo.getChecksum());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Override
    public Optional<Contract> findIdentical(ConsumingContractInfo consumingContractInfo) {
        var result = contractNeo4jRepository.findAllByNameAndArtifactIdAndGroupIdAndVersionAndChecksum(consumingContractInfo.getName(),
            consumingContractInfo.getDependencyInfo().getArtifactId(),
            consumingContractInfo.getDependencyInfo().getGroupId(),
            consumingContractInfo.getDependencyInfo().getVersion(),
            consumingContractInfo.getChecksum());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Override
    public void deleteAllWithoutRelationships() {
        contractNeo4jRepository.deleteAllWithoutRelationships();
    }
}
