package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.forms.RegisterForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegisterFormValidator extends UserFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(RegisterForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegisterForm registerForm = (RegisterForm) o;
        rejectDuplicateEmail(registerForm.getEmail(), errors);
        rejectDuplicateUsername(registerForm.getUsername(), errors);
        rejectWrongRepassword(registerForm.getPassword(), registerForm.getRepassword(), errors);
    }
}
