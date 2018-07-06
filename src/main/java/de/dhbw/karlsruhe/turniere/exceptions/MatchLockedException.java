package de.dhbw.karlsruhe.turniere.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MatchLockedException extends RuntimeException {
    public MatchLockedException(String s) {
        super(s);
    }
}
