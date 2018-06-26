package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Team {
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
