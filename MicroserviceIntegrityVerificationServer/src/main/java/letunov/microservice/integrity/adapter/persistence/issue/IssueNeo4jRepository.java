package letunov.microservice.integrity.adapter.persistence.issue;

import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.issue.IssueType;
import letunov.microservice.integrity.domain.microservice.Microservice;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface IssueNeo4jRepository extends Neo4jRepository<Issue, Long> {
    List<Issue> findAllByDescription(String description);

    @Query("""
        MATCH (m:Microservice {name: $microserviceName})<-[:CAUSED_AS_PROVIDER_BY]-(i:Issue)
        RETURN i
        UNION
        MATCH (m:Microservice {name: $microserviceName})<-[:CAUSED_AS_CONSUMER_BY]-(i:Issue)
        RETURN i
        """)
    List<Issue> findAllCausedBy(String microserviceName);

    List<Issue> findByIssueTypeAndCausedAsProviderByAsString(IssueType type, String providerName);
}
