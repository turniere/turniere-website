package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class RegisterForm {
    @NotEmpty
    @Getter
    @Setter
    private String username;

    @NotEmpty
    @Email
    @Getter
    @Setter
    private String email;

    @NotEmpty
    @Getter
    @Setter
    private String password;
}
