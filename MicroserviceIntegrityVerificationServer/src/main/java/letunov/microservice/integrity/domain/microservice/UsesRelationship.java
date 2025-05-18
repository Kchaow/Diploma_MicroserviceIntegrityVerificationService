package letunov.microservice.integrity.domain.microservice;

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
public class UsesRelationship {
    @RelationshipId
    private String id;
    private String contractName;

    @TargetNode
    private Microservice microservice;
}
