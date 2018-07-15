package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MatchResultSubmitForm {
    @NotNull
    @Min(0)
    private Integer score1;

    @NotNull
    @Min(0)
    private Integer score2;

    @NotNull
    private Boolean live;
}
