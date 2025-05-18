package letunov.microservice.integrity.domain.contract;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Contract")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Contract {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String artifactId;
    private String groupId;
    private String version;
    private String checksum;
}
