package letunov.microservice.integrity.adapter.persistence.contract;

import letunov.microservice.integrity.domain.contract.Contract;
import lombok.Data;

import java.util.List;

@Data
public class MicroserviceContracts {
    private String microserviceName;
    private List<Contract> contracts;
}
