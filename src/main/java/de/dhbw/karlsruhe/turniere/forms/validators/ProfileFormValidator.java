package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.database.models.User;
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
    }

    public void validate(ProfileForm profileForm, User user, Errors errors) {
        String formEmail = profileForm.getEmail();
        String formUsername = profileForm.getUsername();
        if (!formEmail.equals("") && !formEmail.equals(user.getEmail())) {
            rejectDuplicateEmail(profileForm.getEmail(), errors);
        }
        if (!formUsername.equals("") && !formUsername.equals(user.getUsername())) {
            rejectDuplicateUsername(profileForm.getUsername(), errors);
            rejectUsernameIsValidEmail(profileForm.getUsername(), errors);
        }
    }
}