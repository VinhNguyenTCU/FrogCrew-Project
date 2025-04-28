package edu.tcu.cs.backend.availability;

import edu.tcu.cs.backend.game.Game;
import edu.tcu.cs.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter @Getter @NoArgsConstructor
@Entity
@Table(name = "availability")
@IdClass(AvailabilityId.class)
public class Availability implements Serializable {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "game_id")
    private Integer gameId;
    private Boolean open;
    private String comment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    private Game game;

}
