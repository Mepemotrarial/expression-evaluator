package com.meppy.expression;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public final class ParserUnitTest {
    private static CompileOptions compileOptions;
    private static CompileOptions compileOptions1;

    @BeforeClass
    public static void setUp() {
        compileOptions = new CompileOptions();
        compileOptions1 = new CompileOptions(true);
    }

    @Test
    public void testFormattingAndCulture() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.FORMATTING_CULTURE);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture1() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_1);
        new Parser(tokens).parse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture2() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_2);
        new Parser(tokens).parse();
    }

    @Test
    public void testSeparator() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.SEPARATOR);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testPower() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.POWER);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testDot() {
        List<Token> tokens = new Lexer(compileOptions1).tokenize(Expressions.DOT);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testStringAndE() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.STRING_E);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testNull() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.NULL);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testColor() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.COLOR);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid1() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_1);
        new Parser(tokens).parse();
    }

    @Test
    public void testInvalid2() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_2);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test(expected = ParsingException.class)
    public void testInvalid3() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_3);
        new Parser(tokens).parse();
    }

    @Test(expected = ParsingException.class)
    public void testInvalid4() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_4);
        new Parser(tokens).parse();
    }

    @Test(expected = ParsingException.class)
    public void testInvalid5() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_5);
        new Parser(tokens).parse();
    }

    @Test
    public void testInvalid6() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_6);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid7() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_7);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid8() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_8);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid9() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_9);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid10() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_10);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid11() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_11);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid12() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_12);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid13() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_13);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid14() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_14);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid15() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_15);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid16() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_16);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid17() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_17);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid18() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_18);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid19() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_19);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid20() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_20);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid21() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_21);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid22() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_22);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid23() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_23);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testInvalid24() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.INVALID_24);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }

    @Test
    public void testEmbedded() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.EMBEDDED);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testComparison() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.COMPARISON);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testIdentifiersAndNot() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.IDENTIFIERS_NOT);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNotNull(node);
    }

    @Test
    public void testFunctionsInvalidArguments() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.FUNCTIONS_INVALID_ARGUMENTS);
        ParseTreeNode node = new Parser(tokens).parse();
        Assert.assertNull(node);
    }
}