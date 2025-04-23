package edu.tcu.cs.backend.crew;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users") // Alternative if you really must keep user: Quote the table name @Table(name = "\"USER\"")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;

    @ElementCollection
    @CollectionTable(
            name = "user_positions",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "position")
    private List<String> position;

    public User() {}

}
