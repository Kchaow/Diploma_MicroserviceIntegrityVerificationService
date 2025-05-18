package letunov.microservice.integrity.domain.microservice;

import letunov.microservice.integrity.domain.contract.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import java.util.ArrayList;
import java.util.List;

@Node("Microservice")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Microservice {
    @Id
    private String name;

    @Relationship(type = "USES", direction = Direction.OUTGOING)
    private List<UsesRelationship> usesMicroservices = new ArrayList<>();

    @Relationship(type = "CONSUMES", direction = Direction.OUTGOING)
    private List<ConsumingRelationship> consumingContracts = new ArrayList<>();

    @Relationship(type = "PROVIDES", direction = Direction.OUTGOING)
    private List<Contract> providingContracts = new ArrayList<>();
}
