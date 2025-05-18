package letunov.microservice.integrity.domain.contract;

import letunov.microservice.integrity.domain.graph.GraphElementStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Node {
    private String id;
    private GraphElementStatus status;
    private List<String> messages = new ArrayList<>();
}
