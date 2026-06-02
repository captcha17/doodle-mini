package com.doodle.slot.dto;

import com.doodle.slot.SlotStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateSlotRequest(

        LocalDateTime startTime,

        LocalDateTime endTime,

        @NotNull(message = "Status is required")
        SlotStatus status
) {
}
