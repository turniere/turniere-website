package de.dhbw.karlsruhe.turniere.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String s) {
        super(s);
    }
}