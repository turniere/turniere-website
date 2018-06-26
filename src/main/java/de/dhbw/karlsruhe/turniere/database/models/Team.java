package de.dhbw.karlsruhe.turniere.database.models;

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

    public String getName() {

        return name;
    }

    public Team setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getPoints() {
        return points;
    }

    public Team setPoints(Integer points) {
        this.points = points;
        return this;
    }
}
