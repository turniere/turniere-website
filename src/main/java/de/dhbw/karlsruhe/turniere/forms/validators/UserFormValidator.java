package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

public class UserFormValidator {
    @Autowired
    UserRepository userRepository;

    public void rejectDuplicateEmail(String email, Errors errors) {
        if (userRepository.findByEmail(email) != null) {
            errors.rejectValue("email", "duplicate");
        }
    }

    public void rejectDuplicateUsername(String username, Errors errors) {
        if (userRepository.findByUsername(username) != null) {
            errors.rejectValue("username", "duplicate");
        }
    }

    public void rejectWrongRepassword(String password, String repassword, Errors errors) {
        if (!password.equals(repassword)) {
            errors.rejectValue("repassword", "doesntMatch");
        }
    }
}
