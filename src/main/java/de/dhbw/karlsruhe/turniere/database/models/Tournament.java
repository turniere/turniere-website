package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String code;

    private String description;

    @NotNull
    private Boolean isPublic;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Team> teams;

    @OneToOne
    private GroupStage groupStage;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<Stage> stages = new ArrayList<>();

    private String currentStage;

    private byte[] qrcode;

    @ManyToOne
    private Team winner;

    @ManyToOne
    private User owner;

    public Tournament(String name, String code, String description, Boolean isPublic, List<Team> teams) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.isPublic = isPublic;
        this.teams = teams;
    }

    public void addStage(Stage stage) {
        stages.add(stage);
    }

    public List<Match> getAllMatches() {
        List<Match> response = new ArrayList<>();
        GroupStage groupStage = this.groupStage;

        if (groupStage != null) {
            List<Group> groups = groupStage.getGroups();
            groups.sort(Comparator.comparingInt(Group::getPosition));
            for (Group group : groups) {
                List<Match> groupMatches = group.getMatches();
                groupMatches.sort(Comparator.comparingInt(Match::getPosition));
                for (Match match : groupMatches) {
                    match.setPosition(group.getPosition());
                    response.add(match);
                }

            }
        }

        List<Stage> tournamentStages = this.stages;
        tournamentStages.sort(Comparator.comparingInt(Stage::getLevel).reversed());
        for (Stage stage : tournamentStages) {
            List<Match> stageMatches = stage.getMatches();
            stageMatches.sort(Comparator.comparingInt(Match::getPosition));
            for (Match match : stageMatches) {
                match.setStageId(stage.getLevel());
                response.add(match);
            }

        }
        return response;
    }
}
