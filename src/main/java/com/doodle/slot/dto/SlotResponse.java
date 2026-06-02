package com.doodle.slot.dto;

import com.doodle.slot.Slot;
import com.doodle.slot.SlotStatus;

import java.time.LocalDateTime;

public record SlotResponse(
        Long id,
        Long calendarId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        SlotStatus status,
        Long meetingId
) {
    public static SlotResponse from(Slot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getCalendar().getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus(),
                slot.getMeeting() != null ? slot.getMeeting().getId() : null
        );
    }
}
