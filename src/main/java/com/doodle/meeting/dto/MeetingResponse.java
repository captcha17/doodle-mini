package com.doodle.meeting.dto;

import com.doodle.meeting.Meeting;

import java.time.Instant;
import java.util.List;

public record MeetingResponse(
        Long id,
        String title,
        String description,
        Long slotId,
        List<ParticipantDto> participants,
        Instant createdAt,
        Instant updatedAt
) {
    public record ParticipantDto(Long userId, String name, String email) {}

    public static MeetingResponse from(Meeting meeting, Long slotId) {
        List<ParticipantDto> participants = meeting.getParticipants().stream()
                .map(p -> new ParticipantDto(
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getEmail()
                ))
                .toList();

        return new MeetingResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                slotId,
                participants,
                meeting.getCreatedAt(),
                meeting.getUpdatedAt()
        );
    }
}
