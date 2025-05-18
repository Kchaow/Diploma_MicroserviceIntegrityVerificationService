package letunov.microservice.integrity.app.api.change.graph;

import java.util.List;

public interface CreateChangeGraphInbound {
    ChangeGraphInfo execute(List<String> associatedMicroservices);
}
