package com.meppy.expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides parsing capabilities for token lists previously generated
 * by processing character sequences through a {@link Lexer}.
 */
final class Parser {
    /**
     * The list of tokens to parse.
     */
    private final List<Token> tokens;

    /**
     * The current parsing position.
     */
    private int current;

    /**
     * Initializes a new instance of the {@link Parser} class.
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the expression represented by the specified lexer and returns a {@link ParseTreeNode} representing
     * the root of the resulting parse tree, or <code>null</code>, if the expression could not be parsed.
     */
    public ParseTreeNode parse() {
        current = 0;
        return document();
    }

    /**
     * Parses a 'document' non-terminal.
     */
    private ParseTreeNode document() {
        int saved = current;

        ParseTreeNode root = new ParseTreeNode();

        ParseTreeNode node;
        while (current < tokens.size()) {
            if ((node = expression()) != null) {
                root.getChildren().add(node);
            } else if ((node = Text()) != null) {
                root.getChildren().add(node);
            } else {
                current = saved;
                return null;
            }
        }

        return root;
    }

    /**
     * Parses an 'expression' non-terminal.
     */
    private ParseTreeNode expression() {
        int saved = current;

        if (Osb() == null) {
            current = saved;
            return null;
        }

        ParseTreeNode node;
        if ((node = statementListExpression()) == null) {
            current = saved;
            return null;
        }

        if (Csb() == null) {
            current = saved;
            return null;
        }

        return node;
    }

