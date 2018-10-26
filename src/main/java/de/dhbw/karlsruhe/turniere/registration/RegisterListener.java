package de.dhbw.karlsruhe.turniere.registration;

import de.dhbw.karlsruhe.turniere.authentication.UserService;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.services.MailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RegisterListener implements ApplicationListener<OnRegisterEvent> {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Async
    @Override
    public void onApplicationEvent(OnRegisterEvent onRegisterEvent) {
        User user = onRegisterEvent.getUser();
        String verificationToken = UUID.randomUUID().toString();
        VerificationToken verificationTokenObj = userService.attachVerificationToken(user, verificationToken);
        mailService.sendRegistrationMail(verificationTokenObj);
    }
}
