package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public void setName(Team team, String name) {
        team.setName(name);
        teamRepository.save(team);
    }
}
