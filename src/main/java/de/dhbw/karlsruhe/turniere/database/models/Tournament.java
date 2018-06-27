package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    public Tournament(String name, String code, String description, Boolean isPublic, List<Team> teams) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.isPublic = isPublic;
        this.teams = teams;
    }
}
