package de.dhbw.karlsruhe.turniere.forms;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ChangeTeamForm {
    @NotEmpty
    @Size(max = 255)
    private String name;
}