    /**
     * Parses an 'statementListExpression' non-terminal.
     */
    private ParseTreeNode statementListExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = formatExpression()) == null) {
            current = saved;
            return null;
        }

        if (getCurrentToken().getType() == TokenType.OP_EXPRESSION_SEPARATOR) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpExpressionSeparator() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3 = statementListExpression();

            // node3 may be null when the separator is used at the end of an expression, e.g. [expression;]
            if (node3 != null) {
                node2.getChildren().add(node);
                node2.getChildren().add(node3);

                node = node2;
            }
        }

        return node;
    }

    /**
     * Parses a 'formatExpression' non-terminal.
     */
    private ParseTreeNode formatExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = assignmentExpression()) == null) {
            current = saved;
            return null;
        }

        // Look forward
        if (getCurrentToken().getType() == TokenType.OP_FORMAT) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpFormat() == null) {
                current = saved;
                return null;
            }

            boolean discard = false;
            ParseTreeNode node3;
            if ((node3 = Format()) == null) {
                discard = true;
                if ((node3 = Discard()) == null) {
                    current = saved;
                    return null;
                }
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            if (!discard) {
                // Do not attempt to parse culture if discard is specified instead of format
                if (OpCulture() != null) {
                    ParseTreeNode node4;
                    if ((node4 = Culture()) == null) {
                        current = saved;
                        return null;
                    }

                    node2.getChildren().add(node4);
                }
            }

            node = node2;
        }

        return node;
    }

    /**
     * Parses an 'assignmentListExpression' non-terminal.
     */
    private List<ParseTreeNode> assignmentListExpression() {
        int saved = current;

        List<ParseTreeNode> expressions = new ArrayList<>();

        ParseTreeNode node;
        if ((node = assignmentExpression()) == null) {
            return expressions;
        }

        expressions.add(node);

        while (getCurrentToken().getType() == TokenType.COMMA) {
            if (Comma() == null) {
                current = saved;
                return null;
            }

            if ((node = assignmentExpression()) == null) {
                current = saved;
                return null;
            }

            expressions.add(node);
        }

        return expressions;
    }

    /**
     * Parses an 'assignmentExpression' non-terminal.
     */
    private ParseTreeNode assignmentExpression() {
        // We've disabled assignments
        return conditionalOrExpression();
    }

    /**
     * Parses a 'conditionalOrExpression' non-terminal.
     */
    private ParseTreeNode conditionalOrExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = conditionalAndExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_CONDITIONAL_OR) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpConditionalOr() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = conditionalAndExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'conditionalAndExpression' non-terminal.
     */
    private ParseTreeNode conditionalAndExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = orExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_CONDITIONAL_AND) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpConditionalAnd() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = orExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses an 'orExpression' non-terminal.
     */
    private ParseTreeNode orExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = xorExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_OR) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpOr() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = xorExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'xorExpression' non-terminal.
     */
    private ParseTreeNode xorExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = andExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_XOR) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpXor() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = andExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'andExpression' non-terminal.
     */
    private ParseTreeNode andExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = equalityExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_AND) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpAnd() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = equalityExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'equalityExpression' non-terminal.
     */
    private ParseTreeNode equalityExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = relationalExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_EQUAL ||
            getCurrentToken().getType() == TokenType.OP_NOT_EQUAL) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpEqual() == null && OpNotEqual() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = relationalExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'relationalExpression' non-terminal.
     */
    private ParseTreeNode relationalExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = additiveExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_LESS ||
            getCurrentToken().getType() == TokenType.OP_LESS_OR_EQUAL ||
            getCurrentToken().getType() == TokenType.OP_GREATER ||
            getCurrentToken().getType() == TokenType.OP_GREATER_OR_EQUAL) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpLess() == null && OpLessOrEqual() == null && OpGreater() == null && OpGreaterOrEqual() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = additiveExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses an 'additiveExpression' non-terminal.
     */
    private ParseTreeNode additiveExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = multiplicativeExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_ADD ||
            getCurrentToken().getType() == TokenType.OP_SUBTRACT) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpAdd() == null && OpSubtract() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = multiplicativeExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'multiplicativeExpression' non-terminal.
     */
    private ParseTreeNode multiplicativeExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = unaryExpression()) == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_MULTIPLY ||
            getCurrentToken().getType() == TokenType.OP_DIVIDE ||
            getCurrentToken().getType() == TokenType.OP_MOD ||
            getCurrentToken().getType() == TokenType.OP_POWER) {
            ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());

            if (OpMultiply() == null && OpDivide() == null && OpMod() == null && OpPower() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = unaryExpression()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            node = node2;
        }

        return node;
    }

    /**
     * Parses a 'dotExpression' non-terminal.
     */
    private ParseTreeNode dotExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = Identifier()) == null) {
            current = saved;
            return null;
        }

        // Single identifier cannot pass as dereferencing
        if (getCurrentToken().getType() != TokenType.OP_DOT) {
            current = saved;
            return null;
        }

        // Use the first dot token as a parent of the entire sequence.
        // Dot operations are no longer nested
        ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());
        node2.getChildren().add(node);

        while (getCurrentToken().getType() == TokenType.OP_DOT) {
            if (OpDot() == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node3;
            if ((node3 = Identifier()) == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node3);
        }

        return node2;
    }

    /**
     * Parses a 'referenceExpression' non-terminal.
     */
    private ParseTreeNode referenceExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = dotExpression()) == null &&
            (node = Identifier()) == null) {
            current = saved;
            return null;
        }

        // Single identifier cannot pass as dereferencing
        if (getCurrentToken().getType() != TokenType.OP_REFERENCE) {
            current = saved;
            return null;
        }

        ParseTreeNode node2 = new ParseTreeNode(getCurrentToken());
        if (!node.getChildren().isEmpty()) {
            node2.getChildren().addAll(node.getChildren());
        } else {
            node2.getChildren().add(node);
        }

        if (OpReference() == null) {
            current = saved;
            return null;
        }

        ParseTreeNode node3;
        if ((node3 = dotExpression()) == null &&
            (node3 = Identifier()) == null) {
            current = saved;
            return null;
        }

        if (!node3.getChildren().isEmpty()) {
            node3.getChildren().add(0, node2);
            node = node3;
        } else {
            node = new ParseTreeNode(new Token(".", TokenType.OP_DOT));
            node.getChildren().add(node2);
            node.getChildren().add(node3);
        }

        return node;
    }

    /**
     * Parses an 'unaryExpression' non-terminal.
     */
    private ParseTreeNode unaryExpression() {
        int saved = current;

        ParseTreeNode node;

        // Number, followed by identifier is a quantity expression, e.g. 5mm or 1kg
        if ((node = FloatNumber()) != null) {
            if (getCurrentToken().getType() == TokenType.IDENTIFIER) {
                node.getChildren().add(Identifier());
            }

            return node;
        }

        if ((node = IntNumber()) != null) {
            if (getCurrentToken().getType() == TokenType.IDENTIFIER) {
                node.getChildren().add(Identifier());
            }

            return node;
        }

        if ((node = Null()) != null) {
            return node;
        }

        if ((node = String()) != null) {
            return node;
        }

        if ((node = Color()) != null) {
            return node;
        }

        if ((node = referenceExpression()) != null) {
            return node;
        }

        if ((node = dotExpression()) != null) {
            return node;
        }

        if ((node = Identifier()) != null) {
            // Might be a function
            if (getCurrentToken().getType() == TokenType.OB) {
                if (Ob() == null) {
                    current = saved;
                    return null;
                }

                List<ParseTreeNode> parameters;
                if ((parameters = paramList()) == null) {
                    current = saved;
                    return null;
                }

                node.getChildren().addAll(parameters);

                if (Cb() == null) {
                    current = saved;
                    return null;
                }

                node.setToken(node.getToken().withType(TokenType.FUNCTION_CALL));

                return node;
            }

            return node;
        }

        if (getCurrentToken().getType() == TokenType.OP_NOT) {
            if ((node = OpNot()) == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node2;
            if ((node2 = unaryExpression()) == null) {
                current = saved;
                return null;
            }

            node.getChildren().add(node2);

            return node;
        }

        if (getCurrentToken().getType() == TokenType.OP_SUBTRACT) {
            if ((node = OpSubtract()) == null) {
                current = saved;
                return null;
            }

            ParseTreeNode node2;
            if ((node2 = unaryExpression()) == null) {
                current = saved;
                return null;
            }

            node.getChildren().add(node2);

            return node;
        }

        if (getCurrentToken().getType() == TokenType.OB) {
            if (Ob() == null) {
                current = saved;
                return null;
            }

            if ((node = conditionalOrExpression()) == null) {
                current = saved;
                return null;
            }

            if (Cb() == null) {
                current = saved;
                return null;
            }

            return node;
        }

        current = saved;

        return null;
    }

    /**
     * Parses a 'paramList' non-terminal.
     */
    private List<ParseTreeNode> paramList() {
        int saved = current;

        if (getCurrentToken().getType() == TokenType.CB) {
            return new ArrayList<>();
        }

        List<ParseTreeNode> nodes;
        if ((nodes = assignmentListExpression()) == null) {
            current = saved;
            return null;
        }

        return nodes;
    }


    /**
     * Parses a terminal of the specified type at the current parsing position within the token list.
     */
    private ParseTreeNode ParseCurrent(TokenType type) {
        if (getCurrentToken().getType() == type) {
            ParseTreeNode node = new ParseTreeNode(getCurrentToken());
            current++;
            return node;
        }

        return null;
    }

    /**
     * Parses a 'Text' terminal.
     */
    private ParseTreeNode Text() {
        return ParseCurrent(TokenType.TEXT);
    }

    /**
     * Parses an 'Osb' terminal.
     */
    private ParseTreeNode Osb() {
        return ParseCurrent(TokenType.OSB);
    }

    /**
     * Parses a 'Csb' terminal.
     */
    private ParseTreeNode Csb() {
        return ParseCurrent(TokenType.CSB);
    }

    /**
     * Parses a 'Comma' terminal.
     */
    private ParseTreeNode Comma() {
        return ParseCurrent(TokenType.COMMA);
    }

    /**
     * Parses an 'Ob' terminal.
     */
    private ParseTreeNode Ob() {
        return ParseCurrent(TokenType.OB);
    }

    /**
     * Parses a 'Cb' terminal.
     */
    private ParseTreeNode Cb() {
        return ParseCurrent(TokenType.CB);
    }

    /**
     * Parses an 'OpAdd' terminal.
     */
    private ParseTreeNode OpAdd() {
        return ParseCurrent(TokenType.OP_ADD);
    }

    /**
     * Parses an 'OpSubtract' terminal.
     */
    private ParseTreeNode OpSubtract() {
        return ParseCurrent(TokenType.OP_SUBTRACT);
    }

    /**
     * Parses an 'OpNot' terminal.
     */
    private ParseTreeNode OpNot() {
        return ParseCurrent(TokenType.OP_NOT);
    }

    /**
     * Parses an 'OpMultiply' terminal.
     */
    private ParseTreeNode OpMultiply() {
        return ParseCurrent(TokenType.OP_MULTIPLY);
    }

    /**
     * Parses an 'OpDivide' terminal.
     */
    private ParseTreeNode OpDivide() {
        return ParseCurrent(TokenType.OP_DIVIDE);
    }

    /**
     * Parses an 'OpMod' terminal.
     */
    private ParseTreeNode OpMod() {
        return ParseCurrent(TokenType.OP_MOD);
    }

    /**
     * Parses an 'OpPower' terminal.
     */
    private ParseTreeNode OpPower() {
        return ParseCurrent(TokenType.OP_POWER);
    }

    /**
     * Parses an 'OpLess' terminal.
     */
    private ParseTreeNode OpLess() {
        return ParseCurrent(TokenType.OP_LESS);
    }

    /**
     * Parses an 'OpLessOrEqual' terminal.
     */
    private ParseTreeNode OpLessOrEqual() {
        return ParseCurrent(TokenType.OP_LESS_OR_EQUAL);
    }

    /**
     * Parses an 'OpGreater' terminal.
     */
    private ParseTreeNode OpGreater() {
        return ParseCurrent(TokenType.OP_GREATER);
    }

    /**
     * Parses an 'OpGreaterOrEqual' terminal.
     */
    private ParseTreeNode OpGreaterOrEqual() {
        return ParseCurrent(TokenType.OP_GREATER_OR_EQUAL);
    }

    /**
     * Parses an 'OpEqual' terminal.
     */
    private ParseTreeNode OpEqual() {
        return ParseCurrent(TokenType.OP_EQUAL);
    }

    /**
     * Parses an 'OpNotEqual' terminal.
     */
    private ParseTreeNode OpNotEqual() {
        return ParseCurrent(TokenType.OP_NOT_EQUAL);
    }

    /**
     * Parses an 'OpAnd' terminal.
     */
    private ParseTreeNode OpAnd() {
        return ParseCurrent(TokenType.OP_AND);
    }

    /**
     * Parses an 'OpOr' terminal.
     */
    private ParseTreeNode OpOr() {
        return ParseCurrent(TokenType.OP_OR);
    }

    /**
     * Parses an 'OpXor' terminal.
     */
    private ParseTreeNode OpXor() {
        return ParseCurrent(TokenType.OP_XOR);
    }

    /**
     * Parses an 'OpConditionalAnd' terminal.
     */
    private ParseTreeNode OpConditionalAnd() {
        return ParseCurrent(TokenType.OP_CONDITIONAL_AND);
    }

    /**
     * Parses an 'OpConditionalOr' terminal.
     */
    private ParseTreeNode OpConditionalOr() {
        return ParseCurrent(TokenType.OP_CONDITIONAL_OR);
    }

    /**
     * Parses an 'OpDot' terminal.
     */
    private ParseTreeNode OpDot() {
        return ParseCurrent(TokenType.OP_DOT);
    }

    private ParseTreeNode OpReference() {
        return ParseCurrent(TokenType.OP_REFERENCE);
    }

    /**
     * Parses an 'OpFormat' terminal.
     */
    private ParseTreeNode OpFormat() {
        return ParseCurrent(TokenType.OP_FORMAT);
    }

    /**
     * Parses an 'OpExpressionSeparator' terminal.
     */
    private ParseTreeNode OpExpressionSeparator() {
        return ParseCurrent(TokenType.OP_EXPRESSION_SEPARATOR);
    }

    /**
     * Parses an 'OpCulture' terminal.
     */
    private ParseTreeNode OpCulture() {
        return ParseCurrent(TokenType.OP_CULTURE);
    }

    /**
     * Parses a 'Format' terminal.
     */
    private ParseTreeNode Format() {
        return ParseCurrent(TokenType.FORMAT);
    }

    /**
     * Parses a 'Discard' terminal.
     */
    private ParseTreeNode Discard() {
        return ParseCurrent(TokenType.DISCARD);
    }

    /**
     * Parses a 'Culture' terminal.
     */
    private ParseTreeNode Culture() {
        return ParseCurrent(TokenType.CULTURE);
    }

    /**
     * Parses an 'Identifier' terminal.
     */
    private ParseTreeNode Identifier() {
        return ParseCurrent(TokenType.IDENTIFIER);
    }

    /**
     * Parses an 'IntNumber' terminal.
     */
    private ParseTreeNode IntNumber() {
        return ParseCurrent(TokenType.INT_NUMBER);
    }

    /**
     * Parses a 'String' terminal.
     */
    private ParseTreeNode String() {
        return ParseCurrent(TokenType.STRING);
    }

    /**
     * Parses a 'null' keyword.
     */
    private ParseTreeNode Null() {
        return ParseCurrent(TokenType.NULL);
    }

    /**
     * Parses a 'FloatNumber' terminal.
     */
    private ParseTreeNode FloatNumber() {
        return ParseCurrent(TokenType.FLOAT_NUMBER);
    }

    /**
     * Parses a 'Color' terminal.
     */
    private ParseTreeNode Color() {
        return ParseCurrent(TokenType.COLOR);
    }

    /**
     * Gets the current token.
     */
    private Token getCurrentToken() {
        return tokens.get(current);
    }
}