package edu.tcu.cs.backend.game.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameScheduleDto(
        @NotNull(message = "Schedule is required")
        Integer scheduleId,

        @NotEmpty(message = "Sport is required")
        String sport,

        @NotEmpty(message = "Season is required")
        String season
) {
}
