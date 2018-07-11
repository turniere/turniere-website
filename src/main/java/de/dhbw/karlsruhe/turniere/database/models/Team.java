package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer groupScore;

    private Integer groupPointsScored;

    private Integer groupPointsReceived;

    private Integer groupPlace;

    public Team(String name) {
        this.name = name;
        this.groupScore = 0;
        this.groupPointsReceived = 0;
        this.groupPointsScored = 0;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                '}';
    }
}
