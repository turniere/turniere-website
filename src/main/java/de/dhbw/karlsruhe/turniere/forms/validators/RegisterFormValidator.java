package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import de.dhbw.karlsruhe.turniere.forms.RegisterForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class RegisterFormValidator implements Validator {
    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(RegisterForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegisterForm user = (RegisterForm) o;
        if (userRepository.findByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "duplicate");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "duplicate");
        }
        if (!user.getPassword().equals(user.getRepassword())) {
            errors.rejectValue("repassword", "doesntMatch");
        }
    }
}
