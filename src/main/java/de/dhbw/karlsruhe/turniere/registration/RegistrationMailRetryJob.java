package de.dhbw.karlsruhe.turniere.registration;

import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.database.repositories.VerificationTokenRepository;
import de.dhbw.karlsruhe.turniere.services.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationMailRetryJob {
    private final Logger logger = LoggerFactory.getLogger(RegistrationMailRetryJob.class);
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Scheduled(cron = "* 0,10,20,30,40,50 * * * *")
    public void retryUnsentMails() {
        List<VerificationToken> unsentTokens = verificationTokenRepository.findBySentIsFalse();
        for (VerificationToken unsentToken : unsentTokens) {
            logger.info(String.format("Trying to resend %s", unsentToken));
            mailService.sendRegistrationMail(unsentToken);
        }
    }

}
