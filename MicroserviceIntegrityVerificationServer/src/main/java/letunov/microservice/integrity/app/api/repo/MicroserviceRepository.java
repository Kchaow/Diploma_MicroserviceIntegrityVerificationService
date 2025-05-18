package letunov.microservice.integrity.app.api.repo;

import letunov.microservice.integrity.domain.microservice.Microservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MicroserviceRepository {
    Optional<Microservice> findByName(String name);

    Map<String, Microservice> findByNameInGroupByName(Set<String> microservicesNames);

    List<Microservice> findMicroservicesRequiredMicroservice(String microserviceName);

    List<Microservice> findMicroservicesRequiredMicroservices(List<String> microservicesNames);

    void saveAll(List<Microservice> microservices);

    Microservice save(Microservice microservice);

    boolean existByName(String microserviceName);

    void clearConsumingRelationships(String microserviceName);

    void clearUsesRelationships(String microserviceName);

    void clearProvidingRelationships(String microserviceName);

    List<Microservice> findAll();
}
