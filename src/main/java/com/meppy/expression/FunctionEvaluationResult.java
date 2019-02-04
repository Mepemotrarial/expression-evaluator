package com.meppy.expression;

/**
 * Encapsulates the result of a custom function evaluation.
 */
public final class FunctionEvaluationResult {
    private final Object result;
    private final boolean isEvaluated;

    FunctionEvaluationResult() {
        result = null;
        isEvaluated = false;
    }

    public FunctionEvaluationResult(Object result) {
        this.result = result;
        isEvaluated = true;
    }

    public static FunctionEvaluationResult notEvaluated() {
        return new FunctionEvaluationResult();
    }

    public Object getResult() {
        return result;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }
}