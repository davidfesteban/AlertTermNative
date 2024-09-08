package dev.misei.processor;

import dev.misei.records.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CleanErrorsProcessor {

    public final List<String> errors = new ArrayList<>();

    public <T> Stream<T> cleanStream(Stream<Either<T>> eitherStream) {
        return eitherStream.peek(new Consumer<Either<T>>() {
            @Override
            public void accept(Either<T> either) {
                if (!either.isSuccess()) {
                    errors.add(either.getRaw());
                }
            }
        }).filter(Either::isSuccess).map(Either::getValue);
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
