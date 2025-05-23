package letunov.microservice.integrity.domain.graph;

import letunov.microservice.integrity.domain.graph.microservice.ChangeGraphStatus;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ChangeGraph {
    @MongoId
    private String id;
    private List<String> associatedMicroservices = new ArrayList<>();
    private List<MicroserviceInfo> commitedMicroservices = new ArrayList<>();
    private Graph graph;
    private ChangeGraphStatus changeGraphStatus;
    private LocalDateTime dateTime;
    private GraphElementStatus verificationStatus;
}
