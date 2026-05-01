package com.github.recipeexport.dumper.api;

public class RecipeDumpException extends Exception {
    public RecipeDumpException() {
        super();
    }

    public RecipeDumpException(String message) {
        super(message);
    }

    public RecipeDumpException(String message, Throwable cause) {
        super(message, cause);
    }
}
