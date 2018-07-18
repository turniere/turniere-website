package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;

public interface CustomTournamentRepository {
    Tournament findByMatch(Match match);
}
