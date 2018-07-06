package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.StageRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final StageRepository stageRepository;
    private final StageService stageService;
    private final TournamentRepository tournamentRepository;

    /**
     * Set the score for given match to given scores
     *
     * @param match      The match to change the scores
     * @param scoreTeam1 Score of Team 1
     * @param scoreTeam2 Score of Team 2
     */
    private void setScore(Match match, int scoreTeam1, int scoreTeam2) {
        match.setScoreTeam1(scoreTeam1);
        match.setScoreTeam2(scoreTeam2);
    }

    /**
     * Set a Result Score (Winner is automatically moved to next Stage afterwards) for given match
     *
     * @param match      The match to change the scores
     * @param scoreTeam1 Score of Team 1
     * @param scoreTeam2 Score of Team 2
     */
    public void setResults(Match match, Integer scoreTeam1, Integer scoreTeam2) {
        // set scores
        setScore(match, scoreTeam1, scoreTeam2);
        // set state
        match.setState(evaluateWinner(match.getScoreTeam1(), match.getScoreTeam2()));
        // find next stage
        populateStageBelow(tournamentRepository.findByMatch(match), match);
        // save match
        matchRepository.save(match);
    }

    /**
     * Populate Stage below given Match from given Tournament with given Match
     *
     * @param tournament Tournament to populate Match within
     * @param match      Match to populate in given Tournament
     */
    public void populateStageBelow(Tournament tournament, Match match) {
        // find next stage based on current one
        Stage stage = stageRepository.findByMatchesContains(match);
        Optional<Stage> nextStageOptional = stageService.findNextStage(tournament, stage);
        // determine winning team to move it into the next stage
        Team winningTeam = null;
        switch (match.getState()) {
            case TEAM1_WON:
                winningTeam = match.getTeam1();
                break;
            case TEAM2_WON:
                winningTeam = match.getTeam2();
                break;
        }
        if (nextStageOptional.isPresent()) {
            Stage nextStage = nextStageOptional.get();
            // populate next stage with winning teams
            if (match.getState() == Match.State.TEAM1_WON) {
                populateMatchBelow(nextStage, match, winningTeam);
            } else if (match.getState() == Match.State.TEAM2_WON) {
                populateMatchBelow(nextStage, match, winningTeam);
            }
        } else {
            // ensure winning team is already set
            if (winningTeam != null) {
                // final match => set tournament winner
                tournament.setWinner(winningTeam);
                tournamentRepository.save(tournament);
            }
        }
    }

    /**
     * Set a Live Score (Winner is NOT automatically moved to next Stage afterwards) for given match
     *
     * @param match      The match to change the scores
     * @param scoreTeam1 Score of Team 1
     * @param scoreTeam2 Score of Team 2
     */
    public void setLivescore(Match match, int scoreTeam1, int scoreTeam2) {
        setScore(match, scoreTeam1, scoreTeam2);
        match.setState(Match.State.IN_PROGRESS);
        matchRepository.save(match);
    }


    /**
     * Evaluate the Winner of two given scores
     *
     * @param scoreTeam1 Score of Team 1
     * @param scoreTeam2 Score of Team 2
     * @return Winner of the Game as State
     */
    private Match.State evaluateWinner(Integer scoreTeam1, Integer scoreTeam2) {
        if (scoreTeam1 == null) {
            return Match.State.TEAM1_WON;
        }
        if (scoreTeam1 < scoreTeam2) {
            return Match.State.TEAM2_WON;
        } else if (scoreTeam2 < scoreTeam1) {
            return Match.State.TEAM1_WON;
        } else {
            return Match.State.IN_PROGRESS;
        }
    }


    /**
     * Populate Stage below given Stage with given team in the right match (depending on given matches position)
     *
     * @param stage Stage to put team in below
     */
    public void populateMatchBelow(Stage stage, Match match, Team team) {
        Match nextMatch = null;
        if ((match.getPosition() & 1) == 0) {
            //even
            nextMatch = stage.getMatches().get(match.getPosition() / 2);
            nextMatch.setTeam1(team);
        } else {
            //odd
            nextMatch = stage.getMatches().get((match.getPosition() - 1) / 2);
            nextMatch.setTeam2(team);
        }
        matchRepository.save(nextMatch);
    }
}
