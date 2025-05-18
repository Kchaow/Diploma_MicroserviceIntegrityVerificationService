package letunov.microservice.integrity.domain.contract;

import letunov.microservice.integrity.domain.graph.GraphElementStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Edge {
    private String id;
    private String source;
    private String target;
    private String message;
    private String contractName;
    private GraphElementStatus status;
}
