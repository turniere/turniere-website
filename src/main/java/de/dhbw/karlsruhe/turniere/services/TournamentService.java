package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public Match nextMatch(Tournament tournament) {
        List<Team> teams = tournament.getTeams();
        // return plain test match
        return new Match(teams.get(0), teams.get(1), null, null, Match.State.NOT_STARTED);
    }

    private int nextPowerOf2(int number){
        Integer nextPower = 0;
        for (int i = 0; nextPower<number;i++){
            nextPower = (int)Math.pow(2,i);
        }
        return nextPower;
    }

    private List<Match> generateMatches(Tournament tournament, boolean randomize) {
        List<Team> teams = tournament.getTeams();
        if (randomize){
            Collections.shuffle(teams);
        }
        //needed Games --> how many teams need to be kicked out to get the number of teams to the next lower power of 2
        int neededGames;
        if (nextPowerOf2(teams.size()) == teams.size()) {
            neededGames = teams.size();
        }else{
            neededGames = teams.size()-nextPowerOf2(teams.size())/2;
        }
        List<Match> matches = new ArrayList<>();
        //generate neededGames number of matches and add them to the list matches
        for (int i = 0; i<neededGames; i++) {
            matches.add(new Match(teams.get(0), teams.get(1), null, null, Match.State.NOT_STARTED));
            teams.remove(0);
            teams.remove(1);
        }
        return matches;

    }

    public Tournament create(String name, String description, Boolean isPublic, String[] teamNames, User owner) {
        // create team objects from names and save them into a list
        List<Team> teams = Arrays.stream(teamNames).map(teamName -> teamRepository.save(new Team(teamName))).collect
                (Collectors.toList());
        // generate uuid
        String code = UUID.randomUUID().toString();
        // create and save tournament object
        Tournament tournament = tournamentRepository.save(new Tournament(name, code, description, isPublic, teams));
        //tournament.getMatches().add(nextMatch(tournament)); // << deprecated Jonas Code
        List<Match> matches = generateMatches(tournament,true);
        for (Match match:matches){
            tournament.getMatches().add(match);
        }
        tournamentRepository.save(tournament);
        // add saved tournament object to authenticated user (owner)
        owner.getTournaments().add(tournament);
        // save updated user in repository
        userRepository.save(owner);
        return tournament;
    }
}
