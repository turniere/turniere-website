package de.dhbw.karlsruhe.turniere.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MatchIncompleteException extends RuntimeException {
    public MatchIncompleteException(String s) {
        super(s);
    }
}
