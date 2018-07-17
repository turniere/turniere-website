package de.dhbw.karlsruhe.turniere.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ChangeTournamentForm {
    @NotEmpty
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    private Boolean isPublic = false;
}
