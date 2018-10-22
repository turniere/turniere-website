package de.dhbw.karlsruhe.turniere.controllers;

import de.dhbw.karlsruhe.turniere.authentication.CustomUserDetails;
import de.dhbw.karlsruhe.turniere.database.models.GroupStage;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Stage;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import de.dhbw.karlsruhe.turniere.exceptions.ResourceNotFoundException;
import de.dhbw.karlsruhe.turniere.forms.ChangeTournamentForm;
import de.dhbw.karlsruhe.turniere.forms.TournamentForm;
import de.dhbw.karlsruhe.turniere.forms.validators.TournamentFormValidator;
import de.dhbw.karlsruhe.turniere.services.MatchService;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final TournamentFormValidator tournamentFormValidator;

    private Tournament safeGetTournament(String code) {
        return tournamentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with code '" + code + "'"));
    }

    private void verifyOwnership(Tournament tournament, Authentication authentication) {
        User user = User.fromAuthentication(authentication);
        if (!tournament.getOwner().equals(user)) {
            throw new AccessDeniedException("Tournament with code '" + tournament.getCode() + "'");
        }
    }

    private Model getTournamentAndAddToModel(Model model, Authentication authentication, String code) {
        // find tournament object
        Tournament tournament = safeGetTournament(code);
        return getTournamentAndAddToModel(model, authentication, tournament);
    }

    private Model getTournamentAndAddToModel(Model model, Authentication authentication, Tournament tournament) {
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
        return model;
    }

    private Model getUserAuthentication(Model model, Authentication authentication, String code) {
        Tournament tournament = safeGetTournament(code);
        return getUserAuthentication(model, authentication, tournament);
    }

    private Model getUserAuthentication(Model model, Authentication authentication, Tournament tournament) {
        User owner = tournament.getOwner();
        boolean ownerIsAuthenticated = authentication != null && owner.equals(User.fromAuthentication(authentication));
        model.addAttribute("ownerIsAuthenticated", ownerIsAuthenticated);
        model.addAttribute("ownerName", owner.getUsername());
        return model;
    }

    @GetMapping("/liste")
    String tournamentList(Model model, Authentication authentication) {
        List<Tournament> publicTournaments;
        List<Tournament> privateTournaments;
        // check if user is logged in
        if (authentication != null) {
            // find private tournaments and public tournaments not owned by authenticated user
            User user = User.fromAuthentication(authentication);
            publicTournaments = tournamentRepository.findByOwnerNotAndIsPublic(user, true);
            privateTournaments = tournamentRepository.findByOwner(user);
        } else {
            // find public tournaments
            publicTournaments = tournamentRepository.findByIsPublic(true);
            privateTournaments = new ArrayList<>();
        }
        // add tournament lists to model
        model.addAttribute("publicTournaments", publicTournaments);
        model.addAttribute("privateTournaments", privateTournaments);
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
    String viewTournament(@PathVariable String code, Model model, Authentication authentication) {
        model = getTournamentAndAddToModel(model, authentication, code);
        model = getUserAuthentication(model, authentication, code);
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

    @GetMapping("/t/{code}/fullscreen")
    String fullscreenTournament(@PathVariable String code, @RequestParam("stage") String stageCode, Model model, Authentication authentication) {
        Tournament tournament = safeGetTournament(code);
        model = getUserAuthentication(model, authentication, tournament);
        //getTournamentAndAddToModel(model, authentication, tournament);
        if (stageCode.equals("current")) {
            stageCode = tournament.getCurrentStage();
        }
        switch (stageCode) {
            case "groupStage":
                model.addAttribute("tgroupStage", tournament.getGroupStage());
                break;
            case "winner":
                model.addAttribute("twinner", tournament.getWinner());
                break;
            default:
                int stageInt;
                try {
                    stageInt = Integer.valueOf(stageCode);
                } catch (NumberFormatException e) {
                    throw new ResourceNotFoundException("Stage doesn't exist");
                }
                Optional<Stage> modelStageOptional = tournament.getStages().stream().filter(stage -> stage.getLevel().equals(stageInt)).findFirst();
                if (!modelStageOptional.isPresent()) {
                    throw new ResourceNotFoundException("Stage doesn't exist");
                }
                model.addAttribute("tstage", modelStageOptional.get());

        }
        model.addAttribute("tname", tournament.getName());
        model.addAttribute("tcode", tournament.getCode());
        return "tournament_fullscreen";
    }

    @GetMapping("/t/{code}/edit")
    String editTournament(@PathVariable String code, ChangeTournamentForm changeTournamentForm, Model model, Authentication authentication) {
        // find tournament object
        Tournament tournament = safeGetTournament(code);
        verifyOwnership(tournament, authentication);
        tournament.getTeams().sort(Comparator.comparing(Team::getName));
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

    @GetMapping("/t/{code}/{stageId}/start")
    String startStage(@PathVariable String code, @PathVariable Long stageId, Authentication authentication) {
        Tournament tournament = safeGetTournament(code);
        verifyOwnership(tournament, authentication);
        Optional<Stage> optionalStage = tournament.getStages().stream().filter((Stage stage) -> stage.getId().equals(stageId)).findFirst();
        if (!optionalStage.isPresent()) {
            throw new ResourceNotFoundException("Requested stage doesn't exist");
        }
        Stage stage = optionalStage.get();
        for (Match match : stage.getMatches()) {
            matchService.startMatch(match);
        }
        return "redirect:/t/" + tournament.getCode();
    }

    @GetMapping("/t/{code}/group/next")
    String startNextGroupMatches(@PathVariable String code, Authentication authentication) {
        Tournament tournament = safeGetTournament(code);
        verifyOwnership(tournament, authentication);
        Optional<GroupStage> optionalGroupStage = Optional.ofNullable(tournament.getGroupStage());
        if (!optionalGroupStage.isPresent()) {
            throw new ResourceNotFoundException("GroupStage doesn't exist");
        }
        GroupStage groupStage = optionalGroupStage.get();
        groupStage.getGroups().forEach(group -> {
            Optional<Match> optionalFirstNotStartedMatch = group.getMatches().stream().filter(match -> match.getState() == Match.State.NOT_STARTED).findFirst();
            if (optionalFirstNotStartedMatch.isPresent()) {
                Match firstNotStartedMatch = optionalFirstNotStartedMatch.get();
                matchService.startMatch(firstNotStartedMatch);
            } else {
                // TODO: Handle this somehow...
            }
        });
        return "redirect:/t/" + tournament.getCode();
    }
}
