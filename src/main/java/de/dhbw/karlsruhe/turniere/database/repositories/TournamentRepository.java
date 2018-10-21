package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends CrudRepository<Tournament, Long>, CustomTournamentRepository {
    Optional<Tournament> findByCode(String code);

    List<Tournament> findByIsPublic(Boolean isPublic);

    Tournament findByStagesContains(Stage stage);

    Tournament findByTeamsContains(Team team);

    Tournament findByGroupStage(GroupStage groupStage);

    List<Tournament> findByOwner(User owner);

    List<Tournament> findByOwnerNotAndIsPublic(User owner, boolean isPublic);
}