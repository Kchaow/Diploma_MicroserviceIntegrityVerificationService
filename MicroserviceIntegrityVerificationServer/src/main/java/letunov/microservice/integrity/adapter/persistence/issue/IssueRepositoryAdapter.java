package letunov.microservice.integrity.adapter.persistence.issue;

import letunov.microservice.integrity.app.api.repo.IssueRepository;
import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.issue.IssueType;
import letunov.microservice.integrity.domain.microservice.Microservice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class IssueRepositoryAdapter implements IssueRepository {
    private final IssueNeo4jRepository issueNeo4jRepository;

    @Override
    public List<Issue> findByDescription(String description) {
        return issueNeo4jRepository.findAllByDescription(description);
    }

    @Override
    public List<Issue> findByIssueTypeAndCausedAsProviderByAsString(IssueType type, String providerName) {
        return issueNeo4jRepository.findByIssueTypeAndCausedAsProviderByAsString(type, providerName);
    }

    @Override
    public List<Issue> findAllCausedBy(String microserviceName) {
        return issueNeo4jRepository.findAllCausedBy(microserviceName);
    }

    @Override
    public List<Issue> findAll() {
        return issueNeo4jRepository.findAll();
    }

    @Override
    public void saveAll(List<Issue> issues) {
        issueNeo4jRepository.saveAll(issues);
    }

    @Override
    public void deleteAll(List<Issue> issues) {
        issueNeo4jRepository.deleteAll(issues);
    }

    @Override
    public void deleteAll() {
        issueNeo4jRepository.deleteAll();
    }
}
