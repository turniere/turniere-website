package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MatchResultSubmitForm {
    @Min(0)
    private Integer scoreTeam1;

    @Min(0)
    private Integer scoreTeam2;

    @NotNull
    private Boolean isLive;
}
