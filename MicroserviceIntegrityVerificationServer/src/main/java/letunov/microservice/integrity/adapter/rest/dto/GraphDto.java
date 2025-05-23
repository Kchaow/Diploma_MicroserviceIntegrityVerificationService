package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class GraphDto {
    private List<NodeDto> nodes;
    private List<EdgeDto> edges;
    private List<String> messages;
    private String status;
    private String verificationStatus;
}
