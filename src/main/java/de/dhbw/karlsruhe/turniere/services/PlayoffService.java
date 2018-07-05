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
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayoffService {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;
    private final MatchService matchService;

    /**
     * Generate Playoff Stages for given Tournament with given Teams and save them to given owner
     *
     * @param owner      Account to which the tournament is attached
     * @param teams      List of Teams to generate Playoffs with
     * @param tournament Tournament to which Playoff Stages are added
     */
    public void generatePlayoffs(User owner, List<Team> teams, Tournament tournament) {
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
    }

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

        // prevent error with only one team
        if (teams.size() == 1) {
            List<Match> matches = new ArrayList<>();
            matches.add(new Match(teams.get(0), teams.get(0), 0, 0, Match.State.TEAM1_WON, 0));
            return Pair.of(matches, 0);
        }

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
}
