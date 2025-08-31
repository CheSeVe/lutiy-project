package ru.CheSeVe.lutiy_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CREATED)
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {super(message);}
}
