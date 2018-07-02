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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;
    private final MatchService matchService;

    /**
     * Calculate next power of two from given number
     *
     * @param number Lower bound for calculated power of two
     * @return Next power of two after given number
     */
    private int nextPowerOfTwo(int number) {
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
     * @return Pair of list of generated matches and teams not represented in generated matches
     */
    private Pair<List<Match>, Integer> generateMatches(List<Team> originalTeams, boolean randomize) {
        // copy original teams to new variable to not modify original list
        List<Team> teams = new ArrayList<>(originalTeams);
        // shuffle teams if desired
        if (randomize) {
            Collections.shuffle(teams);
        }
        // needed Games --> how many teams need to be kicked out to get the number of teams to the next lower power of 2
        int neededGames;
        if (nextPowerOfTwo(teams.size()) == teams.size()) {
            neededGames = teams.size() / 2;
        } else {
            neededGames = teams.size() - nextPowerOfTwo(teams.size()) / 2;
        }
        List<Match> matches = new ArrayList<>();
        // generate neededGames number of matches and add them to the list matches
        Integer lastPos = 0;
        for (int i = 0; i < neededGames; i++) {
            matches.add(new Match(teams.get(0), teams.get(1), null, null, Match.State.NOT_STARTED, i));
            teams.remove(1);
            teams.remove(0);
            lastPos = i;
        }
        lastPos++;
        Integer halfEmptyGames = 0;
        while (teams.size() != 0) {
            matches.add(new Match(teams.get(0), teams.get(0), 1, 0, Match.State.TEAM1_WON, lastPos));
            teams.remove(0);
            lastPos++;
            halfEmptyGames++;
        }
        return Pair.of(matches, halfEmptyGames);
    }

    /**
     * Calculate number of stages
     *
     * @param numberOfTeams Number of teams to calculate stages for
     * @return Required number of stages for given number of teams
     */
    private Integer calculateRequiredStageCount(Integer numberOfTeams) {
        if (numberOfTeams == 0 || numberOfTeams == 1) {
            return 0;
        } else {
            return (int) (Math.log(nextPowerOfTwo(numberOfTeams) / 2) / Math.log(2));
        }
    }

    /**
     * Generate tournament code
     *
     * @param length Length of generated code
     * @return Generated tournament code
     */
    private String generateCode(int length) {
        char[] codeChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        // generate remaining characters
        for (int i = 0; i < length; i++) {
            stringBuilder.append(codeChars[random.nextInt(codeChars.length)]);
        }
        return stringBuilder.toString();
    }

    /**
     * Generate unique tournament code
     * <p>
     * Uniqueness is ensured by checking for existence of the code in the database.
     *
     * @param length Length of generated code
     * @return Generated tournament code
     */
    private String generateUniqueCode(int length) {
        String code = generateCode(length);
        while (tournamentRepository.findByCode(code) != null) {
            code = generateCode(length);
        }
        return code;
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
        String code = generateUniqueCode(5);
        // create and save tournament object
        Tournament tournament = new Tournament(name, code, description, isPublic, teams);
        int stageCount = calculateRequiredStageCount(teams.size());
        // generate initial matches and save remaining teams
        Pair<List<Match>, Integer> matchesAndRemainingTeams = generateMatches(tournament.getTeams(), true);
        List<Match> matches = matchesAndRemainingTeams.getFirst();
        Integer remainingTeams = matchesAndRemainingTeams.getSecond();
        // put initial matches into first stage
        Stage stage = new Stage(stageCount, matches);
        // save and add to tournament
        tournament.addStage(stageRepository.save(stage));
        //
        List<Match> savedMatches = tournament.getStages().get(0).getMatches();
        // add remaining stages
        for (int i = (stageCount - 1); i >= 0; i--) {
            // fill with calculated number of empty matches
            Stage emptyStage = new Stage(i, generateEmptyMatches((int) Math.pow(2, i)));
            // save and add to tournament
            tournament.addStage(stageRepository.save(emptyStage));
        }
        //move teams without competition to next stage
        while (remainingTeams >= 0) {
            matchService.populateStageBelow(tournament, savedMatches.get(savedMatches.size() - 1 - remainingTeams));
            remainingTeams--;
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
