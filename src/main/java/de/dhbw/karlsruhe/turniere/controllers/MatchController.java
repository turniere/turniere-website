package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.GroupRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.GroupStageRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.MatchIncompleteException;
import de.dhbw.karlsruhe.turniere.exceptions.MatchLockedException;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.forms.MatchResultSubmitForm;
import de.dhbw.karlsruhe.turniere.services.GroupStageService;
import de.dhbw.karlsruhe.turniere.services.MatchService;
import de.dhbw.karlsruhe.turniere.services.PlayoffService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MatchController {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final GroupStageService groupStageService;
    private final GroupStageRepository groupStageRepository;
    private final GroupRepository groupRepository;
    private final PlayoffService playoffService;

    private Tournament safeGetTournament(String code) {
        return tournamentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with code '" + code + "'"));
    }

    /**
     * Get match object for matchId or throw 404/403 if applicable
     *
     * @param matchId        ID of the Match to fetch
     * @param authentication Request authentication to check for ownership of the request match
     * @return Match object for matchId
     * @throws ResourceNotFoundException If there's no corresponding match
     * @throws RuntimeException          If there's no parent stage
     * @throws MatchLockedException      If the match is locked (winner was already evaluated)
     */
    private Match safeGetMatch(Long matchId, Authentication authentication) {
        // set exception message (used for 403/404)
        final String exceptionMessage = "Match with id " + matchId;
        // find match object for matchId
        Optional<Match> optionalMatch = matchRepository.findById(matchId);
        if (!optionalMatch.isPresent()) {
            throw new ResourceNotFoundException(exceptionMessage);
        }
        Match match = optionalMatch.get();
        // find corresponding tournament
        Tournament tournament = tournamentRepository.findByMatch(match);
        // evaluate authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User authenticatedUser = userDetails.getUser();
        // find match owner
        User owner = tournament.getOwner();
        // verify owner is authenticated user
        if (!authenticatedUser.equals(owner)) {
            throw new AccessDeniedException(exceptionMessage);
        }
        // verify match is not locked
        if (match.isLocked()) {
            throw new MatchLockedException(exceptionMessage + " is locked");
        }
        // verify match is completely initialized
        if (match.getTeam1().getName() == null || match.getTeam2().getName() == null) {
            throw new MatchIncompleteException(exceptionMessage + " is incomplete");
        }
        return match;
    }

    @GetMapping("/m/{matchId}")
    ResponseEntity<?> match(@PathVariable Long matchId, Authentication authentication) {
        Match match = safeGetMatch(matchId, authentication);
        return new ResponseEntity<>(new MatchResponse(match), HttpStatus.OK);
    }

    @PostMapping("/m/{matchId}")
    ResponseEntity<?> postMatch(@PathVariable Long matchId, @Valid @RequestBody MatchResultSubmitForm matchResultSubmitForm, BindingResult bindingResult, Authentication authentication) {
        Match match = safeGetMatch(matchId, authentication);
        // validate form doesn't have errors
        if (bindingResult.hasErrors()) {
            // convert form errors to json serializable objects
            return new ResponseEntity<>(new MatchResponse(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        // set scores
        if (matchResultSubmitForm.getLive()) {
            matchService.setLivescore(match, matchResultSubmitForm.getScore1(), matchResultSubmitForm.getScore2());
        } else {
            matchService.setResults(match, matchResultSubmitForm.getScore1(), matchResultSubmitForm.getScore2());
        }
        if (match.getIsGroupMatch()) {
            groupStageService.updateTeamPoints(match.getTeam1());
            groupStageService.updateTeamPoints(match.getTeam2());
            GroupStage groupStage = groupStageRepository.findByGroupsContains(groupRepository.findByMatchesContains(match));
            if (groupStageService.isGroupStageOver(groupStage)) {
                List<Team> teams = groupStageService.getPlayoffTeams(groupStage);
                teams = groupStageService.sortTeams(teams);
                teams = playoffService.sortByMatchups(teams, groupStage.getGroups().size());
                Tournament tournament = tournamentRepository.findByGroupStage(groupStage);
                playoffService.generatePlayoffs(teams, tournament);
                tournamentRepository.save(tournament);
            }
            groupStage.setCurrentPhase(match.getPosition());
            groupStageRepository.save(groupStage);
        }
        return new ResponseEntity<>(new MatchResponse(match), HttpStatus.OK);
    }

    @GetMapping("/m/t/{code}")
    ResponseEntity<?> matches(@PathVariable String code) {
        Tournament tournament = safeGetTournament(code);
        List<Match> matches = tournament.getAllMatches();
        List<MatchResponse> result = matches.stream().map(MatchResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    private class MatchResponse {
        private String error = "";
        private List<FieldError> formErrors = new ArrayList<>();
        private Match data;

        private MatchResponse(String error) {
            this.error = error;
        }

        private MatchResponse(List<FieldError> formErrors) {
            this.formErrors = formErrors;
        }

        private MatchResponse(Match data) {
            this.data = data;
        }
    }
}
