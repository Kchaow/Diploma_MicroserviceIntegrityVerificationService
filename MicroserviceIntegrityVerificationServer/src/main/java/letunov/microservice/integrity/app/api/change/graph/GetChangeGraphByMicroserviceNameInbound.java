package letunov.microservice.integrity.app.api.change.graph;

import java.util.List;

public interface GetChangeGraphByMicroserviceNameInbound {
    List<String> execute(String name);
}
