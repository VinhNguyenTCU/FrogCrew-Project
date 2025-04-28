package edu.tcu.cs.backend.game.converter;

import edu.tcu.cs.backend.game.Game;
import edu.tcu.cs.backend.game.dto.GameDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameToGameDtoConverter implements Converter<Game, GameDto> {

    private final GameScheduleToGameScheduleDtoConverter gameScheduleToGameScheduleDtoConverter;

    public GameToGameDtoConverter(GameScheduleToGameScheduleDtoConverter gameScheduleToGameScheduleDtoConverter) {
        this.gameScheduleToGameScheduleDtoConverter = gameScheduleToGameScheduleDtoConverter;
    }


    @Override
    public GameDto convert(Game source) {
        GameDto gameDTO = new GameDto(
                source.getGameId(),
                source.getGameSchedule() != null ? this.gameScheduleToGameScheduleDtoConverter.convert(source.getGameSchedule()).scheduleId() : null,
                source.getGameDate(),
                source.getVenue(),
                source.getOpponent(),
                source.getIsFinalized()
        );
        return gameDTO;
    }
}
