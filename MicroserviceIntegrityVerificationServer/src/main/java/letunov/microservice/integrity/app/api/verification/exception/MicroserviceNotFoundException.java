package letunov.microservice.integrity.app.api.verification.exception;

public class MicroserviceNotFoundException extends RuntimeException {
    public MicroserviceNotFoundException(String name) {
        super("Microservice with name %s not found".formatted(name));
    }
}
