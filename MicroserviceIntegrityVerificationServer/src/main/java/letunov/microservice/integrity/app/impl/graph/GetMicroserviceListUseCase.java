package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.app.api.graph.GetMicroserviceListInbound;
import letunov.microservice.integrity.app.api.graph.MicroserviceAttributes;
import letunov.microservice.integrity.app.api.repo.MicroserviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetMicroserviceListUseCase implements GetMicroserviceListInbound {
    private final MicroserviceRepository microserviceRepository;

    @Override
    public List<MicroserviceAttributes> execute() {
        return microserviceRepository.findAll().stream()
            .map(microservice -> new MicroserviceAttributes(microservice.getName()))
            .toList();
    }
}
