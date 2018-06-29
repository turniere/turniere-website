package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import org.springframework.beans.factory.annotation.Autowired;

public class TournamentRepositoryImpl implements CustomTournamentRepository {
    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    StageRepository stageRepository;

    @Autowired
    UserRepository userRepository;

    public Tournament findByMatch(Match match) {
        // find stage
        Stage stage = stageRepository.findByMatchesContains(match);
        // find corresponding tournament
        return tournamentRepository.findByStagesContains(stage);
    }

    public User findOwner(Tournament tournament) {
        return userRepository.findByTournamentsContains(tournament);
    }
}
