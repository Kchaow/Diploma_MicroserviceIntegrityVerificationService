package letunov.microservice.integrity.domain.graph.microservice;

import lombok.Data;

@Data
public class ConsumingContractInfo {
    private String name;
    private String serviceName;
    private String checksum;
    private DependencyInfo dependencyInfo;
}
