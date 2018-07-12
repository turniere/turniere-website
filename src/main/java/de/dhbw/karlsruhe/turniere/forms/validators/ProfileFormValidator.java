package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.forms.ProfileForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ProfileFormValidator extends UserFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(ProfileForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ProfileForm registerForm = (ProfileForm) o;
        if (!registerForm.getEmail().equals("")) {
            rejectDuplicateEmail(registerForm.getEmail(), errors);
        }
        if (!registerForm.getUsername().equals("")) {
            rejectDuplicateUsername(registerForm.getUsername(), errors);
            rejectUsernameIsValidEmail(registerForm.getUsername(), errors);
        }
    }
}