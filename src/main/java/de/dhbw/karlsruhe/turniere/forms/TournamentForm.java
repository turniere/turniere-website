package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TournamentForm {
    @NotEmpty
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    private Boolean isPublic;

    @NotEmpty
    private String teamNames;

    @Min(0)
    private Integer groupSize;

    @NotNull
    private Boolean randomize;

    @NotNull
    private Integer playoffSize;
}
