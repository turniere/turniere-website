package de.dhbw.karlsruhe.turniere.database.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

//@Entity
//@Table(name = "games")
public class Game {
/*
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private List<Team> teams;
    private Status status;
    private Timestamp time;

    public Game(Team team1, Team team2, Status status, Timestamp time) {
        this.teams.set(0, team1);
        this.teams.set(1, team2);
        this.status = status;
        this.time = time;
    }

    public Game(String teamOneName, String teamTwoName, Status status, Timestamp time) {
        Team team1 = new Team(teamOneName);
        Team team2 = new Team(teamTwoName);
        new Game(team1, team2, status, time);
    }

    public Game(String teamOneName, String teamTwoName, Timestamp time) {
        new Game(teamOneName, teamTwoName, Status.PRE, time);
    }

    public void setPoints(Integer teamOnePoints, Integer teamTwoPoints) {
        this.teams.set(0, this.teams.get(0).setPoints(teamOnePoints));
        this.teams.set(1, this.teams.get(1).setPoints(teamTwoPoints));
    }

    public void setResult(Integer teamOnePoints, Integer teamTwoPoints) {
        setPoints(teamOnePoints, teamTwoPoints);
        this.status = evaluateWinner(this.teams.get(0), this.teams.get(1));
    }

    public void gameIsOver() {
        if (this.teams.isEmpty()) {
            return; //TODO real error
        }
        this.status = evaluateWinner(this.teams.get(0), this.teams.get(1));
    }

    public String getWinner() {
        switch (this.status) {
            case ONE:
                return this.teams.get(0).getName();
            case TWO:
                return this.teams.get(1).getName();
            case PRE:
                return "Game not started yet";
            case RUN:
                return "Game still running";
            default:
                return "Something went wrong."; //TODO real error
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    private Status evaluateWinner(Team team1, Team team2) {
        if (team1.getPoints() > team2.getPoints()) {
            return Status.ONE;
        } else if (team1.getPoints() < team2.getPoints()) {
            return Status.TWO;
        } else return Status.RUN; //TODO don't allow equal points
    }

    private enum Status {
        PRE, RUN, ONE, TWO
    }
*/
}
