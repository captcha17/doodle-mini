package com.doodle.meeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateMeetingRequest(
        @NotNull(message = "Slot id is required")
        Long slotId,

        @NotBlank(message = "Title is required")
        @Size(max = 255)
        String title,

        String description,

        List<Long> participantUserIds
) {
}
