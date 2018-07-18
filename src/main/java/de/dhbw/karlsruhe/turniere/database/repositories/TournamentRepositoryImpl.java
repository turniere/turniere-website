package de.dhbw.karlsruhe.turniere.database.repositories;

import de.dhbw.karlsruhe.turniere.database.models.Group;
import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import org.springframework.beans.factory.annotation.Autowired;

public class TournamentRepositoryImpl implements CustomTournamentRepository {
    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    StageRepository stageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupStageRepository groupStageRepository;

    @Autowired
    GroupRepository groupRepository;

    public Tournament findByMatch(Match match) {
        // find stage
        Stage stage = stageRepository.findByMatchesContains(match);
        if (stage == null) {
            // match probably belongs to a GroupStage
            // find corresponding group
            Group group = groupRepository.findByMatchesContains(match);
            // find corresponding groupstage
            GroupStage groupStage = groupStageRepository.findByGroupsContains(group);
            // find corresponding tournament
            return tournamentRepository.findByGroupStage(groupStage);
        }
        // find corresponding tournament
        return tournamentRepository.findByStagesContains(stage);
    }
}
