package com.doodle.slot.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateSlotRequest(
        @NotNull(message = "Calendar id is required")
        Long calendarId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime
) {
}
