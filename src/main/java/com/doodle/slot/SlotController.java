package com.doodle.slot;

import com.doodle.slot.dto.CreateSlotRequest;
import com.doodle.slot.dto.SlotResponse;
import com.doodle.slot.dto.UpdateSlotRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Tag(name = "Slots")
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a time slot")
    public SlotResponse createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return slotService.createSlot(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get slot by id")
    public SlotResponse getSlot(@PathVariable Long id) {
        return slotService.getSlot(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update slot time or status")
    public SlotResponse updateSlot(@PathVariable Long id, @Valid @RequestBody UpdateSlotRequest request) {
        return slotService.updateSlot(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a slot")
    public void deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
    }

    @GetMapping("/calendar/{calendarId}")
    @Operation(summary = "Get all slots for a calendar")
    public List<SlotResponse> getSlotsByCalendar(@PathVariable Long calendarId) {
        return slotService.getSlotsByCalendar(calendarId);
    }

    @GetMapping("/availability")
    @Operation(summary = "Query free or busy slots for a time range")
    public List<SlotResponse> getAvailability(
            @RequestParam Long calendarId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) SlotStatus status
    ) {
        return slotService.getAvailability(calendarId, from, to, status);
    }
}
