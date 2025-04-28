package edu.tcu.cs.backend.crewlist;

import edu.tcu.cs.backend.game.Game;
import edu.tcu.cs.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter @NoArgsConstructor
@Table(name = "crew_lists")
public class CrewList {
    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToMany
    @JoinTable(
            name = "crew_list_members",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedCrew = new ArrayList<>();

    private String location;
    private LocalDateTime reportTime;
}
