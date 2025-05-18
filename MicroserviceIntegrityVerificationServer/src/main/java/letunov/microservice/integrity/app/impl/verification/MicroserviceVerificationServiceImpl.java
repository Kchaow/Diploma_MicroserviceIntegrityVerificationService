package letunov.microservice.integrity.app.impl.verification;

import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.domain.contract.Contract;
import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.issue.IssueLevel;
import letunov.microservice.integrity.domain.issue.IssueType;
import letunov.microservice.integrity.domain.microservice.ConsumingRelationship;
import letunov.microservice.integrity.domain.microservice.Microservice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.List.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static letunov.microservice.integrity.app.impl.verification.IssueDescriptionTemplates.*;
import static letunov.microservice.integrity.domain.issue.IssueLevel.ERROR;
import static letunov.microservice.integrity.domain.issue.IssueLevel.WARNING;

@Service
@RequiredArgsConstructor
public class MicroserviceVerificationServiceImpl implements MicroserviceVerificationService {
    private final MicroserviceRepository microserviceRepository;
    private final ContractRepository contractRepository;

    //где-нибудь нужна валидация, чтобы микросервисы не реализывали контракты, которые сами требуют
    @Override
    @Transactional
    public List<Issue> verify(List<Microservice> microservices) {
        Set<Issue> issues = new HashSet<>();

        microservices.forEach(microservice -> {
            issues.addAll(verifyConsumingContracts(microservice, microservices));
            issues.addAll(verifyProvidingContracts(microservice, microservices));
        });

        return new ArrayList<>(issues);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private List<Issue> verifyConsumingContracts(Microservice microservice, List<Microservice> additiveMicroservices) {
        Map<String, List<Contract>> requiredContractsWithRequiredServices = microservice.getConsumingContracts().stream()
            .collect(
                groupingBy(ConsumingRelationship::getServiceName,
                    mapping(ConsumingRelationship::getContract, toList())));
        Map<String, List<Contract>> providingContractsWithMicroservices = getProvidingContractsWithMicroservices(requiredContractsWithRequiredServices, additiveMicroservices, microservice);
        Map<String, Microservice> additiveMicroservicesMap = additiveMicroservices.stream()
            .collect(Collectors.toMap(Microservice::getName, identity()));

        return getConsumingIssues(microservice, requiredContractsWithRequiredServices, providingContractsWithMicroservices, additiveMicroservicesMap);
    }

    private List<Issue> verifyProvidingContracts(Microservice microservice, List<Microservice> additiveMicroservices) {
        Map<String, Contract> providingContractsMap = microservice.getProvidingContracts().stream().collect(toMap(Contract::getName, identity()));

        Map<String, List<Contract>> requiredContractsByMicroservices = new HashMap<>();
        Map<String, List<Contract>> requiredContractsByMicroservicesFromRepo = contractRepository.findRequiredContractsByMicroservices(microservice.getName()); //заранее вытащить все микросервисы
        Map<String, List<Contract>> requiredContractsByMicroservicesFromAdditive = additiveMicroservices.stream()
            .collect(Collectors.toMap(Microservice::getName, additiveMicroservice -> additiveMicroservice.getConsumingContracts().stream()
                .filter(consumingRelationship -> consumingRelationship.getServiceName().equals(microservice.getName()))
                .map(ConsumingRelationship::getContract)
                .toList()));
        requiredContractsByMicroservices.putAll(requiredContractsByMicroservicesFromRepo);
        requiredContractsByMicroservices.putAll(requiredContractsByMicroservicesFromAdditive);
        requiredContractsByMicroservices.put(microservice.getName(), new ArrayList<>());
        Map<String, Microservice> additiveMicroservicesMap = additiveMicroservices.stream()
            .collect(Collectors.toMap(Microservice::getName, identity()));

        return getProvidingIssues(requiredContractsByMicroservices, providingContractsMap, microservice, additiveMicroservicesMap);
    }

    private List<Issue> getConsumingIssues(Microservice microservice, Map<String, List<Contract>> requiredContractsByRequiredServices,
        Map<String, List<Contract>> providingContractsByMicroservices, Map<String, Microservice> additiveMicroservices) {
        List<Issue> issues = new ArrayList<>();
        requiredContractsByRequiredServices.forEach((requiredMicroserviceName, contracts) ->
            contracts.forEach(contract -> {
                var providedContract = ofNullable(providingContractsByMicroservices.get(requiredMicroserviceName))
                    .flatMap(providedContracts -> getContractByName(providedContracts, contract.getName()));

                if (providingContractsByMicroservices.containsKey(requiredMicroserviceName)) {
                    var requiredMicroservice = ofNullable(additiveMicroservices.get(requiredMicroserviceName))
                        .or(() -> microserviceRepository.findByName(requiredMicroserviceName))
                        .orElseThrow();
                    verify(providedContract.isEmpty(), requiredMicroservice, requiredMicroserviceName, providedContract.orElse(null), contract, microservice)
                        .ifPresent(issues::add);
                } else {
                    issues.add(createIssue(null, requiredMicroserviceName, of(contract), microservice, IssueType.MICROSERVICE_DOES_NOT_EXIST, ERROR));
                }
        }));
        return issues;
    }

    private List<Issue> getProvidingIssues(Map<String, List<Contract>> requiredContractsByMicroservices, Map<String, Contract> providingContractsMap,
        Microservice microservice, Map<String, Microservice> additiveMicroservices) {
        List<Issue> issues = new ArrayList<>();
        requiredContractsByMicroservices.forEach((consumerName, contracts) -> contracts.forEach(contract -> {
            var providedContract = providingContractsMap.get(contract.getName());
            if (requiredContractsByMicroservices.containsKey(consumerName)) {
                var consumer = ofNullable(additiveMicroservices.get(consumerName))
                    .or(() -> microserviceRepository.findByName(consumerName))
                    .orElseThrow();
                verify(!providingContractsMap.containsKey(contract.getName()), microservice, microservice.getName(), providedContract, contract, consumer)
                    .ifPresent(issues::add);
            }
        }));
        issues.addAll(getNotRequiredIssues(requiredContractsByMicroservices, providingContractsMap, microservice));
        return issues;
    }

    private Optional<Issue> verify(boolean isProvidingContract, Microservice requiredMicroservice, String requiredMicroserviceName, Contract providedContract,
        Contract targetContract, Microservice consumer) {
        if (isProvidingContract) {
            return Optional.of(createIssue(requiredMicroservice, requiredMicroserviceName, of(targetContract), consumer, IssueType.CONTRACT_IS_NOT_PROVIDING, ERROR));
        } else if (isDependencyDifferent(providedContract, targetContract)) {
            return Optional.of(createIssue(requiredMicroservice, requiredMicroserviceName, of(providedContract, targetContract), consumer, IssueType.CONTRACT_DIFFERENT_DEPENDENCY, ERROR));
        } else if (isVersionsDifferent(providedContract, targetContract)) {
            return Optional.of(createIssue(requiredMicroservice, requiredMicroserviceName, of(providedContract, targetContract), consumer, IssueType.CONTRACT_DIFFERENT_VERSION, ERROR));
        } else if (isChecksumDifferent(providedContract, targetContract)) {
            return Optional.of(createIssue(requiredMicroservice, requiredMicroserviceName, of(providedContract, targetContract), consumer, IssueType.CONTRACT_DIFFERENT_CHECKSUM, ERROR));
        }
        return empty();
    }

    private Issue createIssue(Microservice requiredMicroservice, String requiredMicroserviceName, List<Contract> contracts, Microservice causedAsConsumerBy,
        IssueType issueType, IssueLevel issueLevel) {
        return new Issue()
            .setDescription(getIssueDescription(issueType, requiredMicroserviceName, contracts))
            .setIssueType(issueType)
            .setCausedAsConsumerByAsString(causedAsConsumerBy != null ? causedAsConsumerBy.getName() : null)
            .setCausedAsProviderByAsString(requiredMicroserviceName)
            .setIssueLevel(issueLevel)
            .setAssociatedContracts(contracts)
            .setCausedAsConsumerBy(causedAsConsumerBy)
            .setCausedAsProviderBy(requiredMicroservice);
    }

    private Optional<Contract> getContractByName(List<Contract> contracts, String name) {
        return contracts.stream().filter(contract -> contract.getName().equals(name)).findFirst();
    }

    private boolean isDependencyDifferent(Contract first, Contract second) {
        return !first.getArtifactId().equals(second.getArtifactId()) || !first.getGroupId().equals(second.getGroupId());
    }

    private String getIssueDescription(IssueType issueType, String microserviceName, List<Contract> contracts) {
        return switch (issueType) {
            case MICROSERVICE_DOES_NOT_EXIST -> MICROSERVICE_DOES_NOT_EXIST.formatted(microserviceName);
            case CONTRACT_IS_NOT_PROVIDING -> CONTRACT_IS_NOT_PROVIDING.formatted(microserviceName, contracts.getFirst().getName());
            case CONTRACT_DIFFERENT_VERSION -> CONTRACT_DIFFERENT_VERSION;
            case CONTRACT_DIFFERENT_CHECKSUM -> CONTRACT_DIFFERENT_CHECKSUM;
            case CONTRACT_DIFFERENT_DEPENDENCY -> CONTRACT_DIFFERENT_DEPENDENCY;
            case NOT_REQUIRED_CONTRACT -> NOT_REQUIRED_CONTRACT.formatted(microserviceName, contracts.getFirst().getName());
        };
    }

    private Map<String, List<Contract>> getProvidingContractsWithMicroservices(Map<String, List<Contract>> requiredContractsWithRequiredServices,
        List<Microservice> additiveMicroservices, Microservice currentMicroservice) {
        Map<String, List<Contract>> result = new HashMap<>();

        Map<String, List<Contract>> providingContractsWithMicroservicesFromAdditive = additiveMicroservices.stream()
            .collect(Collectors.toMap(Microservice::getName, Microservice::getProvidingContracts));
        List<String> requiredMicroservices = requiredContractsWithRequiredServices.keySet().stream().toList();
        Map<String, List<Contract>> providingContractsWithMicroservicesFromRepo = contractRepository.findProvidedContractsByMicroservices(requiredMicroservices);
        result.putAll(providingContractsWithMicroservicesFromRepo);
        result.putAll(providingContractsWithMicroservicesFromAdditive);
        result.remove(currentMicroservice.getName());

        return result;
    }

    private List<Issue> getNotRequiredIssues(Map<String, List<Contract>> requiredContractsByMicroservices, Map<String, Contract> providingContractsWithNames, Microservice microservice) {
        Set<Contract> requiredContracts = requiredContractsByMicroservices.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(HashSet::new));
        Set<Contract> providingContracts = new HashSet<>(providingContractsWithNames.values());
        requiredContracts.forEach(requiredContract ->
            providingContracts.removeIf(contract -> contract.getName().equals(
                requiredContract.getName()) &&
                contract.getVersion().equals(requiredContract.getVersion()) &&
                contract.getChecksum().equals(requiredContract.getChecksum()) &&
                contract.getGroupId().equals(requiredContract.getGroupId()) &&
                contract.getArtifactId().equals(requiredContract.getArtifactId())));
        return providingContracts.stream()
            .map(contract -> createIssue(microservice, microservice.getName(), List.of(contract), null, IssueType.NOT_REQUIRED_CONTRACT, WARNING))
            .toList();
    }

    private boolean isVersionsDifferent(Contract first, Contract second) {
        return !first.getVersion().equals(second.getVersion());
    }

    private boolean isChecksumDifferent(Contract first, Contract second) {
        return !first.getChecksum().equals(second.getChecksum());
    }
}
