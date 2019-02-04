package com.meppy.expression;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class CompilerUnitTest {
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
    public void testNormalizeNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NORMALIZED, true);
        Assert.assertEquals(5, code.getLength());
    }

    @Test
    public void testNormalizeNotNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NOT_NORMALIZED, true);
        Assert.assertEquals(5, code.getLength());
    }

    @Test
    public void testNormalizeNull() {
        ByteCode code = Compiler.compile(null, true);
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testNormalizeEmpty() {
        ByteCode code = Compiler.compile("", true);
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testNotNormalizeNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NORMALIZED, false);
        Assert.assertEquals(5, code.getLength());
    }

    @Test
    public void testNotNormalizeNotNormalizedExpression() {
        ByteCode code = Compiler.compile(Expressions.NOT_NORMALIZED, false);
        Assert.assertEquals(2, code.getLength());
    }

    @Test(expected = NullPointerException.class)
    public void testNotNormalizeNull() {
        ByteCode code = Compiler.compile(null, false);
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testNotNormalizeEmpty() {
        ByteCode code = Compiler.compile("", false);
        Assert.assertEquals(0, code.getLength());
    }

    @Test
    public void testByteCode() {
        ByteCode code = Compiler.compile(Expressions.NORMALIZED, false);
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
        ByteCode code = Compiler.compile(Expressions.IDENTIFIERS, false);
        Assert.assertEquals(1, code.getIdentifiers().size());
        Assert.assertTrue(code.getIdentifiers().contains("simple"));
    }
}