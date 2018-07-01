package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final StageRepository stageRepository;
    private final StageService stageService;

    public void setResults(Match match, int scoreTeam1, int scoreTeam2) {
        // set scores
        match.setScoreTeam1(scoreTeam1);
        match.setScoreTeam2(scoreTeam2);
        // set state
        match.setState(evaluateWinner(match.getScoreTeam1(), match.getScoreTeam2()));
        // find next stage
        Stage stage = stageRepository.findByMatchesContains(match);
        Optional<Stage> nextStageOptional = stageService.findNextStage(stage);
        if (nextStageOptional.isPresent()) {
            Stage nextStage = nextStageOptional.get();
            // populate next stage with winning teams
            if (match.getState() == Match.State.TEAM1_WON) {
                populateMatchBelow(nextStage, match, match.getTeam1());
            } else if (match.getState() == Match.State.TEAM2_WON) {
                populateMatchBelow(nextStage, match, match.getTeam2());
            }
        }
        // save match
        matchRepository.save(match);
    }

    public void setLivescore(Match match, int scoreTeam1, int scoreTeam2) {
        match.setScoreTeam1(scoreTeam1);
        match.setScoreTeam2(scoreTeam2);
        match.setState(Match.State.IN_PROGRESS);
        matchRepository.save(match);
    }

    public void whoWon(Match match) {
        match.setState(evaluateWinner(match.getScoreTeam1(), match.getScoreTeam2()));
        matchRepository.save(match);
    }

    private Match.State evaluateWinner(int scoreTeam1, int scoreTeam2) {
        if (scoreTeam1 < scoreTeam2) {
            return Match.State.TEAM2_WON;
        } else if (scoreTeam2 < scoreTeam1) {
            return Match.State.TEAM1_WON;
        } else {
            return Match.State.IN_PROGRESS;
        }
    }

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
