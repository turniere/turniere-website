package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "teams")
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer points;

    public Team(String name) {
        this.name = name;
    }

    public Team(String name, Integer points) {
        this.name = name;
        this.points = points;
    }
}
