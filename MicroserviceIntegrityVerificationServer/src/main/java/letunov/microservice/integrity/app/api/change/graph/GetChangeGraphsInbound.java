package letunov.microservice.integrity.app.api.change.graph;

import java.util.List;

public interface GetChangeGraphsInbound {
    List<ChangeGraphsInfo> execute();
}
