package com.meppy.expression;

/**
 * Thrown during the parsing of an expression. For example, if the parser reached the end of the token stream prematurely.
 */
public class ParsingException extends RuntimeException {
    public ParsingException(String message) {
        super(message);
    }
}