package dev.misei.records;

public class Either <T> {

    private final T value;
    private final String raw;

    private Either(T value, String raw) {
        this.value = value;
        this.raw = raw;
    }

    public static <T> Either<T> success(T value) {
        return new Either<>(value, null);
    }

    public static <T> Either<T> error(String raw) {
        return new Either<T>(null, raw);
    }

    public boolean isSuccess() {
        return raw == null;
    }

    public String getRaw() {
        return raw;
    }

    public T getValue() {
        return value;
    }
}
