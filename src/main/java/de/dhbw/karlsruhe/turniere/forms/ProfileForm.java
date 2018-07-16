package de.dhbw.karlsruhe.turniere.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProfileForm {
    @NotEmpty
    @Size(max = 255)
    private String username;

    @NotEmpty
    @Email
    @Size(max = 255)
    private String email;
}
