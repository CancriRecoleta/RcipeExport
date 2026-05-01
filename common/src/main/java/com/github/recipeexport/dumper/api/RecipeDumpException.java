package com.github.recipeexport.dumper.api;

/**
 * 配方导出过程中抛出的受检异常。
 */
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

