package de.dhbw.karlsruhe.turniere.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegisterForm {
    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 12)
    private String password;

    @NotEmpty
    @Size(min = 12)
    private String repassword;
}
