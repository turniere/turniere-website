package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(unique = true)
    @Getter
    @Setter
    private String username;

    @Column(unique = true)
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {}
}
