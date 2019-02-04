package com.meppy.expression;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public final class EvaluationContextUnitTest {
    private static final class CustomEvaluationContext extends EvaluationContext {
        public CustomEvaluationContext() {
            super(null);
        }

        public Object evaluateNullIdentifier() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            return evaluateIdentifier(null);
        }
    }

    @Test
    public void testLocale() {
        EvaluationContext context = new EvaluationContext(null, new Locale("bg"));
        Assert.assertEquals(new Locale("bg"), context.getLocale());
    }

    @Test
    public void testDispatchFunctionCall() {
        EvaluationContext context = new EvaluationContext(null, Locale.ROOT,
            (name, params) -> {
                if (name.equals("customFunction")) {
                    return new FunctionEvaluationResult(params[0]);
                }

                return FunctionEvaluationResult.notEvaluated();
            });
        Assert.assertEquals(50, context.invokeFunction("customFunction", 50));
    }

    @Test(expected = EvaluationException.class)
    public void testEvaluateIdentifierWithNull() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CustomEvaluationContext context = new CustomEvaluationContext();
        Object result = context.evaluateNullIdentifier();
        Assert.assertNull(result);
    }
}