package com.meppy.expression;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public final class LexerUnitTest {
    private static CompileOptions compileOptions;
    private static CompileOptions compileOptions1;

    @BeforeClass
    public static void setUp() {
        compileOptions = new CompileOptions();
        compileOptions1 = new CompileOptions(true);
    }

    @Test
    public void testFormattingAndCulture() {
        Lexer lexer = new Lexer(compileOptions);
        List<Token> tokens = lexer.tokenize(Expressions.FORMATTING_CULTURE);
        Assert.assertEquals(16, tokens.size());
        Assert.assertEquals(TokenType.OP_FORMAT, tokens.get(11).getType());
        Assert.assertEquals(TokenType.FORMAT, tokens.get(12).getType());
        Assert.assertEquals("\"#.00\"", tokens.get(12).getText());
        Assert.assertEquals(TokenType.OP_CULTURE, tokens.get(13).getType());
        Assert.assertEquals(TokenType.CULTURE, tokens.get(14).getType());
        Assert.assertEquals("\"bg_BG\"", tokens.get(14).getText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture1() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture2() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_2);
    }

    @Test
    public void testSeparator() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.SEPARATOR);
        Assert.assertEquals(TokenType.OP_EXPRESSION_SEPARATOR, tokens.get(4).getType());
    }

    @Test
    public void testPower() {
        List<Token> tokens = new Lexer(compileOptions1).tokenize(Expressions.POWER);
        Assert.assertEquals(TokenType.OP_POWER, tokens.get(2).getType());
    }

    @Test
    public void testDot() {
        List<Token> tokens = new Lexer(compileOptions1).tokenize(Expressions.DOT);
        Assert.assertEquals(TokenType.OP_DOT, tokens.get(2).getType());
    }

    @Test
    public void testStringAndE() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.STRING_E);
        Assert.assertEquals("test", tokens.get(1).getText());
        Assert.assertEquals("102E-1", tokens.get(3).getText());
        Assert.assertEquals(TokenType.FLOAT_NUMBER, tokens.get(3).getType());
    }

    @Test
    public void testNull() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.NULL);
        Assert.assertEquals("null", tokens.get(3).getText());
        Assert.assertEquals(TokenType.NULL, tokens.get(3).getType());
    }

    @Test
    public void testColor() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.COLOR);
        Assert.assertEquals("#ABC123", tokens.get(1).getText());
        Assert.assertEquals(TokenType.COLOR, tokens.get(1).getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid1() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIncompleteFormatting() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_INCOMPLETE_FORMATTING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFormatting() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_FORMATTING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture3() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCulture4() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_CULTURE_4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIncompleteString() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_INCOMPLETE_STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIncompleteString2() {
        new Lexer(compileOptions).tokenize(Expressions.INVALID_INCOMPLETE_STRING_2);
    }

    @Test
    public void testEmbedded() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.EMBEDDED);
        Assert.assertEquals(13, tokens.size());
    }

    @Test
    public void testTokenToString() {
        List<Token> tokens = new Lexer(compileOptions).tokenize(Expressions.FORMATTING_CULTURE);
        Assert.assertNotNull(tokens.get(0).toString());
    }
}