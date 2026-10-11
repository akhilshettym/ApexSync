package com.apexsync.backend.dto;

import jakarta.validation.constraints.NotNull;

public record LockSeatRequest(
        @NotNull(message = "Seat ID is required")
        Long seatId,

        @NotNull(message = "Event ID is required")
        Long eventId,

        @NotNull(message = "User ID is required")
        Long userId
) {}