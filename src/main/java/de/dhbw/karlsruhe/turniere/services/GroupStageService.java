package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Group;
import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.repositories.GroupRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.GroupStageRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupStageService {

    private final GroupStageRepository groupStageRepository;
    private final GroupRepository groupRepository;
    private final MatchRepository matchRepository;

    public GroupStage generateGroupStage(List<Team> origTeams, Tournament tournament, Integer groupSize) {
        List<Team> teams = new ArrayList<>(origTeams);
        int requiredGroupCount = teams.size() / groupSize;
        List<Group> groups = new ArrayList<>();
        char name = 'A';

        for (int i = 0; i < requiredGroupCount; i++) {
            groups.add(new Group((char) ((int) name + i)));
            List<Team> groupTeams = new ArrayList<>();
            for (int j = 0; j < origTeams.size() / requiredGroupCount; j++) {
                groupTeams.add(teams.get(0));
                teams.remove(0);
            }
            groups.get(i).setTeams(groupTeams);
            groups.get(i).setMatches(generateAllPossibleMatches(groupTeams));
        }
        Iterable<Group> savedGroupsIterable = groupRepository.saveAll(groups);
        List<Group> savedGroups = new ArrayList<>();
        savedGroupsIterable.forEach(savedGroups::add);
        GroupStage groupStage = new GroupStage(savedGroups);
        groupStage = groupStageRepository.save(groupStage);
        tournament.setGroupStage(groupStage);
        return groupStage;
    }

    private List<Match> generateAllPossibleMatches(List<Team> origTeams) {
        List<Match> matches = new ArrayList<>();
        List<Team> teams = new ArrayList<>(origTeams);
        int position = 0;
        int cycle = teams.size();
        for (int i = 0; i < cycle; i++) {
            Team team1 = teams.get(0);
            for (int j = 1; j < teams.size(); j++) {
                Team team2 = teams.get(j);
                matches.add(new Match(team1, team2, null, null, Match.State.NOT_STARTED, position++));
            }
            teams.remove(0);
        }
        Iterable<Match> savedMatchesIterable = matchRepository.saveAll(matches);
        List<Match> savedMatches = new ArrayList<>();
        savedMatchesIterable.forEach(savedMatches::add);
        return matches;
    }
}
