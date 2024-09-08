package dev.misei.processor;

import dev.misei.records.Alert;
import dev.misei.records.QueryTerm;
import dev.misei.records.Result;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class MatchProcessor {

    private final Set<Result> approvedMessages = new HashSet<>();

    public void processMatches(Stream<QueryTerm> queryTerms, Stream<Alert> alerts) {
        //TODO: Refactor this to maintain streams. Right now we are not lazy loading with this.
        var alertsList = alerts.toList();

        queryTerms.forEach(queryTerm -> alertsList.forEach(alert -> {
            if (matches(alert, queryTerm)) {
                approvedMessages.add(new Result(alert, queryTerm));
            }
        }));
    }

    // Predicate to check if alert matches the query term
    private boolean matches(Alert alert, QueryTerm queryTerm) {
        return alert.contents().stream().anyMatch(content -> {
            String contentText = content.text().toLowerCase();
            String queryText = queryTerm.text().toLowerCase();

            System.out.println(contentText);
            System.out.println(queryText);

            if (queryTerm.keepOrder()) {
                return contentText.contains(queryText);
            } else {
                String[] queryWords = queryText.split("\\s+");
                return Stream.of(queryWords).allMatch(contentText::contains);
            }
        });
    }

    public Set<Result> getResult() {
        return approvedMessages;
    }
}
