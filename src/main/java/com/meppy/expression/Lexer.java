package com.meppy.expression;

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
     * The options used to customize the lexical analysis.
     */
    private CompileOptions options;

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
    Lexer(CompileOptions options) {
        this.options = options;
    }

    /**
     * Performs a lexical analysis of the specified string and returns a list of tokens.
     * @param input The string to analyze.
     * @return A list of {@link Token} objects.
     */
    List<Token> tokenize(String input) {
        this.input = input;
        this.current = 0;
        this.inExp = false;
        this.inFormat = false;
        this.inLocale = false;

        tokens = new ArrayList<>();

        while (current < input.length()) {
            tokenizeNext();
        }

        return tokens;
    }

    private void tokenizeNext() {
        if (text()) {
            return;
        }
        if (ws()) {
            return;
        }
        if (osb()) {
            return;
        }
        if (csb()) {
            return;
        }
        if (ob()) {
            return;
        }
        if (cb()) {
            return;
        }
        if (nul()) {
            return;
        }
        if (identifier()) {
            return;
        }
        if (number()) {
            return;
        }
        if (color()) {
            return;
        }
        if (string()) {
            return;
        }
        if (opAdd()) {
            return;
        }
        if (opSubtract()) {
            return;
        }
        if (opMultiply()) {
            return;
        }
        if (opDivide()) {
            return;
        }
        if (opMod()) {
            return;
        }
        if (opLessOrEqual()) {
            return;
        }
        if (opGreaterOrEqual()) {
            return;
        }
        if (opNotEqual()) {
            return;
        }
        if (opLess()) {
            return;
        }
        if (opGreater()) {
            return;
        }
        if (opEqual()) {
            return;
        }
        if (opNot()) {
            return;
        }
        if (opConditionalAnd()) {
            return;
        }
        if (opAnd()) {
            return;
        }
        if (opConditionalOr()) {
            return;
        }
        if (opOr()) {
            return;
        }
        if (opPowerOrXor()) {
            return;
        }
        if (opDot()) {
            return;
        }
        if (comma()) {
            return;
        }
        if (opFormat()) {
            return;
        }
        if (discard()) {
            return;
        }
        if (format()) {
            return;
        }
        if (opCulture()) {
            return;
        }
        if (culture()) {
            return;
        }
        if (opExpressionSeparator()) {
            return;
        }

        throw new IllegalArgumentException(String.format("Failed to recognize character '%1$s' at position %2$d within the input string '%3$s'.",
            input.charAt(current), current, input));
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
     * Recognizes the '^' symbol as either raising to power or xor, depending on the current settings.
     */
    private boolean opPowerOrXor() {
        if (options.getInterpretCircumflexAsPower()) {
            return opPower();
        } else {
            return opXor();
        }
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
        if (match(";")) {
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
                tokens.add(new Token(input.substring(current + 1, position), TokenType.STRING));
                current = position + 1;
                return true;
            }

            position++;
        }
    }

    /**
     * Recognizes a number.
     */
    private boolean number() {
        // Note: Do not match numbers after identifiers and dots.
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last != null && (last.getType() == TokenType.IDENTIFIER || last.getType() == TokenType.OP_DOT)) {
            return false;
        }

        int length = match(floatNumberRegex);
        if (length > 0) {
            // Check if its an integer number
            int length2 = match(intNumberRegex);
            if (length2 == length) {
                tokens.add(new Token(input.substring(current, current + length), TokenType.INT_NUMBER));
                current += length;
            } else {
                tokens.add(new Token(input.substring(current, current + length), TokenType.FLOAT_NUMBER));
                current += length;
            }
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
}