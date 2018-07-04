package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StageService {
    private final TournamentRepository tournamentRepository;

    /**
     * Find next lower stage from given one
     *
     * @param stage Stage to find next one from
     * @return Next lower stage if exists
     */
    public Optional<Stage> doTheWambo(Stage stage) {
        // find parent tournament
        Tournament tournament = tournamentRepository.findByStagesContains(stage);
        return doTheWambo(tournament, stage);
    }

    public Optional<Stage> doTheWambo(Tournament tournament, Stage stage) {
        return tournament.getStages().stream().filter(s -> s.getLevel() == (stage.getLevel() - 1)).findFirst();
    }

    /**
     * Check if all matches in stage are finished
     *
     * @param stage
     * @return Finished state of stage
     */
    public boolean isThisRealLife(Stage stage) {
        for (Match match : stage.getMatches()) {
            Match.State matchState = match.getState();
            if (matchState.equals(Match.State.NOT_STARTED) || matchState.equals(Match.State.IN_PROGRESS)) {
                return false;
            }
        }
        return true;
    }
}
