package com.doodle.calendar;

import com.doodle.calendar.dto.CalendarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/user/{userId}")
    public CalendarResponse getCalendarByUserId(@PathVariable Long userId) {
        return calendarService.getCalendarByUserId(userId);
    }

    @GetMapping("/{id}")
    public CalendarResponse getCalendarById(@PathVariable Long id) {
        return calendarService.getCalendarById(id);
    }
}
