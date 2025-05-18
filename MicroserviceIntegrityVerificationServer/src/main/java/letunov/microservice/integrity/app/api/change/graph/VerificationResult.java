package letunov.microservice.integrity.app.api.change.graph;

import letunov.microservice.integrity.domain.graph.GraphElementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResult {
    private GraphElementStatus graphStatus;
}
