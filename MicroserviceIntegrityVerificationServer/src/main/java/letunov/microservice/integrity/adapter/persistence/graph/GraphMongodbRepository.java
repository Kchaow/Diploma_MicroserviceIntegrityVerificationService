package letunov.microservice.integrity.adapter.persistence.graph;

import letunov.microservice.integrity.domain.graph.Graph;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GraphMongodbRepository extends MongoRepository<Graph, String> {
}
