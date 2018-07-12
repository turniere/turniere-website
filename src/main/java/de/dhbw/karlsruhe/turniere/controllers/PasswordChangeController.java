package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.UserService;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.forms.ChangePasswordForm;
import de.dhbw.karlsruhe.turniere.forms.validators.ChangePasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PasswordChangeController {
    private final UserService userService;
    private final ChangePasswordFormValidator changePasswordFormValidator;

    @GetMapping("/passwortaendern")
    String changePassword(ChangePasswordForm changePasswordForm) {
        return "change_password";
    }

    @PostMapping("/passwortaendern")
    String postChangePassword(@Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult, Authentication authentication) {
        changePasswordFormValidator.validate(changePasswordForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "change_password";
        }
        User user = User.fromAuthentication(authentication);
        userService.changePassword(user, changePasswordForm.getPassword());
        return "redirect:/";
    }
}
