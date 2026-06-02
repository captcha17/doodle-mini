package com.doodle.slot;

import com.doodle.slot.dto.CreateSlotRequest;
import com.doodle.slot.dto.SlotResponse;
import com.doodle.slot.dto.UpdateSlotRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotResponse createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return slotService.createSlot(request);
    }

    @GetMapping("/{id}")
    public SlotResponse getSlot(@PathVariable Long id) {
        return slotService.getSlot(id);
    }

    @PutMapping("/{id}")
    public SlotResponse updateSlot(@PathVariable Long id, @Valid @RequestBody UpdateSlotRequest request) {
        return slotService.updateSlot(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
    }

    @GetMapping("/calendar/{calendarId}")
    public List<SlotResponse> getSlotsByCalendar(@PathVariable Long calendarId) {
        return slotService.getSlotsByCalendar(calendarId);
    }
}
