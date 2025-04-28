package edu.tcu.cs.backend.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter @Getter @NoArgsConstructor
@Entity
@Table(name = "schedules")
public class GameSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    private String sport;
    private String season;

    /**
     * All the games that belong to this schedule
     *
     * - `mappedBy = "schedule"` means "Game.schedule" is the owning side".
     * - Cascade ALL & orphanRemoval ensure that if you save/delete a schedule,
     * its games are saved/deleted for you.
     */
    @OneToMany
    private List<Game> games = new ArrayList<>();

    public void addGame(Game game) {
        game.setGameSchedule(this);
        this.games.add(game);
    }

}
