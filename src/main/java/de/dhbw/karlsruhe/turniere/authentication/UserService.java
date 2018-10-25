package de.dhbw.karlsruhe.turniere.authentication;

import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;


    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void attachVerificationToken(User user, String verificationToken) {
        VerificationToken verificationTokenObj = new VerificationToken(user, verificationToken);
        verificationTokenRepository.save(verificationTokenObj);
    }
}
