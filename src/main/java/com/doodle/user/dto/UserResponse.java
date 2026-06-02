package com.doodle.user.dto;

import com.doodle.user.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        Long calendarId,
        Instant createdAt
) {
    public static UserResponse from(User user, Long calendarId) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                calendarId,
                user.getCreatedAt()
        );
    }
}
