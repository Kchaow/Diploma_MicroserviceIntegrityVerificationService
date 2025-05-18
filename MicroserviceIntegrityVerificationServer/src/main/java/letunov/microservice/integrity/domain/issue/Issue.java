package letunov.microservice.integrity.domain.issue;

import letunov.microservice.integrity.domain.contract.Contract;
import letunov.microservice.integrity.domain.microservice.Microservice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import java.util.ArrayList;
import java.util.List;

@Node("Issue")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Issue {
    @Id @GeneratedValue
    private Long id;
    private String description;
    private IssueLevel issueLevel;
    private IssueType issueType;
    private String causedAsProviderByAsString;
    private String causedAsConsumerByAsString;

    @Relationship(type = "CAUSED_AS_PROVIDER_BY", direction = Direction.OUTGOING)
    private Microservice causedAsProviderBy;

    @Relationship(type = "CAUSED_AS_CONSUMER_BY", direction = Direction.OUTGOING)
    private Microservice causedAsConsumerBy;

    @Relationship(type = "ASSOCIATED_WITH", direction = Direction.OUTGOING)
    private List<Contract> associatedContracts = new ArrayList<>();
}
