package edu.tcu.cs.backend.game;

import edu.tcu.cs.backend.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GameScheduleServiceImpl implements GameScheduleService {

    private final GameScheduleRepository gameScheduleRepository;
    private final GameRepository gameRepository;

    public GameScheduleServiceImpl(GameScheduleRepository gameScheduleRepository, GameRepository gameRepository) {
        this.gameScheduleRepository = gameScheduleRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public Game addGame(Game newGame){
        return this.gameRepository.save(newGame);
    }

    @Override
    public GameSchedule addGameSchedule(GameSchedule gameSchedule) {
        return this.gameScheduleRepository.save(gameSchedule);
    }

    @Override
    public List<GameSchedule> findAllGameSchedules() {
        return this.gameScheduleRepository.findAll();
    }

    @Override
    public List<Game> findAllGames() {
        return this.gameRepository.findAll();
    }

    @Override
    public void addGameToSchedule(Integer scheduleId, Game game) {
        GameSchedule assignedSchedule = this.gameScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ObjectNotFoundException("Game schedule", scheduleId));
        assignedSchedule.addGame(game);
        game.setGameSchedule(assignedSchedule);

        this.gameRepository.save(game);
    }

    @Override
    public List<Game> findGamesByScheduleId(int scheduleId) {
        GameSchedule schedule = this.gameScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ObjectNotFoundException("game", scheduleId));
        List<Game> scheduledGames = schedule.getGames();
        return scheduledGames;
    }
}
