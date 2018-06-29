package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import org.springframework.data.repository.CrudRepository;

public interface StageRepository extends CrudRepository<Stage, Long> {
    Stage findByMatchesContains(Match match);
}
