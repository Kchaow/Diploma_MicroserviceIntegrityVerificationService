package letunov.microservice.integrity.adapter.persistence.graph;

import letunov.microservice.integrity.domain.graph.ChangeGraph;
import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChangeGraphMongodbRepository extends MongoRepository<ChangeGraph, String> {
    List<ChangeGraph> findByAssociatedMicroservicesContainsAndChangeGraphStatusIs(String associatedMicroservice, ChangeGraphStatus changeGraphStatus);
}
