package dev.misei;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.records.Alert;
import dev.misei.records.Either;
import dev.misei.records.QueryTerm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ApiAdapter {

    private static final String QUERY_TERM_API = "https://services.prewave.ai/adminInterface/api/testQueryTerm?key=";
    private static final String ALERTS_API = "https://services.prewave.ai/adminInterface/api/testAlerts?key=";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public ApiAdapter() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiKey = "";
    }

    public Supplier<Stream<Either<QueryTerm>>> fetchQueryTerms() {
        return () -> fetchObjects(QUERY_TERM_API, QueryTerm.class);
    }

    public Supplier<Stream<Either<Alert>>> fetchAlerts() {
        return () -> fetchObjects(ALERTS_API, Alert.class);
    }

    private <T> Stream<Either<T>> fetchObjects(String apiUrl, Class<T> clazz) {
        return makeApiRequest(apiUrl + apiKey)
                .map(inputStream -> streamFromInputStream(inputStream, clazz))
                .orElseGet(() -> {
                    System.out.println("Failed to fetch data from API: " + apiUrl);
                    return Stream.empty();
                });
    }

    private <T> Stream<Either<T>> streamFromInputStream(InputStream inputStream, Class<T> clazz) {
        try {
            JsonParser parser = objectMapper.getFactory().createParser(inputStream);

            Spliterator<Either<T>> spliterator = Spliterators.spliteratorUnknownSize(
                    new Iterator<Either<T>>() {
                        @Override
                        public boolean hasNext() {
                            try {
                                return !parser.isClosed() && parser.nextToken() != JsonToken.END_ARRAY;
                            } catch (IOException e) {
                                return false;
                            }
                        }

                        @Override
                        public Either<T> next() {
                            try {
                                T record = objectMapper.readValue(parser, clazz);
                                return Either.success(record);
                            } catch (IOException e) {
                                try {
                                    JsonNode rawRecord = parser.readValueAsTree();
                                    return Either.error(rawRecord.asText());
                                } catch (Exception rawException) {
                                    return Either.error(null);
                                }
                            }
                        }
                    },
                    Spliterator.ORDERED
            );

            return StreamSupport.stream(spliterator, false);
        } catch (Exception e) {
            //TODO: More robust logging
            System.out.println("Error initializing stream for " + clazz.getSimpleName() + ": " + e.getMessage());
            return Stream.empty();
        }
    }

    private Optional<InputStream> makeApiRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return Optional.ofNullable(response.body());
        } catch (Exception e) {
            System.out.println("Error fetching data from API: " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}