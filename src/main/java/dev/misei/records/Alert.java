package dev.misei.records;

import javax.validation.constraints.NotNull;
import java.util.List;

public record Alert (
        @NotNull(message = "ID cannot be null")
        String id,
        @NotNull(message = "Contents cannot be null")
        List<Content> contents,
        @NotNull(message = "Date cannot be null")
        String date,
        @NotNull(message = "Language cannot be null")
        String language) {
}
