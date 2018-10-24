package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
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
     * @param teams      List of Teams to generate Playoffs with
     * @param tournament Tournament to which Playoff Stages are added
     */
    public void generatePlayoffs(List<Team> teams, Tournament tournament) {
        int stageCount = calculateRequiredStageCount(teams.size());
        // generate initial matches and save remaining teams
        Pair<List<Match>, Integer> matchesAndRemainingTeams = generateMatches(teams);
        List<Match> matches = matchesAndRemainingTeams.getFirst();
        Integer remainingTeams = matchesAndRemainingTeams.getSecond();
        // put initial matches into first stage
        Stage stage = new Stage(stageCount, matches);
        // save and add to tournament
        tournament.addStage(stageRepository.save(stage));
        tournament.setCurrentStage(Integer.toString(stageCount));
        //
        List<Match> savedMatches = tournament.getStages().get(0).getMatches();
        // add remaining stages
        addStagesWithEmptyMatches(tournament, stageCount);
        //move teams without competition to next stage
        while (remainingTeams >= 0) {
            matchService.populateStageBelow(tournament, savedMatches.get(savedMatches.size() - 1 - remainingTeams));
            remainingTeams--;
        }
    }

    /**
     * Add given number of Stages with empty Matches to Tournament
     *
     * @param tournament Tournament to add Stages to
     * @param stageCount Number of Stages to add
     */
    private void addStagesWithEmptyMatches(Tournament tournament, int stageCount) {
        List<Stage> stages = generateStagesWithEmptyMatches(stageCount);
        for (Stage stage : stages) {
            // save and add to tournament
            tournament.addStage(stageRepository.save(stage));
        }
    }

    /**
     * Generates given number of Stages with right number of empty matches
     *
     * @param stageCount Number of stages to be generated
     * @return List of Stages
     */
    private List<Stage> generateStagesWithEmptyMatches(int stageCount) {
        List<Stage> stages = new ArrayList<>();
        for (int i = (stageCount - 1); i >= 0; i--) {
            // fill with calculated number of empty matches
            Stage stage = new Stage(i, generateEmptyMatches((int) Math.pow(2, i)));
            stages.add(stage);
        }
        return stages;
    }

    /**
     * Calculate next power of two from given number
     *
     * @param number Lower bound for calculated power of two
     * @return Next power of two after given number
     */
    public int nextPowerOfTwo(int number) {
        Integer nextPower = 0;
        for (int i = 0; nextPower < number; i++) {
            nextPower = (int) Math.pow(2, i);
        }
        return nextPower;
    }

    /**
     * Calculate previous power of two from given number
     *
     * @param number Upper bound for calculated power of two
     * @return Last power of two before given number
     */
    public int previousPowerOfTwo(int number) {
        return nextPowerOfTwo(number) / 2;
    }

    /**
     * Generate empty matches by filling them with a (saved) dummy team
     *
     * @param numberOfMatches Number of empty matches to generate
     * @return List of generated empty matches
     */
    private List<Match> generateEmptyMatches(int numberOfMatches) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            matches.add(new Match(null, null, null, null, Match.State.NOT_STARTED, i, false));
        }
        return matches;
    }

    /**
     * Generate matches for given list of teams
     *
     * @param originalTeams Teams to generate matches for
     * @return Pair of list of generated matches and teams not represented in generated matches
     */
    private Pair<List<Match>, Integer> generateMatches(List<Team> originalTeams) {
        // copy original teams to new variable to not modify original list
        List<Team> teams = new ArrayList<>(originalTeams);

        // prevent error with only one team
        if (teams.size() == 1) {
            List<Match> matches = new ArrayList<>();
            matches.add(new Match(teams.get(0), teams.get(0), 0, 0, Match.State.TEAM1_WON, 0, false));
            return Pair.of(matches, 0);
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
            matches.add(new Match(teams.get(0), teams.get(1), null, null, Match.State.NOT_STARTED, i, false));
            teams.remove(1);
            teams.remove(0);
            lastPos = i;
        }
        lastPos++;
        Integer halfEmptyGames = 0;
        while (teams.size() != 0) {
            matches.add(new Match(teams.get(0), null, null, null, Match.State.SINGLE_TEAM, lastPos, false));
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
            return (int) (Math.log(previousPowerOfTwo(numberOfTeams)) / Math.log(2));
        }
    }

    public List<Team> sortByMatchups(List<Team> teams, int groupCount) {
        int teamsSize = teams.size();
        List<Team> playoffTeams = new ArrayList<>();
        if (teamsSize % 2 == 0 && groupCount % 2 == 0) {
            for (int i = 0; i < teamsSize / 2; i++) {
                playoffTeams.add(teams.get(i));
                playoffTeams.add(teams.get(teamsSize - i - 1));
            }
        } else {
            playoffTeams = new ArrayList<>(teams);
            for (int i = 0; i < 5; i++) {
                Collections.shuffle(playoffTeams);
            }
        }
        return playoffTeams;
    }
}
