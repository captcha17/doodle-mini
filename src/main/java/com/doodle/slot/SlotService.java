package com.doodle.slot;

import com.doodle.calendar.Calendar;
import com.doodle.calendar.CalendarRepository;
import com.doodle.common.exception.ConflictException;
import com.doodle.common.exception.ResourceNotFoundException;
import com.doodle.slot.dto.CommonAvailabilityResponse;
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

        Calendar calendar = calendarRepository.findById(request.calendarId())
                .orElseThrow(() -> ResourceNotFoundException.of("Calendar", request.calendarId()));

        if (slotRepository.existsOverlapping(calendar.getId(), request.startTime(), request.endTime(), null)) {
            throw new ConflictException("Slot overlaps with an existing slot in this calendar");
        }

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

        if (slotRepository.existsOverlapping(slot.getCalendar().getId(), newStart, newEnd, slot.getId())) {
            throw new ConflictException("Updated slot overlaps with an existing slot");
        }

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

    @Transactional(readOnly = true)
    public List<SlotResponse> getAvailability(Long calendarId, LocalDateTime from, LocalDateTime to, SlotStatus status) {
        validateTimes(from, to);
        List<Slot> slots = status != null
                ? slotRepository.findByCalendarIdAndTimeRangeAndStatus(calendarId, from, to, status)
                : slotRepository.findByCalendarIdAndTimeRange(calendarId, from, to);
        return slots.stream().map(SlotResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CommonAvailabilityResponse> getCommonAvailability(List<Long> calendarIds, LocalDateTime from, LocalDateTime to) {
        validateTimes(from, to);
        if (calendarIds == null || calendarIds.isEmpty()) {
            throw new IllegalArgumentException("At least one calendarId is required");
        }

        List<CommonAvailabilityResponse> result = slotRepository
                .findByCalendarIdAndTimeRangeAndStatus(calendarIds.get(0), from, to, SlotStatus.AVAILABLE)
                .stream()
                .map(s -> new CommonAvailabilityResponse(s.getStartTime(), s.getEndTime()))
                .toList();

        for (int i = 1; i < calendarIds.size(); i++) {
            List<CommonAvailabilityResponse> next = slotRepository
                    .findByCalendarIdAndTimeRangeAndStatus(calendarIds.get(i), from, to, SlotStatus.AVAILABLE)
                    .stream()
                    .map(s -> new CommonAvailabilityResponse(s.getStartTime(), s.getEndTime()))
                    .toList();

            result = intersect(result, next);
        }

        return result;
    }

    private List<CommonAvailabilityResponse> intersect(
            List<CommonAvailabilityResponse> a,
            List<CommonAvailabilityResponse> b
    ) {
        return a.stream()
                .flatMap(slotA -> b.stream()
                        .map(slotB -> {
                            LocalDateTime start = slotA.from().isAfter(slotB.from()) ? slotA.from() : slotB.from();
                            LocalDateTime end = slotA.to().isBefore(slotB.to()) ? slotA.to() : slotB.to();
                            return start.isBefore(end) ? new CommonAvailabilityResponse(start, end) : null;
                        })
                        .filter(r -> r != null)
                )
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
