package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.StageRepository;
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
    private final StageRepository stageRepository;

    /**
     * Calculate next power of two
     *
     * @param number Lower bound for calculated power of two
     * @return Next power of two after given number
     */
    private int nextPowerOf2(int number) {
        Integer nextPower = 0;
        for (int i = 0; nextPower < number; i++) {
            nextPower = (int) Math.pow(2, i);
        }
        return nextPower;
    }

    /**
     * Generate empty matches by filling them with a (saved) dummy team
     *
     * @param numberOfMatches Number of empty matches to generate
     * @return List of generated empty matches
     */
    private List<Match> generateEmptyMatches(int numberOfMatches) {
        Team dummy = new Team();
        teamRepository.save(dummy);
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            matches.add(new Match(dummy, dummy, null, null, Match.State.NOT_STARTED, i));
        }
        return matches;
    }

    /**
     * Generate matches for given list of teams
     *
     * @param originalTeams Teams to generate matches for
     * @param randomize     Randomize teams before arranging them into matches
     * @return List of generated matches including all given teams
     */
    private List<Match> generateMatches(List<Team> originalTeams, boolean randomize) {
        // copy original teams to new variable to not modify original list
        List<Team> teams = new ArrayList<>(originalTeams);
        // shuffle teams if desired
        if (randomize) {
            Collections.shuffle(teams);
        }
        // needed Games --> how many teams need to be kicked out to get the number of teams to the next lower power of 2
        int neededGames;
        if (nextPowerOf2(teams.size()) == teams.size()) {
            neededGames = teams.size() / 2;
        } else {
            neededGames = teams.size() - nextPowerOf2(teams.size()) / 2;
        }
        List<Match> matches = new ArrayList<>();
        // generate neededGames number of matches and add them to the list matches
        for (int i = 0; i < neededGames; i++) {
            matches.add(new Match(teams.get(0), teams.get(1), null, null, Match.State.NOT_STARTED, i));
            teams.remove(1);
            teams.remove(0);
        }
        return matches;
    }

    /**
     * Calculate number of stages
     *
     * @param numberOfTeams Number of teams to calculate stages for
     * @return Required number of stages for given number of teams
     */
    private Integer getStageId(Integer numberOfTeams) {
        if (numberOfTeams == 0 || numberOfTeams == 1) {
            return 0;
        } else {
            return (int) (Math.log(nextPowerOf2(numberOfTeams) / 2) / Math.log(2));
        }
    }

    /**
     * Create a new Tournament
     *
     * @param name        Tournament name
     * @param description Tournament description
     * @param isPublic    Tournament accessible without login (deprecated)
     * @param teamNames   Names of initial teams
     * @param owner       Owner of the tournament
     * @return Saved new tournament object
     */
    public Tournament create(String name, String description, Boolean isPublic, String[] teamNames, User owner) {
        // create team objects from names and save them into a list
        List<Team> teams = Arrays.stream(teamNames).map(teamName -> teamRepository.save(new Team(teamName))).collect
                (Collectors.toList());
        // generate uuid
        String code = UUID.randomUUID().toString();
        // create and save tournament object
        Tournament tournament = new Tournament(name, code, description, isPublic, teams);
        int stageId = getStageId(teams.size());
        // generate initial matches
        List<Match> matches = generateMatches(tournament.getTeams(), true);
        //generating "last" stage with actual matches
        Stage stage = new Stage(stageId, matches);
        tournament.addStage(stage);
        stageRepository.save(stage);
        for (int i = (stageId - 1); i >= 0; i--) {
            //generate all other stages with the right number of empty matches
            Stage emptyStage = new Stage(i, generateEmptyMatches((int) Math.pow(2, i)));
            tournament.addStage(stageRepository.save(emptyStage));
        }

        // save tournament object
        tournament = tournamentRepository.save(tournament);
        // add saved tournament object to authenticated user (owner)
        owner.getTournaments().add(tournament);
        // save updated user in repository
        userRepository.save(owner);
        return tournament;
    }

}
