package com.meppy.expressions;

/**
 * Represents a recognized token from a sequence of characters, which conforms to the grammar.
 */
class Token {
    /**
     * The text of the token.
     */
    private final String text;

    /**
     * The type of the token.
     */
    private final TokenType type;

    private Token() {
        this.text = "";
        this.type = TokenType.EMPTY;
    }

    /**
     * Initializes a new instance of the {@link Token} class.
     */
    Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
    }

    /**
     * Gets an empty token instance.
     */
    static Token empty() { return new Token(); }

    Token withType(TokenType newType) {
        return new Token(text, newType);
    }

    @Override
    public String toString() {
        return String.format("\"%1$s\" [%2$s]", text, type);
    }

    /**
     * Gets the token text.
     */
    String getText() {
        return text;
    }

    /**
     * Gets the token type.
     */
    TokenType getType() {
        return type;
    }
}