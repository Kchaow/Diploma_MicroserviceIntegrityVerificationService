package letunov.microservice.integrity.adapter.rest;

import io.swagger.v3.oas.annotations.Operation;
import letunov.microservice.integrity.adapter.rest.dto.*;
import letunov.microservice.integrity.adapter.rest.mapper.GraphMapper;
import letunov.microservice.integrity.app.api.change.graph.*;
import letunov.microservice.integrity.app.api.graph.GetChangeGraphByIdInbound;
import letunov.microservice.integrity.app.api.graph.GetGraphInbound;
import letunov.microservice.integrity.app.api.graph.GetMicroserviceListInbound;
import letunov.microservice.integrity.app.api.graph.UpdateGraphWithMicroserviceInbound;
import letunov.microservice.integrity.app.api.verification.VerifyGraphInbound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class MicroserviceIntegrityController {
    private final UpdateGraphWithMicroserviceInbound updateGraphWithMicroserviceInbound;
    private final GetGraphInbound getGraphInbound;
    private final GraphMapper graphMapper;
    private final GetChangeGraphByIdInbound getChangeGraphByIdInbound;
    private final VerifyGraphInbound verifyGraphInbound;
    private final CreateChangeGraphInbound createChangeGraphInbound;
    private final CommitMicroserviceInbound commitMicroserviceInbound;
    private final VerifyChangeGraphInbound verifyChangeGraphInbound;
    private final GetChangeGraphsInbound getChangeGraphsInbound;
    private final GetMicroserviceListInbound getMicroserviceListInbound;
    private final GetChangeGraphByMicroserviceNameInbound getChangeGraphByMicroserviceNameInbound;

    @Operation(summary = "Изменение графа")
    @PostMapping("/graph/microservice")
    public void updateGraph(@RequestBody MicroserviceDto microserviceDto) {
        log.info("[updateGraph] request; microserviceDto: {}", microserviceDto);
        var microserviceInfo = graphMapper.toMicroserviceInfo(microserviceDto);
        updateGraphWithMicroserviceInbound.execute(microserviceInfo);
    }

    @Operation(summary = "Получение всего графа")
    @GetMapping("/graph")
    public ResponseEntity<GraphDto> getGraph() {
        log.info("[getGraph] request;");
        var graph = getGraphInbound.execute();
        return ResponseEntity.ok(graphMapper.toGraphDto(graph));
    }

    @Operation(summary = "Обновление графа")
    @PostMapping("/graph/update")
    public void updateGraph() {
        log.info("[updateGraph] request;");
        verifyGraphInbound.execute();
    }

    @Operation(summary = "Создать граф изменений")
    @PostMapping("/change-graph")
    public ResponseEntity<CreatedChangeGraphInfoDto> createChangeGraph(@RequestBody CreateChangeGraphDto createChangeGraphDto) {
        log.info("[createChangeGraph] request; createChangeGraphDto: {}", createChangeGraphDto);
        var result = createChangeGraphInbound.execute(createChangeGraphDto.associatedMicroservices());
        return ResponseEntity.ok(graphMapper.toCreatedChangeGraphInfoDto(result));
    }

    @Operation(summary = "Добавить микросервис в граф изменений")
    @PutMapping("/change-graph/{id}")
    public void commitMicroservice(@RequestBody MicroserviceDto microserviceDto, @PathVariable String id) {
        log.info("[commitMicroservice] request; microserviceDto: {}; id: {}", microserviceDto, id);
        commitMicroserviceInbound.execute(graphMapper.toMicroserviceInfo(microserviceDto), id);
    }

    @Operation(summary = "Верифицировать граф изменений")
    @PutMapping("/change-graph/{id}/verify")
    public ResponseEntity<VerificationResultDto> verifyChangeGraph(@PathVariable String id) {
        log.info("[verifyChangeGraph] request; id: {}", id);
        var result = verifyChangeGraphInbound.execute(id);
        return ResponseEntity.ok(graphMapper.toVerificationResultDto(result));
    }

    @Operation(summary = "Получить графы изменений")
    @GetMapping("/change-graph")
    public List<ChangeGraphsInfoDto> getChangeGraphs() {
        log.info("[getChangeGraphs] request;");
        var result = getChangeGraphsInbound.execute();
        return graphMapper.toChangeGraphsInfoDtoList(result);
    }

    @Operation(summary = "Получение графа изменений по id")
    @GetMapping("/change-graph/{id}")
    public ResponseEntity<GraphDto> getGraphById(@PathVariable String id) {
        log.info("[getGraphById] request; id: {}", id);
        var result = getChangeGraphByIdInbound.execute(id);
        return ResponseEntity.ok(graphMapper.toGraphDto(result));
    }

    @Operation(summary = "Справочник существующих микросервисов")
    @GetMapping("/microservice")
    public ResponseEntity<List<MicroserviceDictDto>> getMicroserviceList() {
        log.info("[getMicroserviceList] request;");
        var result = getMicroserviceListInbound.execute();
        return ResponseEntity.ok(graphMapper.toMicroserviceDictDtoList(result));
    }

    @Operation(summary = "Получить активные процессы верификации")
    @GetMapping("/change-graph/process/microservice/{microserviceName}")
    public ResponseEntity<List<String>> getVerificationProcesses(@PathVariable String microserviceName) {
        log.info("[getMicroserviceList] request; microserviceName: {}", microserviceName);
        var result = getChangeGraphByMicroserviceNameInbound.execute(microserviceName);
        return ResponseEntity.ok(result);
    }
}
