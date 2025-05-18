package letunov.microservice.integrity.domain.graph;

import letunov.microservice.integrity.domain.contract.Edge;
import letunov.microservice.integrity.domain.contract.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private List<String> messages = new ArrayList<>();
}
