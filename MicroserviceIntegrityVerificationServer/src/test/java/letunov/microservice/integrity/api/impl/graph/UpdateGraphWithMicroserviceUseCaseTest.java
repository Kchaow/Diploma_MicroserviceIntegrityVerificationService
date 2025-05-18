package letunov.microservice.integrity.api.impl.graph;

import letunov.microservice.integrity.TestSupport;
import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import letunov.microservice.integrity.app.api.repo.ContractRepository;
import letunov.microservice.integrity.app.api.repo.IssueRepository;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.app.api.verification.MicroserviceVerificationService;
import letunov.microservice.integrity.app.impl.graph.UpdateGraphWithMicroserviceUseCase;
import letunov.microservice.integrity.domain.contract.Contract;
import letunov.microservice.integrity.domain.microservice.Microservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Set.of;
import static java.util.function.UnaryOperator.identity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateGraphWithMicroserviceUseCaseTest extends TestSupport {
    @Mock
    private MicroserviceVerificationService microserviceVerificationService;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private MicroserviceRepository microserviceRepository;
    @InjectMocks
    private UpdateGraphWithMicroserviceUseCase updateGraphWithMicroserviceUseCase;
    private MicroserviceInfo microserviceInfo;
    private Contract identicalContract;
    private ConsumingContractInfo identicalConsumingContractInfo;
    private ProvidingContractInfo identicalProvidingContractInfo;
    private Map<String, Microservice> microserviceMap;
    private Microservice microserviceToSave;

    @BeforeEach
    void setup() {
        microserviceInfo = readFromFile("microservice_info.json", MicroserviceInfo.class);
        identicalContract = readFromFile("identical_contract.json", Contract.class);
        identicalConsumingContractInfo = readFromFile("identical_consuming_contract_info.json", ConsumingContractInfo.class);
        identicalProvidingContractInfo = readFromFile("identical_providing_contract_info.json", ProvidingContractInfo.class);
        microserviceMap = getMircroserviceMap();
        microserviceToSave = readFromFile("microservice_to_save.json", Microservice.class);
    }

    @Test
    void execute_existingMicroservice() {
        when(microserviceRepository.findByName(microserviceInfo.getName())).thenReturn(Optional.of(new Microservice().setName("microName")));
        mock();

        updateGraphWithMicroserviceUseCase.execute(microserviceInfo);

        verify(microserviceRepository).save(microserviceToSave);
    }

    @Test
    void execute_notExistingMicroservice() {
        List<Microservice> microservicesThatRequires = readListFromFile("uses_microservices.json", Microservice.class);
        List<Microservice> microservicesToUpdate = readListFromFile("microservices_to_update.json", Microservice.class);
        when(microserviceRepository.findMicroservicesRequiredMicroservice(microserviceInfo.getName())).thenReturn(microservicesThatRequires);
        when(microserviceRepository.findByName(microserviceInfo.getName())).thenReturn(empty());
        mock();

        updateGraphWithMicroserviceUseCase.execute(microserviceInfo);

        verify(microserviceRepository).save(microserviceToSave);
        verify(microserviceRepository).saveAll(microservicesToUpdate);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Map<String, Microservice> getMircroserviceMap() {
        return ((List<Microservice>) readListFromFile("microservices.json", Microservice.class)).stream()
            .collect(Collectors.toMap(Microservice::getName, identity()));
    }

    private void mockIdenticalProvidingContract(Contract identicalContract, ProvidingContractInfo identicalProvidingContractInfo) {
        when(contractRepository.findIdentical(any(ProvidingContractInfo.class))).thenAnswer(invocationOnMock -> {
            if (invocationOnMock.getArgument(0).equals(identicalProvidingContractInfo)) {
                return Optional.of(identicalContract);
            } else {
                return empty();
            }
        });
    }

    private void mockIdenticalConsumingContract(Contract identicalContract, ConsumingContractInfo identicalConsumingContractInfo) {
        when(contractRepository.findIdentical(any(ConsumingContractInfo.class))).thenAnswer(invocationOnMock -> {
            if (invocationOnMock.getArgument(0).equals(identicalConsumingContractInfo)) {
                return Optional.of(identicalContract);
            } else {
                return empty();
            }
        });
    }

    private void mock() {
        mockIdenticalConsumingContract(identicalContract, identicalConsumingContractInfo);
        mockIdenticalProvidingContract(identicalContract, identicalProvidingContractInfo);
        when(microserviceRepository.findByNameInGroupByName(of("service1", "service2", "service3"))).thenReturn(microserviceMap);
        when(issueRepository.findByDescription(anyString())).thenReturn(List.of());
        when(issueRepository.findAllCausedBy(any())).thenReturn(List.of());
        when(microserviceRepository.save(microserviceToSave)).thenReturn(microserviceToSave);
    }
}
