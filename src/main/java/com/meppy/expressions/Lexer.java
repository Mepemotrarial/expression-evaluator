package com.meppy.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides capabilities for recognizing character sequences of the grammar.
 */
final class Lexer {
    /**
     * Identifier regular expression.
     */
    private static final Pattern identifierRegex = Pattern.compile("[a-zA-Z_][a-z_A-Z0-9]*");

    /**
     * Free text regular expression.
     */
    private static final Pattern textRegex = Pattern.compile("[^\\[]*");

    /**
     * Integer number regular expression.
     */
    private static final Pattern intNumberRegex = Pattern.compile("[0-9]+");

    /**
     * Float number regular expression.
     */
    private static final Pattern floatNumberRegex = Pattern.compile("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    /**
     * Color regular expression.
     */
    private static final Pattern colorRegex = Pattern.compile("#[a-zA-Z0-9]*");

    /**
     * Whitespace regular expression.
     */
    private static final Pattern wsRegex = Pattern.compile("[\\s]*");


    /**
     * The sequence of recognized tokens.
     */
    private List<Token> tokens;

    /**
     * The current processing position.
     */
    private int current;

    /**
     * The input string.
     */
    private String input;

    /**
     * A flag indicating whether currently in an expression.
     */
    private boolean inExp;

    /**
     * A flag indicating whether currently in a format specifier.
     */
    private boolean inFormat;

    /**
     * A flag indicating whether currently in a locale specifier.
     */
    private boolean inLocale;

    /**
     * Initializes a new instance of the {@link Lexer} class.
     */
    Lexer(String input, CompileOptions options) {
        this.current = 0;
        this.input = input;

        this.inExp = false;
        this.inFormat = false;
        this.inLocale = false;

        tokens = new ArrayList<>();

        while (current < input.length()) {
            if (text()) {
                continue;
            }
            if (ws()) {
                continue;
            }
            if (osb()) {
                continue;
            }
            if (csb()) {
                continue;
            }
            if (ob()) {
                continue;
            }
            if (cb()) {
                continue;
            }
            if (nul()) {
                continue;
            }
            if (identifier()) {
                continue;
            }
            if (floatNumber()) {
                continue;
            }
            if (intNumber()) {
                continue;
            }
            if (color()) {
                continue;
            }
            if (string()) {
                continue;
            }
            if (opAdd()) {
                continue;
            }
            if (opSubtract()) {
                continue;
            }
            if (opMultiply()) {
                continue;
            }
            if (opDivide()) {
                continue;
            }
            if (opMod()) {
                continue;
            }
            if (opLessOrEqual()) {
                continue;
            }
            if (opGreaterOrEqual()) {
                continue;
            }
            if (opNotEqual()) {
                continue;
            }
            if (opLess()) {
                continue;
            }
            if (opGreater()) {
                continue;
            }
            if (opEqual()) {
                continue;
            }
            if (!options.getInterpretExclamationAsReference() && opNot()) {
                continue;
            }
            if (opConditionalAnd()) {
                continue;
            }
            if (opAnd()) {
                continue;
            }
            if (opConditionalOr()) {
                continue;
            }
            if (opOr()) {
                continue;
            }
            if (!options.getInterpretCircumflexAsPower()) {
                if (opXor())
                    continue;
            } else {
                if (opPower())
                    continue;
            }
            if (options.getInterpretExclamationAsReference() && opReference()) {
                continue;
            }
            if (opDot()) {
                continue;
            }
            if (comma()) {
                continue;
            }
            if (opFormat()) {
                continue;
            }
            if (discard()) {
                continue;
            }
            if (format()) {
                continue;
            }
            if (opCulture()) {
                continue;
            }
            if (culture()) {
                continue;
            }
            if (opExpressionSeparator()) {
                continue;
            }

            throw new IllegalArgumentException(String.format("Failed to recognize character '%1$s' at position %2$d within the input string '%3$s'.",
                input.charAt(current), current, input));
        }
    }

    /**
     * Matches the specified string against the substring at the current position within the underlying string.
     */
    private boolean match(String s) {
        return input.startsWith(s, current);
    }

    /**
     * Matches the specified regex against the string starting from the current position within the underlying string.
     */
    private int match(Pattern pattern) {
        Matcher m = pattern.matcher(input);
        if (!m.find(current)) {
            return 0;
        }
        if (m.start() == m.end()) {
            return 0;
        }
        if (m.start() != current) {
            return 0;
        }

        return m.end() - m.start();
    }

    private boolean matchToken(String text, TokenType tokenType) {
        if (match(text)) {
            tokens.add(new Token(input.substring(current, current + text.length()), tokenType));
            current += text.length();
            return true;
        }

        return false;
    }

    /**
     * Recognizes a whitespace token.
     */
    private boolean ws() {
        int length = match(wsRegex);
        if (length > 0) {
            // Note: We are ignoring whitespaces
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes an opening bracket.
     */
    private boolean ob() {
        return matchToken("(", TokenType.OB);
    }

    /**
     * Recognizes a closing bracket.
     */
    private boolean cb() {
        return matchToken(")", TokenType.CB);
    }

    /**
     * Recognizes an opening square bracket.
     */
    private boolean osb() {
        if (!inExp && matchToken("[", TokenType.OSB)) {
            inExp = true;
            return true;
        }

        return false;
    }

    /**
     * Recognizes a closing square bracket.
     */
    private boolean csb() {
        if (inExp && matchToken("]", TokenType.CSB)) {
            inExp = false;
            inFormat = false;
            inLocale = false;
            return true;
        }

        return false;
    }

    /**
     * Recognizes an addition operator '+'.
     */
    private boolean opAdd() {
        return matchToken("+", TokenType.OP_ADD);
    }

    /**
     * Recognizes a subtraction operator '-'.
     */
    private boolean opSubtract() {
        return matchToken("-", TokenType.OP_SUBTRACT);
    }

    /**
     * Recognizes a multiplication operator '*'.
     */
    private boolean opMultiply() {
        return matchToken("*", TokenType.OP_MULTIPLY);
    }

    /**
     * Recognizes a division operator '/'.
     */
    private boolean opDivide() {
        return matchToken("/", TokenType.OP_DIVIDE);
    }

    /**
     * Recognizes a modulo operator '%'.
     */
    private boolean opMod() {
        return matchToken("%", TokenType.OP_MOD);
    }

    /**
     * Recognizes a less than comparison operator '&lt;'.
     */
    private boolean opLess() {
        return matchToken("<", TokenType.OP_LESS);
    }

    /**
     * Recognizes a greater than comparison operator '&gt;'.
     */
    private boolean opGreater() {
        return matchToken(">", TokenType.OP_GREATER);
    }

    /**
     * Recognizes an equal comparison operator '=='.
     */
    private boolean opEqual() {
        return matchToken("==", TokenType.OP_EQUAL);
    }

    /**
     * Recognizes a not equal comparison operator '!='.
     */
    private boolean opNotEqual() {
        return matchToken("!=", TokenType.OP_NOT_EQUAL);
    }

    /**
     * Recognizes a less than or equal comparison operator '&lt;='.
     */
    private boolean opLessOrEqual() {
        return matchToken("<=", TokenType.OP_LESS_OR_EQUAL);
    }

    /**
     * Recognizes a greater than or equal comparison operator '&gt;='.
     */
    private boolean opGreaterOrEqual() {
        return matchToken(">=", TokenType.OP_GREATER_OR_EQUAL);
    }

    /**
     * Recognizes a boolean negation operator '!'.
     */
    private boolean opNot() {
        if (inFormat) {
            return false;
        }

        return matchToken("!", TokenType.OP_NOT);
    }

    /**
     * Recognizes a bitwise and operator '&amp;'.
     */
    private boolean opAnd() {
        return matchToken("&", TokenType.OP_AND);
    }

    /**
     * Recognizes a bitwise or operator '|'.
     */
    private boolean opOr() {
        return matchToken("|", TokenType.OP_OR);
    }

    /**
     * Recognizes a bitwise or operator '^'.
     */
    private boolean opXor() {
        return matchToken("^", TokenType.OP_XOR);
    }

    /**
     * Recognizes the raise to power operator '^'.
     */
    private boolean opPower() {
        return matchToken("^", TokenType.OP_POWER);
    }

    /**
     * Recognizes a conditional and operator '&amp;&amp;'.
     */
    private boolean opConditionalAnd() {
        return matchToken("&&", TokenType.OP_CONDITIONAL_AND);
    }

    /**
     * Recognizes a conditional or operator '||'.
     */
    private boolean opConditionalOr() {
        return matchToken("||", TokenType.OP_CONDITIONAL_OR);
    }

    /**
     * Recognizes a reference operator '!'.
     */
    private boolean opReference() {
        return matchToken("!", TokenType.OP_REFERENCE);
    }

    /**
     * Recognizes a member access operator '.'.
     */
    private boolean opDot() {
        return matchToken(".", TokenType.OP_DOT);
    }

    /**
     * Recognizes the formatting operator '@'.
     */
    private boolean opFormat() {
        if (matchToken("@", TokenType.OP_FORMAT)) {
            inFormat = true;
            return true;
        }

        return false;
    }

    /**
     * Recognizes the culture specifier ':' within a format.
     */
    private boolean opCulture() {
        if (inFormat && matchToken(":", TokenType.OP_CULTURE)) {
            inLocale = true;
            return true;
        }

        return false;
    }

    private boolean opExpressionSeparator() {
        if (match(";") || match(",")) {
            tokens.add(new Token(input.substring(current, current + 1), TokenType.OP_EXPRESSION_SEPARATOR));
            current++;
            return true;
        }

        return false;
    }

    /**
     * Recognizes comma.
     */
    private boolean comma() {
        return matchToken(",", TokenType.COMMA);
    }

    /**
     * Recognizes free text.
     */
    private boolean text() {
        if (inExp) {
            return false;
        }

        int length = match(textRegex);
        if (length > 0) {
            tokens.add(new Token(input.substring(current, current + length), TokenType.TEXT));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes an identifier.
     */
    private boolean identifier() {
        int length = match(identifierRegex);
        if (length > 0) {
            tokens.add(new Token(input.substring(current, current + length), TokenType.IDENTIFIER));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes an integer number.
     */
    private boolean intNumber() {
        // Note: Numbers found after dots are matched as identifiers.
        boolean isReference = false;
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last != null && last.getType() == TokenType.OP_DOT) {
            isReference = true;
        }

        int length = match(intNumberRegex);
        if (length > 0) {
            tokens.add(new Token(input.substring(current, current + length), isReference ? TokenType.IDENTIFIER : TokenType.INT_NUMBER));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes a 'null' keyword.
     */
    private boolean nul() {
        // Attempt to recognize 'null' followed by a non-letter
        int length = 4;
        if (input.startsWith("null", current) &&
            (input.length() == current + length || !Character.isLetter(input.charAt(current + length)))) {
            tokens.add(new Token(input.substring(current, current + length), TokenType.NULL));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes a string literal.
     */
    private boolean string() {
        if (!inExp || inFormat || inLocale) {
            return false;
        }

        // Should begin with '"'
        if (current == input.length() - 1) {
            return false;
        }
        if (input.charAt(current) != '"') {
            return false;
        }

        int position = current + 1;

        // Search for closing '"'
        while (true) {
            if (position == input.length()) {
                return false;
            }

            if (input.charAt(position) == '"') {
                tokens.add(new Token(input.substring(current + 1, position - 1), TokenType.STRING));
                current = position + 1;
                return true;
            }

            position++;
        }
    }

    /**
     * Recognizes a float number.
     */
    private boolean floatNumber()
    {
        // Note: Do not match numbers after identifiers and dots.
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last != null && (last.getType() == TokenType.IDENTIFIER || last.getType() == TokenType.OP_DOT)) {
            return false;
        }

        int length = match(floatNumberRegex);
        if (length > 0) {
            tokens.add(new Token(input.substring(current, current + length), TokenType.FLOAT_NUMBER));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes a color.
     */
    private boolean color() {
        int length = match(colorRegex);
        if (length > 0) {
            tokens.add(new Token(input.substring(current, current + length), TokenType.COLOR));
            current += length;
            return true;
        }

        return false;
    }

    /**
     * Recognizes a formatting specifier.
     */
    private boolean format() {
        if (!inFormat || inLocale) {
            return false;
        }

        // Should begin with '"'
        if (current == input.length() - 1) {
            return false;
        }
        if (input.charAt(current) != '"') {
            return false;
        }

        int position = current + 1;

        // Search for closing '"'
        while (true) {
            if (position == input.length()) {
                return false;
            }

            if (input.charAt(position) == '"') {
                tokens.add(new Token(input.substring(current, position + 1), TokenType.FORMAT));
                current = position + 1;
                return true;
            }

            position++;
        }
    }

    /**
     * Recognizes a discard symbol.
     */
    private boolean discard() {
        if (!inFormat || inLocale) {
            return false;
        }

        return matchToken("!", TokenType.DISCARD);
    }

    /**
     * Recognizes a culture name.
     */
    private boolean culture() {
        if (!inLocale) {
            return false;
        }

        // Should begin with '"'
        if (current == input.length() - 1) {
            return false;
        }
        if (input.charAt(current) != '"') {
            return false;
        }

        int position = current + 1;

        // Search for closing '"'
        while (true) {
            if (position == input.length()) {
                return false;
            }

            if (input.charAt(position) == '"') {
                tokens.add(new Token(input.substring(current, position + 1), TokenType.CULTURE));
                current = position + 1;
                return true;
            }

            position++;
        }
    }

    /**
     * Gets the list of tokens produced by the lexer.
     */
    List<Token> getTokens() {
        return tokens;
    }
}