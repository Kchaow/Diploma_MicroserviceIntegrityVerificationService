package letunov.microservice.integrity.app.api.graph;

import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import lombok.Data;

import java.util.List;

@Data
public class MicroserviceContractsInfo {
    private String name;
    private List<ProvidingContractInfo> providingContractInfoList;
    private List<ConsumingContractInfo> consumingContractInfoList;
}
