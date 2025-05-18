package letunov.microservice.integrity.adapter.rest.mapper;

import letunov.microservice.integrity.adapter.rest.dto.*;
import letunov.microservice.integrity.app.api.change.graph.ChangeGraphInfo;
import letunov.microservice.integrity.app.api.change.graph.ChangeGraphsInfo;
import letunov.microservice.integrity.app.api.change.graph.VerificationResult;
import letunov.microservice.integrity.app.api.graph.MicroserviceAttributes;
import letunov.microservice.integrity.app.api.verification.VerificationInfo;
import letunov.microservice.integrity.domain.contract.Edge;
import letunov.microservice.integrity.domain.contract.Node;
import letunov.microservice.integrity.domain.graph.*;
import letunov.microservice.integrity.domain.graph.microservice.ConsumingContractInfo;
import letunov.microservice.integrity.domain.graph.microservice.DependencyInfo;
import letunov.microservice.integrity.domain.graph.microservice.MicroserviceInfo;
import letunov.microservice.integrity.domain.graph.microservice.ProvidingContractInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GraphMapper {
    GraphDto toGraphDto(Graph graph);

    NodeDto toNodeDto(Node node);

    EdgeDto toEdgeDto(Edge edge);

    @Mapping(target = "name", source = "microserviceName")
    @Mapping(target = "providingContractInfoList", source = "providing")
    @Mapping(target = "consumingContractInfoList", source = "consuming")
    MicroserviceInfo toMicroserviceInfo(MicroserviceDto microserviceDto);

    @Mapping(target = "dependencyInfo", source = "dependency")
    ProvidingContractInfo toProvidingContractInfo(ProvidingContractDto providingContractDto);

    @Mapping(target = "dependencyInfo", source = "dependency")
    ConsumingContractInfo toConsumingContractInfo(ConsumingContractDto consumingContractDto);

    DependencyInfo toDependencyInfo(DependencyDto dependencyDto);

    VerificationDto toVerificationDto(VerificationInfo verificationInfo);

    CreatedChangeGraphInfoDto toCreatedChangeGraphInfoDto(ChangeGraphInfo changeGraphInfo);

    ChangeGraphsInfoDto toChangeGraphsInfoDto(ChangeGraphsInfo changeGraphsInfo);

    List<ChangeGraphsInfoDto> toChangeGraphsInfoDtoList(List<ChangeGraphsInfo> changeGraphsInfoList);

    VerificationResultDto toVerificationResultDto(VerificationResult verificationResult);

    MicroserviceDictDto toMicroserviceDictDto(MicroserviceAttributes microserviceAttributes);

    List<MicroserviceDictDto> toMicroserviceDictDtoList(List<MicroserviceAttributes> microserviceAttributesList);
}
