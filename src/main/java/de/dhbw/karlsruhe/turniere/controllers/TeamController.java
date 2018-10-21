package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.forms.ChangeTeamForm;
import de.dhbw.karlsruhe.turniere.services.TeamService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TeamController {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;

    private Team safeGetTeam(Long teamId, Authentication authentication) {
        // set exception message (used for 403/404)
        final String exceptionMessage = "Team with id " + teamId;
        // find Team object for teamId
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (!optionalTeam.isPresent()) {
            throw new ResourceNotFoundException(exceptionMessage);
        }
        Team team = optionalTeam.get();
        // find corresponding tournament
        Tournament tournament = tournamentRepository.findByTeamsContains(team);
        // evaluate authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User authenticatedUser = userDetails.getUser();
        // find tournament/team owner
        User owner = tournament.getOwner();
        // verify owner is authenticated user
        if (!authenticatedUser.equals(owner)) {
            throw new AccessDeniedException(exceptionMessage);
        }

        return team;
    }

    @GetMapping("/team/{teamId}")
    ResponseEntity<?> match(@PathVariable Long teamId, Authentication authentication) {
        Team team = safeGetTeam(teamId, authentication);
        return new ResponseEntity<>(new TeamController.TeamResponse(team), HttpStatus.OK);
    }

    @PostMapping("/team/{teamId}")
    ResponseEntity<?> postTeam(@PathVariable Long teamId, @Valid @RequestBody ChangeTeamForm changeTeamForm, BindingResult bindingResult, Authentication authentication) {
        Team team = safeGetTeam(teamId, authentication);
        // validate form doesn't have errors
        if (bindingResult.hasErrors()) {
            // convert form errors to json serializable objects
            return new ResponseEntity<>(new TeamController.TeamResponse(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        // set name
        teamService.setName(team, changeTeamForm.getName());


        return new ResponseEntity<>(new TeamController.TeamResponse(team), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    private class TeamResponse {
        private String error = "";
        private List<FieldError> formErrors = new ArrayList<>();
        private Team data;

        private TeamResponse(String error) {
            this.error = error;
        }

        private TeamResponse(List<FieldError> formErrors) {
            this.formErrors = formErrors;
        }

        private TeamResponse(Team data) {
            this.data = data;
        }
    }
}
