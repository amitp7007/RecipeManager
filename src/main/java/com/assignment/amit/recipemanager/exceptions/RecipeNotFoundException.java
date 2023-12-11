package com.assignment.amit.recipemanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RecipeNotFoundException extends RuntimeException{
    public RecipeNotFoundException(String message){
        super(message);
    }
}
