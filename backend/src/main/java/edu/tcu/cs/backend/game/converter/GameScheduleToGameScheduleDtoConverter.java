package edu.tcu.cs.backend.game.converter;

import edu.tcu.cs.backend.game.GameSchedule;
import edu.tcu.cs.backend.game.dto.GameScheduleDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameScheduleToGameScheduleDtoConverter implements Converter<GameSchedule, GameScheduleDto> {
    @Override
    public GameScheduleDto convert(GameSchedule source) {
        GameScheduleDto scheduleDTO = new GameScheduleDto(
                source.getScheduleId(),
                source.getSport(),
                source.getSeason());
        return scheduleDTO;
    }
}
