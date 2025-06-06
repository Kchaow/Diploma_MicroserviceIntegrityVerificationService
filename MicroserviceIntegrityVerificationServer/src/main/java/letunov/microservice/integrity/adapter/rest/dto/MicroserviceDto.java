package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class MicroserviceDto {
    private String microserviceName;
    private List<ProvidingContractDto> providing;
    private List<ConsumingContractDto> consuming;
}
