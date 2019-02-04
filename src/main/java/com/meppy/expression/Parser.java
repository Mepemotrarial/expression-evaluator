package com.meppy.expression;

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
        return nDocument();
    }

    /**
     * Parses a 'document' non-terminal.
     */
    private ParseTreeNode nDocument() {
        int saved = current;

        ParseTreeNode root = new ParseTreeNode();

        ParseTreeNode node;
        while (current < tokens.size()) {
            if ((node = nExpression()) != null) {
                root.getChildren().add(node);
            } else if ((node = tText()) != null) {
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
    private ParseTreeNode nExpression() {
        int saved = current;

        if (tOsb() == null) {
            current = saved;
            return null;
        }

        ParseTreeNode node = nStatementListExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        if (tCsb() == null) {
            current = saved;
            return null;
        }

        return node;
    }

    /**
     * Parses an 'statementListExpression' non-terminal.
     */
    private ParseTreeNode nStatementListExpression() {
        int saved = current;

        ParseTreeNode node = nFormatExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        if (getCurrentToken().getType() == TokenType.OP_EXPRESSION_SEPARATOR) {
            ParseTreeNode node2 = tCurrent();
            ParseTreeNode node3 = nStatementListExpression();

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
    private ParseTreeNode nFormatExpression() {
        int saved = current;

        ParseTreeNode node = nAssignmentExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        // Look forward
        if (getCurrentToken().getType() == TokenType.OP_FORMAT) {
            ParseTreeNode node2 = tCurrent();

            boolean discard = false;
            ParseTreeNode node3;
            if ((node3 = tFormat()) == null) {
                discard = true;
                if ((node3 = tDiscard()) == null) {
                    current = saved;
                    return null;
                }
            }

            node2.getChildren().add(node);
            node2.getChildren().add(node3);

            if (!discard && tOpCulture() != null) {
                // Do not attempt to parse culture if discard is specified instead of format
                ParseTreeNode node4 = tCulture();
                if (node4 == null) {
                    current = saved;
                    return null;
                }

                node2.getChildren().add(node4);
            }

            node = node2;
        }

        return node;
    }

    /**
     * Parses an 'assignmentListExpression' non-terminal.
     */
    private List<ParseTreeNode> nAssignmentListExpression() {
        int saved = current;

        List<ParseTreeNode> expressions = new ArrayList<>();

        ParseTreeNode node = nAssignmentExpression();
        if (node == null) {
            return expressions;
        }

        expressions.add(node);

        while (getCurrentToken().getType() == TokenType.COMMA) {
            tCurrent();

            node = nAssignmentExpression();
            if (node == null) {
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
    private ParseTreeNode nAssignmentExpression() {
        // We've disabled assignments
        return nConditionalOrExpression();
    }

    /**
     * Parses a 'conditionalOrExpression' non-terminal.
     */
    private ParseTreeNode nConditionalOrExpression() {
        int saved = current;

        ParseTreeNode node = nConditionalAndExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_CONDITIONAL_OR) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nConditionalAndExpression();
            if (node3 == null) {
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
    private ParseTreeNode nConditionalAndExpression() {
        int saved = current;

        ParseTreeNode node = nOrExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_CONDITIONAL_AND) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nOrExpression();
            if (node3 == null) {
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
    private ParseTreeNode nOrExpression() {
        int saved = current;

        ParseTreeNode node = nXorExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_OR) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nXorExpression();
            if (node3 == null) {
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
    private ParseTreeNode nXorExpression() {
        int saved = current;

        ParseTreeNode node = nAndExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_XOR) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nAndExpression();
            if (node3 == null) {
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
    private ParseTreeNode nAndExpression() {
        int saved = current;

        ParseTreeNode node = nEqualityExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_AND) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nEqualityExpression();
            if (node3 == null) {
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
    private ParseTreeNode nEqualityExpression() {
        int saved = current;

        ParseTreeNode node = nRelationalExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_EQUAL ||
            getCurrentToken().getType() == TokenType.OP_NOT_EQUAL) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nRelationalExpression();
            if (node3 == null) {
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
    private ParseTreeNode nRelationalExpression() {
        int saved = current;

        ParseTreeNode node = nAdditiveExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_LESS ||
            getCurrentToken().getType() == TokenType.OP_LESS_OR_EQUAL ||
            getCurrentToken().getType() == TokenType.OP_GREATER ||
            getCurrentToken().getType() == TokenType.OP_GREATER_OR_EQUAL) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nAdditiveExpression();
            if (node3 == null) {
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
    private ParseTreeNode nAdditiveExpression() {
        int saved = current;

        ParseTreeNode node = nMultiplicativeExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_ADD ||
            getCurrentToken().getType() == TokenType.OP_SUBTRACT) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nMultiplicativeExpression();
            if (node3 == null) {
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
    private ParseTreeNode nMultiplicativeExpression() {
        int saved = current;

        ParseTreeNode node = nUnaryExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        while (getCurrentToken().getType() == TokenType.OP_MULTIPLY ||
            getCurrentToken().getType() == TokenType.OP_DIVIDE ||
            getCurrentToken().getType() == TokenType.OP_MOD ||
            getCurrentToken().getType() == TokenType.OP_POWER) {
            ParseTreeNode node2 = tCurrent();

            ParseTreeNode node3 = nUnaryExpression();
            if (node3 == null) {
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
    private ParseTreeNode nDotExpression() {
        int saved = current;

        ParseTreeNode node;
        if ((node = nFunctionCall()) == null &&
            (node = tIdentifier()) == null) {
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
            tCurrent();

            ParseTreeNode node3 = tIdentifier();
            if (node3 == null) {
                current = saved;
                return null;
            }

            node2.getChildren().add(node3);
        }

        return node2;
    }

    /**
     * Parses an 'unaryExpression' non-terminal.
     */
    private ParseTreeNode nUnaryExpression() {
        int saved = current;

        // Number, followed by identifier is a quantity expression, e.g. 5mm or 1kg
        ParseTreeNode node;
        if ((node = tFloatNumber()) != null) {
            if (getCurrentToken().getType() == TokenType.IDENTIFIER) {
                node.getChildren().add(tIdentifier());
            }

            return node;
        }

        if ((node = tIntNumber()) != null) {
            if (getCurrentToken().getType() == TokenType.IDENTIFIER) {
                node.getChildren().add(tIdentifier());
            }

            return node;
        }

        if ((node = tNull()) != null) {
            return node;
        }

        if ((node = tString()) != null) {
            return node;
        }

        if ((node = tColor()) != null) {
            return node;
        }

        if ((node = nDotExpression()) != null) {
            return node;
        }

        if ((node = nFunctionCall()) != null) {
            return node;
        }

        if ((node = nNotExpression()) != null) {
            return node;
        }

        if ((node = nUnaryMinusExpression()) != null) {
            return node;
        }

        if ((node = nParenthesizedExpression()) != null) {
            return node;
        }

        current = saved;

        return null;
    }

    private ParseTreeNode nNotExpression() {
        int saved = current;

        if (getCurrentToken().getType() == TokenType.OP_NOT) {
            ParseTreeNode node = tCurrent();

            ParseTreeNode node2 = nUnaryExpression();
            if (node2 == null) {
                current = saved;
                return null;
            }

            node.getChildren().add(node2);

            return node;
        }

        current = saved;

        return null;
    }

    private ParseTreeNode nUnaryMinusExpression() {
        int saved = current;

        if (getCurrentToken().getType() == TokenType.OP_SUBTRACT) {
            ParseTreeNode node = tCurrent();

            ParseTreeNode node2 = nUnaryExpression();
            if (node2 == null) {
                current = saved;
                return null;
            }

            node.getChildren().add(node2);

            return node;
        }

        current = saved;

        return null;
    }

    private ParseTreeNode nParenthesizedExpression() {
        int saved = current;

        if (tOb() == null) {
            return null;
        }

        ParseTreeNode node = nConditionalOrExpression();
        if (node == null) {
            current = saved;
            return null;
        }

        if (tCb() == null) {
            current = saved;
            return null;
        }

        return node;
    }

    private ParseTreeNode nFunctionCall() {
        int saved = current;

        ParseTreeNode node = tIdentifier();
        if (node == null) {
            current = saved;
            return null;
        }

        if (getCurrentToken().getType() != TokenType.OB) {
            return node;
        }

        tOb();

        List<ParseTreeNode> parameters;
        if ((parameters = nParamList()) == null) {
            current = saved;
            return null;
        }

        node.getChildren().addAll(parameters);

        if (tCb() == null) {
            current = saved;
            return null;
        }

        node.setToken(node.getToken().withType(TokenType.FUNCTION_CALL));

        return node;
    }

    /**
     * Parses a 'paramList' non-terminal.
     */
    private List<ParseTreeNode> nParamList() {
        int saved = current;

        if (getCurrentToken().getType() == TokenType.CB) {
            return new ArrayList<>();
        }

        List<ParseTreeNode> nodes;
        if ((nodes = nAssignmentListExpression()) == null) {
            current = saved;
            return null;
        }

        return nodes;
    }


    /**
     * Parses a terminal at the current parsing position within the token list.
     */
    private ParseTreeNode tCurrent() {
        ParseTreeNode node = new ParseTreeNode(getCurrentToken());
        current++;
        return node;
    }

    /**
     * Parses a terminal of the specified type at the current parsing position within the token list.
     */
    private ParseTreeNode tParseCurrent(TokenType type) {
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
    private ParseTreeNode tText() {
        return tParseCurrent(TokenType.TEXT);
    }

    /**
     * Parses an 'Osb' terminal.
     */
    private ParseTreeNode tOsb() {
        return tParseCurrent(TokenType.OSB);
    }

    /**
     * Parses a 'Csb' terminal.
     */
    private ParseTreeNode tCsb() {
        return tParseCurrent(TokenType.CSB);
    }

    /**
     * Parses an 'Ob' terminal.
     */
    private ParseTreeNode tOb() {
        return tParseCurrent(TokenType.OB);
    }

    /**
     * Parses a 'Cb' terminal.
     */
    private ParseTreeNode tCb() {
        return tParseCurrent(TokenType.CB);
    }

    /**
     * Parses an 'OpCulture' terminal.
     */
    private ParseTreeNode tOpCulture() {
        return tParseCurrent(TokenType.OP_CULTURE);
    }

    /**
     * Parses a 'Format' terminal.
     */
    private ParseTreeNode tFormat() {
        return tParseCurrent(TokenType.FORMAT);
    }

    /**
     * Parses a 'Discard' terminal.
     */
    private ParseTreeNode tDiscard() {
        return tParseCurrent(TokenType.DISCARD);
    }

    /**
     * Parses a 'Culture' terminal.
     */
    private ParseTreeNode tCulture() {
        return tParseCurrent(TokenType.CULTURE);
    }

    /**
     * Parses an 'Identifier' terminal.
     */
    private ParseTreeNode tIdentifier() {
        return tParseCurrent(TokenType.IDENTIFIER);
    }

    /**
     * Parses an 'IntNumber' terminal.
     */
    private ParseTreeNode tIntNumber() {
        return tParseCurrent(TokenType.INT_NUMBER);
    }

    /**
     * Parses a 'String' terminal.
     */
    private ParseTreeNode tString() {
        return tParseCurrent(TokenType.STRING);
    }

    /**
     * Parses a 'null' keyword.
     */
    private ParseTreeNode tNull() {
        return tParseCurrent(TokenType.NULL);
    }

    /**
     * Parses a 'FloatNumber' terminal.
     */
    private ParseTreeNode tFloatNumber() {
        return tParseCurrent(TokenType.FLOAT_NUMBER);
    }

    /**
     * Parses a 'Color' terminal.
     */
    private ParseTreeNode tColor() {
        return tParseCurrent(TokenType.COLOR);
    }

    /**
     * Gets the current token.
     */
    private Token getCurrentToken() {
        if (current >= tokens.size()) {
            throw new ParsingException("Unexpected end of the token stream. The expression is most likely incomplete.");
        }

        return tokens.get(current);
    }
}