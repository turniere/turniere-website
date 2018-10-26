package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.database.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final Environment environment;
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository verificationTokenRepository;

    private String mailText;
    private String mailFrom;
    private String mailSubject;

    @PostConstruct
    private void init() {
        mailText = readMailText(environment.getProperty("spring.mail.textPath", "mail.txt"));
        mailFrom = Objects.requireNonNull(environment.getProperty("spring.mail.from"));
        mailSubject = Objects.requireNonNull(environment.getProperty("spring.mail.subject"));
    }

    private String readMailText(String filepath) {
        File file = new File(filepath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String fileText = br.lines().collect(Collectors.joining("\n"));
            return fileText;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegistrationMail(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        String recipient = user.getEmail();
        String token = verificationToken.getToken();
        String url = environment.getProperty("spring.root_url") + "/confirm_registration?token=" + token;
        String debugMessage = String.format("mail to %s (token: %s)", recipient, token);
        logger.info("Sending activation " + debugMessage);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setFrom(mailFrom);
        email.setSubject(mailSubject);
        email.setText(String.format(mailText, url));
        javaMailSender.send(email);
        logger.info("Success fully sent " + debugMessage);
        verificationToken.setSent(true);
        verificationTokenRepository.save(verificationToken);
    }
}
