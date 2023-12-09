package com.assignment.amit.recipemanager.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class RecipeAlreadyExistException extends RuntimeException {
    public RecipeAlreadyExistException(String message) {
        super(message);
    }
}
