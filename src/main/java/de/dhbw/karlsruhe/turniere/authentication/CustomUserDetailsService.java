package de.dhbw.karlsruhe.turniere.authentication;

import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        User userByUsername = userRepository.findByUsernameIgnoreCase(s);
        User userByEmail = userRepository.findByEmailIgnoreCase(s);
        if (userByUsername == null) {
            if (userByEmail == null) {
                throw new UsernameNotFoundException(s);
            } else {
                user = userByEmail;
            }
        } else {
            user = userByUsername;
        }
        return new CustomUserDetails(user);
    }
}
