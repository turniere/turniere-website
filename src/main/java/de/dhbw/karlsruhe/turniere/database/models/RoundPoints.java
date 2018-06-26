package de.dhbw.karlsruhe.turniere.database.models;

public class RoundPoints {
    private Integer points;
    private Integer stage;

    RoundPoints(Integer points, Integer stage) {
        this.points = points;
        this.stage = stage;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getStage() {
        return stage;
    }

}
