package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stages")
@Getter
@Setter
@NoArgsConstructor
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer level;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Match> matches = new ArrayList<>();

    public Stage(Integer level, List<Match> matches) {
        this.level = level;
        this.matches = matches;
    }
}
