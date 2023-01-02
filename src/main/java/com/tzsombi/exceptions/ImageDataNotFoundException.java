package com.tzsombi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageDataNotFoundException extends RuntimeException{
    public ImageDataNotFoundException(String message) { super(message); }
}
