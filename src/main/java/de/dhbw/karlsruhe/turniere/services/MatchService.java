package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;

    public void setResults(Match match, int scoreTeam1, int scoreTeam2) {
        match.setScoreTeam1(scoreTeam1);
        match.setScoreTeam2(scoreTeam2);
        match.setState(evaluateWinner(match.getScoreTeam1(), match.getScoreTeam2()));
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
}
