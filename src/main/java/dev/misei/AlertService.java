package dev.misei;


import dev.misei.processor.CleanErrorsProcessor;
import dev.misei.processor.MatchProcessor;
import dev.misei.records.Alert;
import dev.misei.records.Either;
import dev.misei.records.QueryTerm;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class AlertService {
    private static AlertService instance;

    private final ApiAdapter apiAdapter;
    private final Supplier<Stream<Either<QueryTerm>>> queryTermStreamSupplier;
    private final Supplier<Stream<Either<Alert>>> alertStreamSupplier;
    private final CleanErrorsProcessor cleanErrorsProcessor;
    private final MatchProcessor matchProcessor;


    private AlertService() {
        this.apiAdapter = new ApiAdapter();
        this.queryTermStreamSupplier = apiAdapter.fetchQueryTerms();
        this.alertStreamSupplier = apiAdapter.fetchAlerts();
        this.cleanErrorsProcessor = new CleanErrorsProcessor();
        this.matchProcessor = new MatchProcessor();
    }

    public void processBatchForMatchValidation() {
        Stream<Alert> alertCleanStream = cleanErrorsProcessor.cleanStream(alertStreamSupplier.get());
        Stream<QueryTerm> queryTermCleanStream = cleanErrorsProcessor.cleanStream(queryTermStreamSupplier.get());

        matchProcessor.processMatches(queryTermCleanStream, alertCleanStream);

        //TODO: Push somewhere this information
        cleanErrorsProcessor.getErrors();
        matchProcessor.getResult();
    }

    public static synchronized AlertService getInstance() {
        if (instance == null) {
            instance = new AlertService();
        }
        return instance;
    }
}
