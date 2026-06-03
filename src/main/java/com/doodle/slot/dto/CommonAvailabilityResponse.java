package com.doodle.slot.dto;

import java.time.LocalDateTime;

public record CommonAvailabilityResponse(
        LocalDateTime from,
        LocalDateTime to
) {}
