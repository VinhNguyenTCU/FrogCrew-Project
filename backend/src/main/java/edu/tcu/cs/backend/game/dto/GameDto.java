package edu.tcu.cs.backend.game.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameDto(
        @NotNull
        Integer gameId,
        Integer scheduleId,
        @NotEmpty(message = "Game date is required.")
        String gameDate,
        @NotEmpty(message = "Venue is required.")
        String venue,
        @NotEmpty(message = "Opponent is required.")
        String opponent,
        Boolean isFinalized
) {
}
