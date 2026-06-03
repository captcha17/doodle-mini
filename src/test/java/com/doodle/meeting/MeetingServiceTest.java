package com.doodle.meeting;

import com.doodle.AbstractIntegrationTest;
import com.doodle.calendar.CalendarRepository;
import com.doodle.common.exception.ConflictException;
import com.doodle.meeting.dto.CreateMeetingRequest;
import com.doodle.meeting.dto.MeetingResponse;
import com.doodle.slot.Slot;
import com.doodle.slot.SlotRepository;
import com.doodle.slot.SlotStatus;
import com.doodle.user.UserRepository;
import com.doodle.user.UserService;
import com.doodle.user.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class MeetingServiceTest extends AbstractIntegrationTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;

    private Long calendarId;
    private Long slotId;
    private Long userId;

    @BeforeEach
    void setUp() {
        var userResponse = userService.createUser(new CreateUserRequest("Dima", "dima@test.com"));
        userId = userResponse.id();
        calendarId = userResponse.calendarId();

        var calendar = calendarRepository.findById(calendarId).orElseThrow();

        Slot slot = new Slot();
        slot.setCalendar(calendar);
        slot.setStartTime(LocalDateTime.now().plusDays(1));
        slot.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        slot.setStatus(SlotStatus.AVAILABLE);
        slotId = slotRepository.save(slot).getId();
    }

    @Test
    void createMeeting_shouldBookSlotAndSetBusy() {
        CreateMeetingRequest request = new CreateMeetingRequest(slotId, "Team sync", "Weekly sync", List.of());

        MeetingResponse response = meetingService.createMeeting(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Team sync");
        assertThat(response.slotId()).isEqualTo(slotId);

        Slot slot = slotRepository.findById(slotId).orElseThrow();
        assertThat(slot.getStatus()).isEqualTo(SlotStatus.BUSY);
        assertThat(slot.getMeeting()).isNotNull();
    }

    @Test
    void createMeeting_shouldFailIfSlotAlreadyBooked() {
        meetingService.createMeeting(new CreateMeetingRequest(slotId, "First meeting", null, List.of()));

        assertThatThrownBy(() ->
                meetingService.createMeeting(new CreateMeetingRequest(slotId, "Second meeting", null, List.of()))
        ).isInstanceOf(ConflictException.class);
    }

    @Test
    void createMeeting_withParticipants_shouldAddThem() {
        var participant = userService.createUser(new CreateUserRequest("Alex", "alex@test.com"));

        CreateMeetingRequest request = new CreateMeetingRequest(slotId, "Planning", null, List.of(participant.id()));

        MeetingResponse response = meetingService.createMeeting(request);

        assertThat(response.participants()).hasSize(1);
        assertThat(response.participants().get(0).userId()).isEqualTo(participant.id());
    }
}
