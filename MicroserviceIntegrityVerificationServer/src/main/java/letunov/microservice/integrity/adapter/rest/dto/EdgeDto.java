package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

@Data
public class EdgeDto {
    private String id;
    private String message;
    private String source;
    private String target;
    private String status;
}
