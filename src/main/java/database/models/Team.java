package database.models;

public class Team {
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

    private String name;
    private Integer points;
}
