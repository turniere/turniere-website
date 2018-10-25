package de.dhbw.karlsruhe.turniere.registration;

import de.dhbw.karlsruhe.turniere.authentication.UserService;
import de.dhbw.karlsruhe.turniere.database.models.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RegisterListener implements ApplicationListener<OnRegisterEvent> {
    private JavaMailSender javaMailSender;
    private Environment environment;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(OnRegisterEvent onRegisterEvent) {
        User user = onRegisterEvent.getUser();
        String verificationToken = UUID.randomUUID().toString();
        userService.attachVerificationToken(user, verificationToken);

        String recipient = user.getEmail();
        String url = environment.getProperty("spring.root_url") + "/confirm_registration?token=" + verificationToken;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setFrom(environment.getProperty("spring.mail.from"));
        email.setSubject("Turnie.re registration");
        email.setText(url);
        javaMailSender.send(email);
    }
}
