package letunov.microservice.integrity.adapter.persistence.contract;

import letunov.microservice.integrity.domain.contract.Contract;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContractNeo4jRepository extends Neo4jRepository<Contract, Long> {
    @Query("""
        MATCH (m:Microservice)-[:PROVIDES]->(c:Contract)
        WHERE m.name IN $microservices
        RETURN m.name AS microserviceName, COLLECT(c) AS contracts
        """)
    List<MicroserviceContracts> findProvidedContractsByMicroservices(List<String> microservices);

//    MATCH (m:Microservice)-[:USES]->(target:Microservice {name: $microserviceName})
    @Query("""
        MATCH (m)-[c:CONSUMES]->(contract:Contract)
        WHERE c.serviceName = $microserviceName
        RETURN m.name as microserviceName, COLLECT(contract) AS contracts
        """)
    List<MicroserviceContracts> findRequiredContractsByMicroservices(String microserviceName);

    @Query("""
        MATCH (n:Contract)
        WHERE NOT (n)--()
        DELETE (n)
        """)
    void deleteAllWithoutRelationships();

    Optional<Contract> findByNameAndArtifactIdAndGroupIdAndVersionAndChecksum(String name, String artifactId, String groupId, String version, String checksum);
}
