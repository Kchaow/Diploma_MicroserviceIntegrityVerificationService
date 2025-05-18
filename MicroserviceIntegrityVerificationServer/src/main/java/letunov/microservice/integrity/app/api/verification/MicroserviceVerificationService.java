package letunov.microservice.integrity.app.api.verification;

import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.microservice.Microservice;

import java.util.List;

public interface MicroserviceVerificationService {
    List<Issue> verify(List<Microservice> microservices);
}
