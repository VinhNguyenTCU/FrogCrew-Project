package edu.tcu.cs.backend.game;

import edu.tcu.cs.backend.game.converter.GameDtoToGameConverter;
import edu.tcu.cs.backend.game.converter.GameScheduleDtoToGameScheduleConverter;
import edu.tcu.cs.backend.game.converter.GameScheduleToGameScheduleDtoConverter;
import edu.tcu.cs.backend.game.converter.GameToGameDtoConverter;
import edu.tcu.cs.backend.game.dto.GameDto;
import edu.tcu.cs.backend.game.dto.GameScheduleDto;
import edu.tcu.cs.backend.system.Result;
import edu.tcu.cs.backend.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gameSchedule")
public class GameScheduleController {
    private final GameScheduleService gameScheduleService;
    private final GameScheduleDtoToGameScheduleConverter gameScheduleDtoToGameScheduleConverter;
    private final GameScheduleToGameScheduleDtoConverter gameScheduleToGameScheduleDtoConverter;
    private final GameDtoToGameConverter gameDtoToGameConverter;
    private final GameToGameDtoConverter gameToGameDtoConverter;

    public GameScheduleController(GameScheduleService gameScheduleService, GameScheduleDtoToGameScheduleConverter gameScheduleDtoToGameScheduleConverter, GameScheduleToGameScheduleDtoConverter gameScheduleToGameScheduleDtoConverter, GameDtoToGameConverter gameDtoToGameConverter, GameToGameDtoConverter gameToGameDtoConverter){
        this.gameScheduleService = gameScheduleService;
        this.gameScheduleDtoToGameScheduleConverter = gameScheduleDtoToGameScheduleConverter;
        this.gameScheduleToGameScheduleDtoConverter = gameScheduleToGameScheduleDtoConverter;
        this.gameDtoToGameConverter = gameDtoToGameConverter;
        this.gameToGameDtoConverter = gameToGameDtoConverter;
    }

    @PostMapping
    public Result addGameSchedule(@RequestBody @Valid GameSchedule newSchedule) {
        GameSchedule savedSchedule = this.gameScheduleService.addGameSchedule(newSchedule);
        GameScheduleDto scheduleDTO = this.gameScheduleToGameScheduleDtoConverter.convert(savedSchedule);
        return new Result(true, StatusCode.SUCCESS, "Schedule added", scheduleDTO);
    }

    @GetMapping
    public Result findAllGameSchedules() {
        List<GameSchedule> schedules = this.gameScheduleService.findAllGameSchedules();
        // Convert to DTO
        List<GameScheduleDto> schedulesDTO = schedules.stream()
                .map(this.gameScheduleToGameScheduleDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Found all schedules", schedulesDTO);
    }

    @GetMapping("/game")
    public Result findAllGames() {
        List<Game> games = this.gameScheduleService.findAllGames();
        // Convert to DTO
        List<GameDto> gamesDTO = games.stream()
                .map(this.gameToGameDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Found all games", gamesDTO);
    }

    @PostMapping("/{scheduleId}/game")
    public Result addGameToSchedule(@PathVariable Integer scheduleId, @RequestBody @Valid Game newGame) {
        this.gameScheduleService.addGameToSchedule(scheduleId, newGame);
        GameDto gameDTO = this.gameToGameDtoConverter.convert(newGame);
        return new Result(true, StatusCode.SUCCESS, "Game added to schedule " + scheduleId, gameDTO);
    }

    @GetMapping("/{scheduleId}/game")
    public Result findGamesByScheduleId(@PathVariable Integer scheduleId) {
        List<Game> games = this.gameScheduleService.findGamesByScheduleId(scheduleId);
        List<GameDto> gamesDTO = games.stream()
                .map(this.gameToGameDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Found all games with scheduleId " + scheduleId, gamesDTO);
    }
}
