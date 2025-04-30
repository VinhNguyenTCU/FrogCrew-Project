package edu.tcu.cs.backend.game;

import edu.tcu.cs.backend.crewlist.CrewList;
import edu.tcu.cs.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gameId;

    @Column(name = "game_date")
    private String gameDate;
    private String venue;
    private String opponent;
    private Boolean isFinalized;

    @ElementCollection // Tells Hibernate "this is a collection of basic values (String), not entities".
    // Defines the join table and which column in it links back to the parent's primary key.
    @CollectionTable(
            name = "game_open_positions",
            joinColumns = @JoinColumn(name = "game_id")
    )
    @Column(name = "position")
    private List<String> openPositions = new ArrayList<>();

    // Which users have been assigned
    @ManyToMany
    private List<User> users = new ArrayList<>();

    // Back‐reference to its gameSchedule
    @ManyToOne
    private GameSchedule gameSchedule;

    // One‐to‐one link to its crew list
    @OneToOne
    private CrewList crewList;

}
