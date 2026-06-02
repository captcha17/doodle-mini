package com.doodle.calendar.dto;

import com.doodle.calendar.Calendar;

import java.time.Instant;

public record CalendarResponse(
        Long id,
        Long userId,
        String timezone,
        Instant createdAt
) {
    public static CalendarResponse from(Calendar calendar) {
        return new CalendarResponse(
                calendar.getId(),
                calendar.getUser().getId(),
                calendar.getTimezone(),
                calendar.getCreatedAt()
        );
    }
}
