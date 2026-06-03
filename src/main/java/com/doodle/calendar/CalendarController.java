package com.doodle.calendar;

import com.doodle.calendar.dto.CalendarResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendars")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get calendar by user id")
    public CalendarResponse getCalendarByUserId(@PathVariable Long userId) {
        return calendarService.getCalendarByUserId(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get calendar by id")
    public CalendarResponse getCalendarById(@PathVariable Long id) {
        return calendarService.getCalendarById(id);
    }
}
