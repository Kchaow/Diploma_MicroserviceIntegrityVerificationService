package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

@Data
public class ConsumingContractDto {
    private String name;
    private String serviceName;
    private DependencyDto dependency;
    private String checksum;
}
