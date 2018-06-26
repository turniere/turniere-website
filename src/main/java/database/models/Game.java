package database.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private List<Team> teams;
    private Status status;
    private Timestamp time;

    private enum Status {
        PRE, RUN, ONE, TWO
    }

    public Game(Team team1,Team team2, Status status, Timestamp time){
        this.teams.set(0,team1);
        this.teams.set(1,team2);
        this.status = status;
        this.time = time;
    }

    public Game(String teamOneName, String teamTwoName, Status status, Timestamp time) {
        Team team1 = new Team(teamOneName);
        Team team2 = new Team(teamTwoName);
        new Game(team1,team2,status,time);
    }

    public
        this.status = evaluateWinner(this.teams.get(0),this.teams.get(1));
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
        }else if (team1.getPoints() < team2.getPoints()) {
            return Status.TWO;
        }else return Status.RUN; //TODO don't allow equal points
    }

}
