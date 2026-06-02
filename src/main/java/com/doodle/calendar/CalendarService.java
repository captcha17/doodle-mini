package com.doodle.calendar;

import com.doodle.calendar.dto.CalendarResponse;
import com.doodle.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;

    @Transactional(readOnly = true)
    public CalendarResponse getCalendarByUserId(Long userId) {
        Calendar calendar = calendarRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Calendar not found: " + userId));

        return CalendarResponse.from(calendar);
    }

    @Transactional(readOnly = true)
    public CalendarResponse getCalendarById(Long id) {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Calendar", id));

        return CalendarResponse.from(calendar);
    }
}
