package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.domain.microservice.Microservice;
import letunov.microservice.integrity.domain.microservice.UsesRelationship;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetUsesRelationshipsFromConsumingDelegate {
    public List<UsesRelationship> execute(Microservice consumingMicroservice, Microservice microserviceInUse) {
        return consumingMicroservice.getConsumingContracts().stream()
            .filter(consumingRelationship -> consumingRelationship.getServiceName().equals(microserviceInUse.getName()))
            .map(consumingRelationship -> new UsesRelationship()
                .setContractName(consumingRelationship.getContract().getName())
                .setMicroservice(microserviceInUse))
            .toList();
    }
}
