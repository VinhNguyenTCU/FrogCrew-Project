package edu.tcu.cs.backend.game.converter;

import edu.tcu.cs.backend.game.GameSchedule;
import edu.tcu.cs.backend.game.dto.GameScheduleDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameScheduleDtoToGameScheduleConverter implements Converter<GameScheduleDto, GameSchedule> {


    @Override
    public GameSchedule convert(GameScheduleDto source) {
        GameSchedule gameSchedule = new GameSchedule();
        gameSchedule.setScheduleId(source.scheduleId());
        gameSchedule.setSport(source.sport());
        gameSchedule.setSeason(source.season());
        return gameSchedule;
    }
}
