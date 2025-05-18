package letunov.microservice.integrity.app.api.graph.exception;

public class GraphNotFoundException extends RuntimeException {

    public GraphNotFoundException(String id) {
        super("Graph with '%s' not found".formatted(id));
    }
}
