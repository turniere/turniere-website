package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.forms.ChangePasswordForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordFormValidator extends UserFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(ChangePasswordForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ChangePasswordForm changePasswordForm = (ChangePasswordForm) o;
        rejectWrongRepassword(changePasswordForm.getPassword(), changePasswordForm.getRepassword(), errors);
    }
}
