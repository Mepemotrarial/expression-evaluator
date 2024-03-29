package com.meppy.expression;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public final class CompilerUnitTest {
    @Test
    public void testNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NORMALIZED);
        Assert.assertEquals(5, code.getLength());
    }

    @Test
    public void testNotNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NOT_NORMALIZED);
        Assert.assertEquals(5, code.getLength());
    }

    @Test
    public void testNull() {
        ByteCode code = Compiler.compile(null);
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testEmpty() {
        ByteCode code = Compiler.compile("");
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testByteCode() {
        ByteCode code = Compiler.compile(Expressions.NORMALIZED);
        Assert.assertEquals(5, code.getLength());
        Assert.assertEquals(1, code.get(1));
        Assert.assertEquals(2, code.get(3));
    }

    @Test
    public void testEmptyByteCode() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ByteCode code = ByteCode.empty();
        Assert.assertNull(code.evaluate(new EvaluationContext(null)));
    }

    @Test
    public void testIdentifiers() {
        ByteCode code = Compiler.compile(Expressions.IDENTIFIERS);
        Assert.assertEquals(1, code.getIdentifiers().size());
        Assert.assertTrue(code.getIdentifiers().contains("simple"));
    }
}