package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class ProfileForm {
    private String username;

    @Email
    private String email;
}
