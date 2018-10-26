package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.database.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final Environment environment;
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository verificationTokenRepository;

    public void sendRegistrationMail(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        String recipient = user.getEmail();
        String token = verificationToken.getToken();
        String url = environment.getProperty("spring.root_url") + "/confirm_registration?token=" + token;
        String debugMessage = String.format("%s (token: %s)", recipient, token);
        logger.info("Sending activation mail to " + debugMessage);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setFrom(environment.getProperty("spring.mail.from"));
        email.setSubject("Turnie.re registration");
        email.setText(url);
        javaMailSender.send(email);
        logger.info("Success sending " + debugMessage);
        verificationToken.setSent(true);
        verificationTokenRepository.save(verificationToken);
    }
}
