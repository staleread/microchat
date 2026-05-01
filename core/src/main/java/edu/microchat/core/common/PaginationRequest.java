package edu.microchat.core.common;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PaginationRequest(
    @PositiveOrZero(message = "Page index must be zero or positive") int page,
    @Positive(message = "Page size must be positive") int size) {}
