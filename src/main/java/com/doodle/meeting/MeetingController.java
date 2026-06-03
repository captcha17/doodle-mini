package com.doodle.meeting;

import com.doodle.meeting.dto.CreateMeetingRequest;
import com.doodle.meeting.dto.MeetingResponse;
import com.doodle.meeting.dto.UpdateMeetingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book a meeting from an available slot")
    public MeetingResponse createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        return meetingService.createMeeting(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meeting by id")
    public MeetingResponse getMeeting(@PathVariable Long id) {
        return meetingService.getMeeting(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update meeting details or participants")
    public MeetingResponse updateMeeting(@PathVariable Long id, @Valid @RequestBody UpdateMeetingRequest request) {
        return meetingService.updateMeeting(id, request);
    }

    @GetMapping("/calendar/{calendarId}")
    @Operation(summary = "Get all meetings for a calendar")
    public List<MeetingResponse> getMeetingsByCalendar(@PathVariable Long calendarId) {
        return meetingService.getMeetingsByCalendar(calendarId);
    }
}
