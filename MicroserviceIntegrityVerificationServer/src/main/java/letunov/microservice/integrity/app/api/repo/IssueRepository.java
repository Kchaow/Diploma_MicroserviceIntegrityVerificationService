package letunov.microservice.integrity.app.api.repo;

import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.issue.IssueType;
import letunov.microservice.integrity.domain.microservice.Microservice;

import java.util.List;

public interface IssueRepository {
    List<Issue> findByDescription(String description);

    List<Issue> findByIssueTypeAndCausedAsProviderByAsString(IssueType type, String providerName);

    List<Issue> findAllCausedBy(String microserviceName);

    List<Issue> findAll();

    void saveAll(List<Issue> issues);

    void deleteAll(List<Issue> issues);

    void deleteAll();
}
