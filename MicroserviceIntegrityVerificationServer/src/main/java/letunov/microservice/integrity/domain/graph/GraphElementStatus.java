package letunov.microservice.integrity.domain.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GraphElementStatus {
    ERROR(3),
    WARNING(2),
    OK(1);

    private final int level;
}
