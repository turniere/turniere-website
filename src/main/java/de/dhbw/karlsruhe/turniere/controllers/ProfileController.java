package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import de.dhbw.karlsruhe.turniere.forms.ProfileForm;
import de.dhbw.karlsruhe.turniere.forms.validators.ProfileFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileFormValidator profileFormValidator;
    private final UserRepository userRepository;

    @GetMapping("/profil")
    String profile(ProfileForm profileForm, Model model, Authentication authentication) {
        model.addAttribute("user", User.fromAuthentication(authentication));
        return "profile";
    }

    @PostMapping("/profil")
    String postProfile(@Valid ProfileForm profileForm, BindingResult bindingResult, Model model, Authentication authentication) {
        User user = User.fromAuthentication(authentication);
        model.addAttribute("user", user);
        // validate profile form
        profileFormValidator.validate(profileForm, user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "profile";
        }
        // set new attributes if set in form
        String formEmail = profileForm.getEmail();
        String formUsername = profileForm.getUsername();
        if (!formEmail.equals("") && !formEmail.equals(user.getEmail())) {
            user.setEmail(formEmail);
        }
        if (!formUsername.equals("") && !formUsername.equals(user.getUsername())) {
            user.setUsername(formUsername);
        }
        // save changed user
        userRepository.save(user);
        // redirect to index
        return "redirect:/";
    }
}
