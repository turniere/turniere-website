package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.UserService;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.VerificationTokenRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.exceptions.VerificationTokenExpiredException;
import de.dhbw.karlsruhe.turniere.forms.RegisterForm;
import de.dhbw.karlsruhe.turniere.forms.validators.RegisterFormValidator;
import de.dhbw.karlsruhe.turniere.registration.OnRegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    private final RegisterFormValidator registerFormValidator;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    String register(RegisterForm registerForm) {
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    String postRegister(@Valid RegisterForm registerForm, BindingResult bindingResult, HttpServletRequest
            httpServletRequest) {
        // valid form
        registerFormValidator.validate(registerForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }
        // create and register user object
        User user = new User(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword());
        userService.register(user);
        // fire register event
        applicationEventPublisher.publishEvent(new OnRegisterEvent(user, httpServletRequest.getRequestURI()));
        // redirect to registration page
        return "redirect:/registered";
    }

    @RequestMapping(value = "/confirm_registration")
    String confirmRegistration(@RequestParam("token") String token, HttpServletRequest httpServletRequest) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
        verificationTokenOptional.orElseThrow(() -> new ResourceNotFoundException(String.format("Token '%s' doesn't exist", token)));
        VerificationToken verificationToken = verificationTokenOptional.get();
        if (verificationToken.isExpired()) {
            throw new VerificationTokenExpiredException(token);
        }
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
        return "redirect:/login";
    }

    @RequestMapping(value = "/registered")
    String registered(Model model, RegisterForm registerForm) {
        model.addAttribute("registered", true);
        return "login";
    }
}
