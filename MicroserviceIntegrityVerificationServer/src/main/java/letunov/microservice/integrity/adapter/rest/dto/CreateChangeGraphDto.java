package letunov.microservice.integrity.adapter.rest.dto;

import java.util.List;

public record CreateChangeGraphDto(List<String> associatedMicroservices) {
}
