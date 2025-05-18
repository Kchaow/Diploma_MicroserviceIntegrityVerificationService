package letunov.microservice.integrity.domain.graph.microservice;

public enum ChangeGraphStatus {
    WAIT_FOR_COMMIT,
    WAIT_FOR_VERIFY,
    DONE
}
