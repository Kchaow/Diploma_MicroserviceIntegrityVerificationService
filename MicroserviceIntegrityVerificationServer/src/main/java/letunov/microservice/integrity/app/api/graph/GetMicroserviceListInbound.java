package letunov.microservice.integrity.app.api.graph;

import java.util.List;

public interface GetMicroserviceListInbound {
    List<MicroserviceAttributes> execute();
}
