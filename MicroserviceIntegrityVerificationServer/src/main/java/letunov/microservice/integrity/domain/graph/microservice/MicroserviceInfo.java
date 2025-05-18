package letunov.microservice.integrity.domain.graph.microservice;

import lombok.Data;

import java.util.List;

@Data
public class MicroserviceInfo {
    private String name;
    private List<ProvidingContractInfo> providingContractInfoList;
    private List<ConsumingContractInfo> consumingContractInfoList;
}
