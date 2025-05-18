package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

@Data
public class DependencyDto {
    private String groupId;
    private String artifactId;
    private String version;
}
