package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
