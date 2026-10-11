package com.apexsync.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
        @NotBlank(message = "Event title is required")
        String title,

        @NotNull(message = "Venue ID is required")
        Long venueId,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be greater than zero")
        BigDecimal basePrice,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotEmpty(message = "At least one section layout configuration is required")
        List<SectionConfig> sections
) {
    public record SectionConfig(
            @NotBlank(message = "Section name is required")
            String sectionName,

            @Min(value = 1, message = "Row count must be at least 1")
            int rowCount,

            @Min(value = 1, message = "Seats per row must be at least 1")
            int seatsPerRow
    ) {}
}