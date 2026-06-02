package com.doodle.slot;

import com.doodle.calendar.Calendar;
import com.doodle.calendar.CalendarRepository;
import com.doodle.common.exception.ConflictException;
import com.doodle.common.exception.ResourceNotFoundException;
import com.doodle.slot.dto.CreateSlotRequest;
import com.doodle.slot.dto.SlotResponse;
import com.doodle.slot.dto.UpdateSlotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final SlotRepository slotRepository;
    private final CalendarRepository calendarRepository;

    @Transactional
    public SlotResponse createSlot(CreateSlotRequest request) {
        validateTimes(request.startTime(), request.endTime());

        Calendar calendar = calendarRepository.findById(request.calendarId()).orElseThrow(() -> ResourceNotFoundException.of("Calendar", request.calendarId()));

        Slot slot = new Slot();
        slot.setCalendar(calendar);
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());
        slot.setStatus(SlotStatus.AVAILABLE);

        return SlotResponse.from(slotRepository.save(slot));
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlot(Long id) {
        return SlotResponse.from(findById(id));
    }

    @Transactional
    public SlotResponse updateSlot(Long id, UpdateSlotRequest request) {
        Slot slot = findById(id);

        if (slot.hasMeeting()) {
            throw new ConflictException("Cannot modify a slot that already has a meeting");
        }

        LocalDateTime newStart = request.startTime() != null ? request.startTime() : slot.getStartTime();
        LocalDateTime newEnd = request.endTime() != null ? request.endTime() : slot.getEndTime();

        validateTimes(newStart, newEnd);

        slot.setStartTime(newStart);
        slot.setEndTime(newEnd);
        slot.setStatus(request.status());

        return SlotResponse.from(slotRepository.save(slot));
    }

    @Transactional
    public void deleteSlot(Long id) {
        Slot slot = findById(id);

        if (slot.hasMeeting()) {
            throw new ConflictException("Cannot delete a slot that already has a meeting");
        }

        slotRepository.delete(slot);
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsByCalendar(Long calendarId) {
        return slotRepository.findAllByCalendarIdOrderByStartTimeAsc(calendarId)
                .stream()
                .map(SlotResponse::from)
                .toList();
    }

    private Slot findById(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Slot", id));
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}
