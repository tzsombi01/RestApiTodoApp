package com.tzsombi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AtAuthException extends RuntimeException {
    public AtAuthException(String message) {
        super(message);
    }
}
