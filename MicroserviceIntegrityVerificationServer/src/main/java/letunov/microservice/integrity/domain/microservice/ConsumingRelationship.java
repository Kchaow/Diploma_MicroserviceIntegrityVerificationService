package letunov.microservice.integrity.domain.microservice;

import letunov.microservice.integrity.domain.contract.Contract;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class ConsumingRelationship {
    @RelationshipId
    private Long id;
    private String serviceName;

    @TargetNode
    private Contract contract;
}
