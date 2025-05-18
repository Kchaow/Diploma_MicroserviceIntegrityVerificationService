package letunov.microservice.integrity.domain.graph.microservice;

import lombok.Data;

@Data
public class DependencyInfo {
    private String groupId;
    private String artifactId;
    private String version;
}
