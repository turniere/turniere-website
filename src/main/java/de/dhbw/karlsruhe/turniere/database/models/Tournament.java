package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Stage> stages = new ArrayList<>();

    private byte[] qrcode;

    @ManyToOne
    private Team winner;

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
}
