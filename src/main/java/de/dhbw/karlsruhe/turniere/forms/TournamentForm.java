package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
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

    @Min(0)
    private Integer groupSize;

    private Boolean mix;
}
