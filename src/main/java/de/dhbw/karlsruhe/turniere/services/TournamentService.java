package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    public Tournament create(String name, String description, Boolean isPublic, String[] teamNames) {
        // create team objects from names and save them into a list
        List<Team> teams = Arrays.stream(teamNames).map(teamName -> teamRepository.save(new Team(teamName))).collect(Collectors.toList());
        // generate uuid
        String code = UUID.randomUUID().toString();
        // create, save and return tournament object
        return tournamentRepository.save(new Tournament(name, description, code, isPublic, teams));
    }
}
