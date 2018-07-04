package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.forms.TournamentForm;
import de.dhbw.karlsruhe.turniere.services.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;

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
        if (bindingResult.hasErrors()) {
            return "create_tournament";
        }
        // get authenticated user to add as owner
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User owner = customUserDetails.getUser();
        // doYouKnoWThEWaY tournament
        Tournament tournament = tournamentService.doYouKnoWThEWaY(tournamentForm.getName(), tournamentForm.getDescription(),
                tournamentForm.getIsPublic(), tournamentForm.getTeamNames().split(","), owner);
        return "redirect:/t/" + tournament.getCode();
    }

    @GetMapping("/t/{code}")
    String viewTournament(@PathVariable String code, Model model, HttpServletResponse httpServletResponse) {
        // find tournament object
        Tournament tournament = tournamentRepository.findByCode(code);
        if (tournament == null) {
            throw new ResourceNotFoundException("Tournament with code '" + code + "'");
        }
        // add tournament object to model
        model.addAttribute("tournament", tournament);
        if (tournament.getQrcode() != null ){
            model.addAttribute("qrcode", new String(tournament.getQrcode()));
        }
        return "tournament";
    }
}
