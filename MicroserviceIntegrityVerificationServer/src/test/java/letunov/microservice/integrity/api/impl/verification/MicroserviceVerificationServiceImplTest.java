//package letunov.microservice.integrity.api.impl.verification;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import letunov.microservice.integrity.TestSupport;
//import letunov.microservice.integrity.app.api.repo.ContractRepository;
//import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
//import letunov.microservice.integrity.app.impl.verification.MicroserviceVerificationServiceImpl;
//import letunov.microservice.integrity.domain.contract.Contract;
//import letunov.microservice.integrity.domain.issue.Issue;
//import letunov.microservice.integrity.domain.microservice.Microservice;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//class MicroserviceVerificationServiceImplTest extends TestSupport {
//    @Mock
//    private MicroserviceRepository microserviceRepository;
//    @Mock
//    private ContractRepository contractRepository;
//    @InjectMocks
//    private MicroserviceVerificationServiceImpl microserviceVerificationService;
//
//    @Test
//    void verify() throws Exception {
//        Microservice microservice = readFromFile("microservice.json", Microservice.class);
//        mockContractRepository();
//        mockMicroserviceRepository();
//
//        var result = microserviceVerificationService.verify(microservice);
//
//        List<Issue> expected = readListFromFile("issues.json", Issue.class);
//        assertEquals(expected.size(), result.size());
//        assertTrue(expected.containsAll(result));
//    }
//
//    // ===================================================================================================================
//    // = Implementation
//    // ===================================================================================================================
//
//    private void mockContractRepository() throws Exception {
//        Map<String, List<Contract>> contractsByMicroservices =
//            objectMapper.readValue(readFile("contracts_by_microservices.json"), new TypeReference<>() {});
//        List<String> requiredMicroservices = readListFromFile("required_microservices.json", String.class);
//        when(contractRepository.findProvidedContractsByMicroservices(requiredMicroservices)).thenReturn(contractsByMicroservices);
//
//        Map<String, List<Contract>> requiredContractsByMicroservices =
//            objectMapper.readValue(readFile("required_contracts_by_microservices.json"), new TypeReference<>() {});
//        when(contractRepository.findRequiredContractsByMicroservices("testMicro")).thenReturn(requiredContractsByMicroservices);
//    }
//
//    private void mockMicroserviceRepository() {
//        when(microserviceRepository.findByName(anyString())).thenAnswer(
//            invocationOnMock -> Optional.of(new Microservice().setName(invocationOnMock.getArgument(0))));
//    }
//}
