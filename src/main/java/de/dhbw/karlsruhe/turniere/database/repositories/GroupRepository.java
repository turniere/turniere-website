package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.Group;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Group findByMatchesContains(Match match);

    Group findByTeamsContains(Team team);

}
