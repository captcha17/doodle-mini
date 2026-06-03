package com.doodle.meeting;

import com.doodle.common.exception.ConflictException;
import com.doodle.common.exception.ResourceNotFoundException;
import com.doodle.meeting.dto.CreateMeetingRequest;
import com.doodle.meeting.dto.MeetingResponse;
import com.doodle.meeting.dto.UpdateMeetingRequest;
import com.doodle.slot.Slot;
import com.doodle.slot.SlotRepository;
import com.doodle.slot.SlotStatus;
import com.doodle.user.User;
import com.doodle.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    @Transactional
    public MeetingResponse createMeeting(CreateMeetingRequest request) {
        Slot slot = slotRepository.findById(request.slotId())
                .orElseThrow(() -> ResourceNotFoundException.of("Slot", request.slotId()));

        if (!slot.isAvailable()) {
            throw new ConflictException("Slot is not available");
        }
        if (slot.hasMeeting()) {
            throw new ConflictException("Slot already has a meeting");
        }

        Meeting meeting = new Meeting();
        meeting.setTitle(request.title());
        meeting.setDescription(request.description());
        meetingRepository.save(meeting);

        addParticipants(meeting, request.participantUserIds());

        slot.setStatus(SlotStatus.BUSY);
        slot.setMeeting(meeting);
        slotRepository.save(slot);

        return MeetingResponse.from(meeting, slot.getId());
    }

    @Transactional(readOnly = true)
    public MeetingResponse getMeeting(Long id) {
        Meeting meeting = meetingRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Meeting", id));
        Long slotId = slotRepository.findByMeetingId(id).map(Slot::getId).orElse(null);
        return MeetingResponse.from(meeting, slotId);
    }

    @Transactional
    public MeetingResponse updateMeeting(Long id, UpdateMeetingRequest request) {
        Meeting meeting = meetingRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Meeting", id));

        meeting.setTitle(request.title());
        meeting.setDescription(request.description());
        meeting.getParticipants().clear();
        addParticipants(meeting, request.participantUserIds());

        meetingRepository.save(meeting);

        Long slotId = slotRepository.findByMeetingId(id).map(Slot::getId).orElse(null);
        return MeetingResponse.from(meeting, slotId);
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> getMeetingsByCalendar(Long calendarId) {
        return meetingRepository.findAllByCalendarId(calendarId).stream()
                .map(m -> {
                    Long slotId = slotRepository.findByMeetingId(m.getId()).map(Slot::getId).orElse(null);
                    return MeetingResponse.from(m, slotId);
                })
                .toList();
    }

    private void addParticipants(Meeting meeting, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> {
            MeetingParticipant participant = new MeetingParticipant();
            participant.setMeeting(meeting);
            participant.setUser(user);
            meeting.getParticipants().add(participant);
        });
    }
}
