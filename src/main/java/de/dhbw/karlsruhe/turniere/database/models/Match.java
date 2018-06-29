package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team1;

    @ManyToOne
    private Team team2;

    private Integer scoreTeam1;

    private Integer scoreTeam2;

    private State state;

    private Integer position;

    public Match(Team team1, Team team2, Integer scoreTeam1, Integer scoreTeam2, State state, Integer position) {
        this.team1 = team1;
        this.team2 = team2;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.state = state;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Match{" +
                "team1=" + team1 +
                ", team2=" + team2 +
                ", scoreTeam1=" + scoreTeam1 +
                ", scoreTeam2=" + scoreTeam2 +
                ", state=" + state +
                ", position=" + position +
                '}';
    }

    public enum State {
        TEAM1_WON, TEAM2_WON, IN_PROGRESS, NOT_STARTED
    }
}
