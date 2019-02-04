package com.meppy.expression;

/**
 * Specifies the type of a token.
 */
enum TokenType {
    /** Indicates a blank or invalid token. */
    EMPTY,
    /** Indicates a free text token. */
    TEXT,
    /** Indicates the 'null' keyword. */
    NULL,
    /** Indicates an opening square bracket. */
    OSB,
    /** Indicates a closing square bracket. */
    CSB,
    /** Indicates an opening bracket. */
    OB,
    /** Indicates a closing bracket. */
    CB,
    /** Indicates an identifier. */
    IDENTIFIER,
    /** Indicates a function invocation. */
    FUNCTION_CALL,
    /** Indicates a floating point number. */
    FLOAT_NUMBER,
    /** Indicates an integer number. */
    INT_NUMBER,
    /** Indicates a string literal. */
    STRING,
    /** Indicates a color literal. */
    COLOR,
    /** Indicates an addition operator. */
    OP_ADD,
    /** Indicates a subtraction operator. */
    OP_SUBTRACT,
    /** Indicates a multiplication operator. */
    OP_MULTIPLY,
    /** Indicates a division operator. */
    OP_DIVIDE,
    /** Indicates a modulo operator. */
    OP_MOD,
    /** Indicates a less than comparison operator. */
    OP_LESS,
    /** Indicates a greater than comparison operator. */
    OP_GREATER,
    /** Indicates an equal comparison operator. */
    OP_EQUAL,
    /** Indicates a not equal comparison operator. */
    OP_NOT_EQUAL,
    /** Indicates a less than or equal comparison operator. */
    OP_LESS_OR_EQUAL,
    /** Indicates a greater than or equal comparison operator. */
    OP_GREATER_OR_EQUAL,
    /** Indicates a boolean negation operator. */
    OP_NOT,
    /** Indicates a bitwise and operator. */
    OP_AND,
    /** Indicates a bitwise exclusive or operator. */
    OP_XOR,
    /** Indicates a raise to power operator. */
    OP_POWER,
    /** Indicates a bitwise or operator. */
    OP_OR,
    /** Indicates a conditional and operator. */
    OP_CONDITIONAL_AND,
    /** Indicates a conditional or operator. */
    OP_CONDITIONAL_OR,
    /** Indicates a member access operator. */
    OP_DOT,
    /** Indicates a comma. */
    COMMA,
    /** Indicates a formatting operator. */
    OP_FORMAT,
    /** Indicates a formatting string. */
    FORMAT,
    /** Indicates a discarding symbol, that is, a symbol, which when specified as a format will prevent the expression from appearing in the final text. */
    DISCARD,
    /** Indicates a culture specifier. */
    OP_CULTURE,
    /** Indicates a culture name. */
    CULTURE,
    /** Indicates a separator between expressions. */
    OP_EXPRESSION_SEPARATOR
}