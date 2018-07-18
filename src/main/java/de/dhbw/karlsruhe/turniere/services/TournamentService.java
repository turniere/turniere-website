package de.dhbw.karlsruhe.turniere.services;

import de.dhbw.karlsruhe.turniere.database.models.Team;
import de.dhbw.karlsruhe.turniere.database.models.Tournament;
import de.dhbw.karlsruhe.turniere.database.models.User;
import de.dhbw.karlsruhe.turniere.database.repositories.TeamRepository;
import de.dhbw.karlsruhe.turniere.database.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final QRService qrService;
    private final PlayoffService playoffService;
    private final GroupStageService groupStageService;

    public static String[] splitTeamnames(String string) {
        return string.split(",");
    }

    /**
     * Generate tournament code
     *
     * @param length Length of generated code
     * @return Generated tournament code
     */
    private String generateCode(int length) {
        char[] codeChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        // generate remaining characters
        for (int i = 0; i < length; i++) {
            stringBuilder.append(codeChars[random.nextInt(codeChars.length)]);
        }
        return stringBuilder.toString();
    }

    /**
     * Generate unique tournament code
     * <p>
     * Uniqueness is ensured by checking for existence of the code in the database.
     *
     * @param length Length of generated code
     * @return Generated tournament code
     */
    private String generateUniqueCode(int length) {
        String code = generateCode(length);
        while (tournamentRepository.findByCode(code).isPresent()) {
            code = generateCode(length);
        }
        return code;
    }

    /**
     * Create a new Tournament
     *
     * @param name        Tournament name
     * @param description Tournament description
     * @param isPublic    Tournament accessible without login (deprecated)
     * @param teamNames   Names of initial teams
     * @param owner       Owner of the tournament
     * @param groupSize   Size of Groups to be generated
     * @param randomize   If Teams should be randomized before generating Matches or not
     * @return Saved new tournament object
     */
    public Tournament create(String name, String description, Boolean isPublic, String[] teamNames, User owner, Integer groupSize, Boolean randomize, Integer playoffSize) {
        // generate uuid
        List<Team> teams = Arrays.stream(teamNames).map(teamName -> teamRepository.save(new Team(teamName))).collect(Collectors.toList());
        String code = generateUniqueCode(5);
        // shuffle teams if desired
        if (randomize) {
            Collections.shuffle(teams);
        }
        // create and save tournament object
        Tournament tournament = new Tournament(name, code, description, isPublic, teams);
        try {
            // generate qrcode with tournament code
            byte[] encodedQRCode = qrService.generateQRCode(code);
            // add qrcode to tournament object
            tournament.setQrcode(encodedQRCode);
        } catch (Exception e) {
            // qrcode generation failed
            throw new RuntimeException("QRCode generation failed", e);
        }

        if (groupSize < 2) {
            playoffService.generatePlayoffs(teams, tournament);
        } else {
            groupStageService.generateGroupStage(teams, tournament, groupSize, playoffSize);
        }
        // set owner to authenticated user
        tournament.setOwner(owner);
        owner.getTournaments().add(tournament);
        // save tournament object
        tournament = tournamentRepository.save(tournament);
        return tournament;

    }


}
