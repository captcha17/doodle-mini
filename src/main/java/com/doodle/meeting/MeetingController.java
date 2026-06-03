package com.doodle.meeting;

import com.doodle.meeting.dto.CreateMeetingRequest;
import com.doodle.meeting.dto.MeetingResponse;
import com.doodle.meeting.dto.UpdateMeetingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingResponse createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        return meetingService.createMeeting(request);
    }

    @GetMapping("/{id}")
    public MeetingResponse getMeeting(@PathVariable Long id) {
        return meetingService.getMeeting(id);
    }

    @PutMapping("/{id}")
    public MeetingResponse updateMeeting(@PathVariable Long id, @Valid @RequestBody UpdateMeetingRequest request) {
        return meetingService.updateMeeting(id, request);
    }

    @GetMapping("/calendar/{calendarId}")
    public List<MeetingResponse> getMeetingsByCalendar(@PathVariable Long calendarId) {
        return meetingService.getMeetingsByCalendar(calendarId);
    }
}
