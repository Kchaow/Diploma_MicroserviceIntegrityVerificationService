package letunov.microservice.integrity.adapter.rest;

import letunov.microservice.integrity.app.api.graph.exception.GraphNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler({GraphNotFoundException.class})
    public ResponseEntity<String> handleNotFound(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
