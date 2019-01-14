package com.meppy.expressions;

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
    Cb,
    /** Indicates an identifier. */
    Identifier,
    /** Indicates a function invocation. */
    FunctionCall,
    /** Indicates a floating point number. */
    FloatNumber,
    /** Indicates an integer number. */
    IntNumber,
    /** Indicates a string literal. */
    String,
    /** Indicates a color literal. */
    Color,
    /** Indicates an addition operator. */
    OpAdd,
    /** Indicates a subtraction operator. */
    OpSubtract,
    /** Indicates a multiplication operator. */
    OpMultiply,
    /** Indicates a division operator. */
    OpDivide,
    /** Indicates a modulo operator. */
    OpMod,
    /** Indicates a less than comparison operator. */
    OpLess,
    /** Indicates a greater than comparison operator. */
    OpGreater,
    /** Indicates an equal comparison operator. */
    OpEqual,
    /** Indicates a not equal comparison operator. */
    OpNotEqual,
    /** Indicates a less than or equal comparison operator. */
    OpLessOrEqual,
    /** Indicates a greater than or equal comparison operator. */
    OpGreaterOrEqual,
    /** Indicates a boolean negation operator. */
    OpNot,
    /** Indicates a bitwise and operator. */
    OpAnd,
    /** Indicates a bitwise exclusive or operator. */
    OpXor,
    /** Indicates a raise to power operator. */
    OpPower,
    /** Indicates a bitwise or operator. */
    OpOr,
    /** Indicates a conditional and operator. */
    OpConditionalAnd,
    /** Indicates a conditional or operator. */
    OpConditionalOr,
    /** Indicates a member access operator. */
    OpDot,
    /** Indicates a reference operator. */
    OpReference,
    /** Indicates a comma. */
    Comma,
    /** Indicates a formatting operator. */
    OpFormat,
    /** Indicates a formatting string. */
    Format,
    /** Indicates a discarding symbol, that is, a symbol, which when specified as a format will prevent the expression from appearing in the final text. */
    Discard,
    /** Indicates a culture specifier. */
    OpCulture,
    /** Indicates a culture name. */
    Culture,
    /** Indicates a separator between expressions. */
    OpExpressionSeparator
}