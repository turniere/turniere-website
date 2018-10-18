package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

public class UserFormValidator {
    @Autowired
    UserRepository userRepository;

    public void rejectDuplicateEmail(String email, Errors errors) {
        if (userRepository.findByEmailIgnoreCase(email.toLowerCase()) != null) {
            errors.rejectValue("email", "duplicate");
        }
    }

    public void rejectDuplicateUsername(String username, Errors errors) {
        if (userRepository.findByUsernameIgnoreCase(username.toLowerCase()) != null) {
            errors.rejectValue("username", "duplicate");
        }
    }

    public void rejectWrongRepassword(String password, String repassword, Errors errors) {
        if (!password.equals(repassword)) {
            errors.rejectValue("repassword", "doesntMatch");
        }
    }

    public void rejectUsernameIsValidEmail(String username, Errors errors) {
        EmailValidator emailValidator = new EmailValidator();
        if (emailValidator.isValid(username, null)) {
            errors.rejectValue("username", "validEmail");
        }
    }
}
