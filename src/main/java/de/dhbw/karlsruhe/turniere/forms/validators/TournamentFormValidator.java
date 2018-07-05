package de.dhbw.karlsruhe.turniere.forms.validators;

import de.dhbw.karlsruhe.turniere.forms.TournamentForm;
import de.dhbw.karlsruhe.turniere.services.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class TournamentFormValidator implements Validator {

    private final TournamentService tournamentService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(TournamentForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        TournamentForm tournamentForm = (TournamentForm) o;
        int groupSize = tournamentForm.getGroupSize();
        if (groupSize == 0 || groupSize == 1) {
            return;
        }
        if (((tournamentService.splitTeamnames(tournamentForm.getTeamNames()).length) % (groupSize)) != 0) {
            errors.rejectValue("groupSize", "teamSizedoesntMatchGroupSize");
        }
        /*
        if (!user.getPassword().equals(user.getRepassword())) {
            errors.rejectValue("repassword", "doesntMatch");
        }
        */
    }
}
