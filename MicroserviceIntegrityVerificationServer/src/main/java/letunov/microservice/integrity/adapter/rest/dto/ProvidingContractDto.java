package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

@Data
public class ProvidingContractDto {
    private String name;
    private DependencyDto dependency;
    private String checksum;
}
