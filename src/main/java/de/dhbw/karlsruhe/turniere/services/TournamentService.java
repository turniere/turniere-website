package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
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
    private final UserRepository userRepository;

    public Tournament create(String name, String description, Boolean isPublic, String[] teamNames, User owner) {
        // create team objects from names and save them into a list
        List<Team> teams = Arrays.stream(teamNames).map(teamName -> teamRepository.save(new Team(teamName))).collect
                (Collectors.toList());
        // generate uuid
        String code = UUID.randomUUID().toString();
        // create and save tournament object
        Tournament tournament = tournamentRepository.save(new Tournament(name, code, description, isPublic, teams));
        // add saved tournament object to authenticated user (owner)
        owner.getTournaments().add(tournament);
        // save updated user in repository
        userRepository.save(owner);
        return tournament;
    }
}
