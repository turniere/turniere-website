package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.UserService;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.forms.RegisterForm;
import de.dhbw.karlsruhe.turniere.forms.validators.RegisterFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    private final RegisterFormValidator registerFormValidator;

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
        // doYouKnoWThEWaY and register user object
        userService.register(new User(registerForm.getUsername(), registerForm.getEmail(), registerForm
                .getPassword()));
        // login newly registered user
        try {
            httpServletRequest.login(registerForm.getEmail(), registerForm.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        // redirect to index
        return "redirect:/";
    }
}
