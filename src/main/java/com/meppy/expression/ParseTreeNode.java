package com.meppy.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in a parse tree.
 */
final class ParseTreeNode {
    /**
     * The underlying token (if any) or null.
     */
    private Token token;

    /**
     * The child nodes.
     */
    private final List<ParseTreeNode> children;

    /**
     * Initializes a new instance of the {@link ParseTreeNode} class.
     */
    ParseTreeNode() {
        this(Token.empty());
    }

    /**
     * Initializes a new instance of the {@link ParseTreeNode} class.
     */
    ParseTreeNode(Token token) {
        this.token = token;
        this.children = new ArrayList<>();
    }

    /**
     * Gets the list with all children of this node.
     */
    List<ParseTreeNode> getChildren() {
        return children;
    }

    /**
     * Gets the underlying token.
     */
    Token getToken() {
        return token;
    }

    /**
     * Sets the underlying token.
     */
    void setToken(Token value) {
        token = value;
    }
}