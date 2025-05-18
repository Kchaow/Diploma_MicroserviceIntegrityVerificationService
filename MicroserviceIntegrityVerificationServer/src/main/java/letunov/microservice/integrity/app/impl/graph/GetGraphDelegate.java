package letunov.microservice.integrity.app.impl.graph;

import letunov.microservice.integrity.domain.contract.Edge;
import letunov.microservice.integrity.domain.contract.Node;
import letunov.microservice.integrity.domain.graph.Graph;
import letunov.microservice.integrity.domain.issue.Issue;
import letunov.microservice.integrity.domain.microservice.Microservice;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static letunov.microservice.integrity.domain.graph.GraphElementStatus.*;
import static letunov.microservice.integrity.domain.issue.IssueType.MICROSERVICE_DOES_NOT_EXIST;
import static letunov.microservice.integrity.domain.issue.IssueType.NOT_REQUIRED_CONTRACT;

@Component
public class GetGraphDelegate {
    private static final String TWO_SIDE_ISSUE_MESSAGE_TEMPLATE = "%s [%s]->[%s]: %s";
    private static final String ONE_SIDE_ISSUE_MESSAGE_TEMPLATE = "%s [%s]: %s";

    public Graph execute(List<Microservice> microservices, List<Issue> issues) {
        var messages = getMessages(issues);

        List<Node> nodes = getNodes(microservices);
        fillNodesWithIssues(nodes, issues);

        List<Edge> edges = getEdges(microservices);
        fillEdgesWithIssues(edges, issues);

        return new Graph()
            .setMessages(messages)
            .setNodes(nodes)
            .setEdges(edges);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void fillNodesWithIssues(List<Node> nodes, List<Issue> issues) {
        Map<String, Node> nodeMap = nodes.stream()
            .collect(Collectors.toMap(Node::getId, identity()));
        issues.forEach(issue -> {
            if (issue.getIssueType() == NOT_REQUIRED_CONTRACT) {
                var node = nodeMap.get(issue.getCausedAsProviderByAsString());
                if (node.getStatus() != ERROR) {
                    node.setStatus(WARNING)
                        .getMessages().add(getMessage(issue));
                }
                return;
            }
            if (nodeMap.containsKey(issue.getCausedAsConsumerByAsString())) {
                var node = nodeMap.get(issue.getCausedAsConsumerByAsString());
                node.setStatus(ERROR);
                node.getMessages().add(getMessage(issue));
            }
            if (nodeMap.containsKey(issue.getCausedAsProviderByAsString())) {
                var node = nodeMap.get(issue.getCausedAsProviderByAsString());
                node.setStatus(ERROR);
                node.getMessages().add(getMessage(issue));
            }
        });
    }

    private String getMessage(Issue issue) {
        if (issue.getIssueType() == NOT_REQUIRED_CONTRACT) {
            return ONE_SIDE_ISSUE_MESSAGE_TEMPLATE.formatted(issue.getIssueLevel(), issue.getCausedAsProviderByAsString(), issue.getDescription());
        }
        return TWO_SIDE_ISSUE_MESSAGE_TEMPLATE.formatted(issue.getIssueLevel(), issue.getCausedAsConsumerByAsString(), issue.getCausedAsProviderByAsString(), issue.getDescription());
    }

    private List<String> getMessages(List<Issue> issues) {
        return issues.stream()
            .map(this::getMessage)
            .distinct()
            .toList();
    }

    private void fillEdgesWithIssues(List<Edge> edges, List<Issue> issues) {
        edges.forEach(edge ->
            popIssue(issues, edge).ifPresent(issue ->
                    edge.setMessage(getMessage(issue))
                        .setStatus(ERROR)));
    }

    private Optional<Issue> popIssue(List<Issue> issues, Edge edge) {
        for (int i = 0; i < issues.size(); i++) {
            var issue = issues.get(i);
            if (doesEdgeHasIssue(edge, issue)) {
                issues.remove(i);
                return of(issue);
            }
        }
        return empty();
    }

    private List<Edge> getEdges(List<Microservice> microservices) {
        return microservices.stream()
            .flatMap(microservice -> microservice.getUsesMicroservices().stream()
                .map(usesRelationship -> new Edge()
                    .setId(usesRelationship.getId())
                    .setSource(microservice.getName())
                    .setContractName(usesRelationship.getContractName())
                    .setTarget(usesRelationship.getMicroservice().getName())
                    .setStatus(OK)))
            .collect(toCollection(ArrayList::new));
    }

    private List<Node> getNodes(List<Microservice> microservices) {
        return microservices.stream()
            .map(microservice -> new Node().
                setId(microservice.getName())
                .setStatus(OK))
            .collect(toCollection(ArrayList::new));
    }

    private boolean doesEdgeHasIssue(Edge edge, Issue issue) {
        return
            issue.getCausedAsConsumerByAsString() != null &&
                issue.getCausedAsProviderBy() != null &&
                issue.getCausedAsConsumerBy() != null &&
                issue.getCausedAsProviderBy().getName().equals(edge.getTarget()) &&
                issue.getCausedAsConsumerBy().getName().equals(edge.getSource()) &&
                issue.getAssociatedContracts().getFirst().getName().equals(edge.getContractName());
    }
}
