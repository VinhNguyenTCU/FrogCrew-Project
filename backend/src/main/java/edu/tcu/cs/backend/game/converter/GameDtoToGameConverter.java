package edu.tcu.cs.backend.game.converter;

import edu.tcu.cs.backend.game.Game;
import edu.tcu.cs.backend.game.dto.GameDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameDtoToGameConverter implements Converter<GameDto, Game> {

    @Override
    public Game convert(GameDto source) {
        Game game = new Game();
        game.setGameId(source.gameId());
        game.setGameDate(source.gameDate());
        game.setVenue(source.venue());
        game.setOpponent(source.opponent());
        game.setIsFinalized(source.isFinalized());
        return game;
    }
}
