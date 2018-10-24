package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "group_stages")
@Getter
@Setter
@NoArgsConstructor

public class GroupStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<Group> groups;
    private int playoffSize;
    private int currentPhase;

    public GroupStage(List<Group> groups) {
        this.groups = groups;
        this.currentPhase = 0;
    }

    public List<Match> getCurrentMatches() {
        List<Match> matches = new ArrayList<>();
        List<Group> groups = this.groups;
        groups.sort(Comparator.comparingInt(Group::getPosition));
        for (Group group : groups) {
            Match match = group.getMatches().stream().filter((streamMatch -> streamMatch.getPosition() == this.currentPhase))
                    .collect(Collectors.toList()).get(0);
            match.setPosition(group.getPosition());
            matches.add(match);
        }
        return matches;
    }

}
