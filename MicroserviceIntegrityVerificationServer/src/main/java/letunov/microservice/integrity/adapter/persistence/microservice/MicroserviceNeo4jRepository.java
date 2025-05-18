package letunov.microservice.integrity.adapter.persistence.microservice;

import letunov.microservice.integrity.domain.microservice.Microservice;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MicroserviceNeo4jRepository extends Neo4jRepository<Microservice, String> {
    Optional<Microservice> findByName(String name);

    List<Microservice> findMicroserviceByNameIn(Set<String> microservicesNames);

    @Query("""
        MATCH (m:Microservice)-[:CONSUMES {serviceName: $microserviceName}]->(:Contract)
        RETURN m.name
        """)
    List<String> findMicroservicesRequiredMicroservice(String microserviceName);

    @Query("""
        MATCH (m:Microservice)-[c:CONSUMES]->(:Contract)
        WHERE c.serviceName IN $microservicesNames
        RETURN m.name
        """)
    List<String> findMicroservicesRequiredMicroservices(List<String> microservicesNames);

    @Query("""
        MATCH (:Microservice {name: $microserviceName})-[c:CONSUMES]->(:Contract)
        DELETE c
        """)
    void clearConsumingRelationships(String microserviceName);

    @Query("""
        MATCH (:Microservice {name: $microserviceName})-[u:USES]->(:Microservice)
        DELETE u
        """)
    void clearUsesRelationships(String microserviceName);

    @Query("""
        MATCH (:Microservice {name: $microserviceName})-[p:PROVIDES]->(:Contract)
        DELETE p
        """)
    void clearProvidingRelationships(String microserviceName);

    List<Microservice> findAllByNameIn(List<String> names);

    boolean existsByName(String microserviceName);
}
