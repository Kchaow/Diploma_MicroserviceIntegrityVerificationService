package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class NodeDto {
    private String id;
    private String status;
    private List<String> messages;
}
