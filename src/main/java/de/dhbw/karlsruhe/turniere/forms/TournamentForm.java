package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class TournamentForm {
    @NotEmpty
    private String name;

    private String description;

    private Boolean isPublic;

    @NotEmpty
    private String teamNames;
}
