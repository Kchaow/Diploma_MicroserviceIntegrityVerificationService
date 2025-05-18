package letunov.microservice.integrity.domain.graph.microservice;

import lombok.Data;

@Data
public class ProvidingContractInfo {
    private String name;
    private String checksum;
    private DependencyInfo dependencyInfo;
}
