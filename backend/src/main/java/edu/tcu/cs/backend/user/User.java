package edu.tcu.cs.backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "users") // Alternative if you really must keep user: Quote the table name @Table(name = "\"USER\"")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer user_id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;

    @ElementCollection // Tells Hibernate "this is a collection of basic values (String), not entities".
    // Defines the join table and which column in it links back to the parent's primary key.
    @CollectionTable(
            name = "user_positions",
            joinColumns = @JoinColumn(name = "user_id") // Points back to user_id
    )
    @Column(name = "position") // Defines the column that actually stores each String
    private List<String> position;

}
