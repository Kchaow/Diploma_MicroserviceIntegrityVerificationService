package letunov.microservice.integrity.adapter.persistence.microservice;

import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import letunov.microservice.integrity.domain.microservice.Microservice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class MicroserviceRepositoryAdapter implements MicroserviceRepository {
    private final MicroserviceNeo4jRepository microserviceNeo4jRepository;

    @Override
    public Optional<Microservice> findByName(String name) {
        return microserviceNeo4jRepository.findByName(name);
    }

    @Override
    public Map<String, Microservice> findByNameInGroupByName(Set<String> microservicesNames) {
        return microserviceNeo4jRepository.findMicroserviceByNameIn(microservicesNames).stream()
            .collect(toMap(Microservice::getName, identity()));
    }

    @Override
    public List<Microservice> findMicroservicesRequiredMicroservice(String microserviceName) {
        var names = microserviceNeo4jRepository.findMicroservicesRequiredMicroservice(microserviceName);
        return microserviceNeo4jRepository.findAllByNameIn(names);
    }

    @Override
    public List<Microservice> findMicroservicesRequiredMicroservices(List<String> microservicesNames) {
        var names = microserviceNeo4jRepository.findMicroservicesRequiredMicroservices(microservicesNames);
        return microserviceNeo4jRepository.findAllByNameIn(names);
    }

    @Override
    public void saveAll(List<Microservice> microservices) {
        microserviceNeo4jRepository.saveAll(microservices);
    }

    @Override
    public Microservice save(Microservice microservice) {
        return microserviceNeo4jRepository.save(microservice);
    }

    @Override
    public boolean existByName(String microserviceName) {
        return microserviceNeo4jRepository.existsByName(microserviceName);
    }

    @Override
    public void clearConsumingRelationships(String microserviceName) {
        microserviceNeo4jRepository.clearConsumingRelationships(microserviceName);
    }

    @Override
    public void clearUsesRelationships(String microserviceName) {
        microserviceNeo4jRepository.clearUsesRelationships(microserviceName);
    }

    @Override
    public void clearProvidingRelationships(String microserviceName) {
        microserviceNeo4jRepository.clearProvidingRelationships(microserviceName);
    }

    @Override
    public List<Microservice> findAll() {
        return microserviceNeo4jRepository.findAll();
    }
}
