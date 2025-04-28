package edu.tcu.cs.backend.game;

import java.util.List;

public interface GameScheduleService {
    Game addGame(Game newGame);

    GameSchedule addGameSchedule(GameSchedule gameSchedule);

    List<GameSchedule> findAllGameSchedules();

    List<Game> findAllGames();

    void addGameToSchedule(Integer scheduleId, Game game);

    List<Game> findGamesByScheduleId(int scheduleId);
}
