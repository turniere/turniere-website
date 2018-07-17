package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.forms.ChangeTournamentForm;
import de.dhbw.karlsruhe.turniere.forms.TournamentForm;
import de.dhbw.karlsruhe.turniere.forms.validators.TournamentFormValidator;
import de.dhbw.karlsruhe.turniere.services.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;
    private final TournamentFormValidator tournamentFormValidator;

    private Tournament safeGetTournament(String code) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByCode(code);
        if (!optionalTournament.isPresent()) {
            throw new ResourceNotFoundException("Tournament with code '" + code + "'");
        }
        return optionalTournament.get();
    }

    private void verifyOwnership(Tournament tournament, Authentication authentication) {
        User user = User.fromAuthentication(authentication);
        if (!tournament.getOwner().equals(user)) {
            throw new AccessDeniedException("Tournament with code '" + tournament.getCode() + "'");
        }
    }

    @GetMapping("/liste")
    String tournamentList(Model model, Authentication authentication) {
        // add public tournaments to template
        model.addAttribute("publicTournaments", tournamentRepository.findByIsPublic(true));
        // check if user is logged in
        if (authentication != null) {
            // fetch user tournaments
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();
            List<Tournament> userTournaments = user.getTournaments();
            // add to template if not empty
            if (!userTournaments.isEmpty()) {
                model.addAttribute("privateTournaments", userTournaments);
            }
        }
        return "tournaments";
    }

    @GetMapping("/erstellen")
    String createTournamentForm(TournamentForm tournamentForm) {
        return "create_tournament";
    }

    @PostMapping("/erstellen")
    String createTournament(@Valid TournamentForm tournamentForm, BindingResult bindingResult, Authentication
            authentication) {
        tournamentFormValidator.validate(tournamentForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "create_tournament";
        }
        // get authenticated user to add as owner
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User owner = customUserDetails.getUser();
        // create tournament
        Tournament tournament = tournamentService.create(tournamentForm.getName(), tournamentForm.getDescription(),
                tournamentForm.getIsPublic(), tournamentService.splitTeamnames(tournamentForm.getTeamNames()), owner, tournamentForm.getGroupSize(), tournamentForm.getRandomize(), tournamentForm.getPlayoffSize());
        return "redirect:/t/" + tournament.getCode();
    }

    @GetMapping("/t/{code}")
    String viewTournament(@PathVariable String code, Model model, HttpServletResponse httpServletResponse, Authentication authentication) {
        // find tournament object
        Tournament tournament = safeGetTournament(code);
        //sort matches and teams
        tournament.getStages().sort(Comparator.comparing(Stage::getLevel).reversed());
        tournament.getStages().forEach(stage -> stage.getMatches().sort(Comparator.comparing(Match::getPosition)));
        Optional.ofNullable(tournament.getGroupStage())
                .ifPresent(groupStage -> groupStage.getGroups().forEach(group -> {
                    group.getTeams().sort(Comparator.comparing(Team::getGroupPlace).thenComparing(Team::getName));
                    group.getMatches().sort(Comparator.comparing(Match::getPosition));
                }));
        // add tournament object to model
        model.addAttribute("tournament", tournament);
        if (tournament.getQrcode() != null) {
            model.addAttribute("qrcode", new String(tournament.getQrcode()));
        }
        User owner = tournament.getOwner();
        boolean ownerIsAuthenticated = authentication != null && owner.equals(User.fromAuthentication(authentication));
        model.addAttribute("ownerIsAuthenticated", ownerIsAuthenticated);
        model.addAttribute("ownerName", owner.getUsername());
        return "tournament";
    }

    @GetMapping("/t")
    String redirectToTournament(@RequestParam("code") String code) {
        if (code == null) {
            return "redirect:/liste";
        } else {
            return "redirect:/t/" + code;
        }
    }

    @GetMapping("/t/{code}/edit")
    String editTournament(@PathVariable String code, ChangeTournamentForm changeTournamentForm, Model model, Authentication authentication) {
        // find tournament object
        Tournament tournament = safeGetTournament(code);
        verifyOwnership(tournament, authentication);
        model.addAttribute("tournament", tournament);
        return "edit_tournament";
    }

    @PostMapping("/t/{code}/edit")
    String postEditTournament(@PathVariable String code, @Valid ChangeTournamentForm changeTournamentForm, BindingResult bindingResult, Model model, Authentication authentication) {
        Tournament tournament = safeGetTournament(code);
        verifyOwnership(tournament, authentication);
        model.addAttribute("tournament", tournament);
        if (bindingResult.hasErrors()) {
            return "edit_tournament";
        }
        tournament.setName(changeTournamentForm.getName());
        tournament.setDescription(changeTournamentForm.getDescription());
        tournament.setIsPublic(changeTournamentForm.getIsPublic());
        tournamentRepository.save(tournament);
        return "redirect:/t/" + tournament.getCode();
    }
}
