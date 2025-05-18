package letunov.microservice.integrity.app.api.verification;

import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;

public interface VerifyMicroserviceInbound {
    VerificationInfo execute(MicroserviceInfo microserviceInfo);
}
