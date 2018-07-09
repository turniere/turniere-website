package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TournamentRepository extends CrudRepository<Tournament, Long>, CustomTournamentRepository {
    Tournament findByCode(String code);

    List<Tournament> findByIsPublic(Boolean isPublic);

    Tournament findByStagesContains(Stage stage);

    Tournament findByGroupStage(GroupStage groupStage);
}