package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.forms.TournamentForm;
import de.dhbw.karlsruhe.turniere.services.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;

    @GetMapping("/tournaments")
    String tournamentList(Model model) {
        model.addAttribute("publicTournaments", tournamentRepository.findByIsPublic(true));
        return "tournaments";
    }

    @GetMapping("/tournaments/create")
    String createTournamentForm(TournamentForm tournamentForm) {
        return "create_tournament";
    }

    @PostMapping("/tournaments/create")
    String createTournament(@Valid TournamentForm tournamentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create_tournament";
        }
        tournamentService.create(tournamentForm.getName(), tournamentForm.getDescription(), tournamentForm.getIsPublic(), tournamentForm.getTeamNames().split(","));
        return "create_tournament";
    }
}
