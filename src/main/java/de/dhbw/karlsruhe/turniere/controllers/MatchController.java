package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.MatchRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.StageRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.exceptions.StageLockedException;
import de.dhbw.karlsruhe.turniere.forms.MatchResultSubmitForm;
import de.dhbw.karlsruhe.turniere.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class MatchController {
    @Autowired
    TournamentRepository tournamentRepository;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    MatchService matchService;
    @Autowired
    StageRepository stageRepository;


    /**
     * Get match object for matchId or throw 404/403 if applicable
     *
     * @param matchId        ID of the Match to fetch
     * @param authentication Request authentication to check for ownership of the request match
     * @return Match object for matchId
     * @throws ResourceNotFoundException If there's no corresponding match
     * @throws RuntimeException If there's no parent stage
     * @throws StageLockedException If the parent stage is locked
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
        User owner = tournamentRepository.findOwner(tournament);
        // check owner is authenticated user
        if (!authenticatedUser.equals(owner)) {
            throw new AccessDeniedException(exceptionMessage);
        }
        Stage parentStage = stageRepository.findByMatchesContains(match);
        if (parentStage == null) {
            throw new RuntimeException(exceptionMessage + " doesn't have a parent stage");
        }
        if (parentStage.getIsLocked()) {
            throw new StageLockedException("Stage with id " + parentStage.getId() + " is locked");
        }
        return match;
    }

    /**
     * Check if all matches in stage are finished
     *
     * @param stage
     * @return Finished state of stage
     */
    private boolean checkStageFinished(Stage stage) {
        for (Match match : stage.getMatches()) {
            Match.State matchState = match.getState();
            if (matchState.equals(Match.State.NOT_STARTED) || matchState.equals(Match.State.IN_PROGRESS)) {
                return false;
            }
        }
        return true;
    }

    @GetMapping("/m/{matchId}")
    String match(@PathVariable Long matchId, Model model, MatchResultSubmitForm matchResultSubmitForm, Authentication authentication) {
        model.addAttribute("match", safeGetMatch(matchId, authentication));
        return "match";
    }

    @PostMapping("/m/{matchId}")
    String postMatch(@PathVariable Long matchId, @Valid MatchResultSubmitForm matchResultSubmitForm, BindingResult bindingResult, Model model, Authentication authentication) {
        Match match = safeGetMatch(matchId, authentication);
        // add match to template model
        model.addAttribute("match", match);
        // validate form doesn't have errors
        if (bindingResult.hasErrors()) {
            return "match";
        }
        // set scores
        if (matchResultSubmitForm.getIsLive()) {
            matchService.setLivescore(match, matchResultSubmitForm.getScoreTeam1(), matchResultSubmitForm.getScoreTeam2());
        } else {
            matchService.setResults(match, matchResultSubmitForm.getScoreTeam1(), matchResultSubmitForm.getScoreTeam2());
        }
        // check if all matches on that stage are finished
        Stage parentStage = stageRepository.findByMatchesContains(match);
        if (checkStageFinished(parentStage)) {
            parentStage.lock();
            stageRepository.save(parentStage);
        }
        Tournament tournament = tournamentRepository.findByStagesContains(parentStage);
        if (matchResultSubmitForm.getIsLive()) {
            return "match";
        } else {
            return "redirect:/t/" + tournament.getCode();
        }
    }
}
