package com.meppy.expressions;

/**
 * Thrown during the evaluation of an expression. For example, this exception is thrown when
 * calling a method with insufficient number of arguments.
 */
public final class EvaluationException extends RuntimeException {
    public EvaluationException() {
        super();
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
