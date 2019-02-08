package com.meppy.expression;

import java.io.PrintStream;
import java.util.List;

public final class InfoPrinter {
    private InfoPrinter() {
    }

    public static void printTokens(PrintStream printStream, String expression, CompileOptions options) {
        Lexer lexer = new Lexer(options);
        List<Token> tokens = lexer.tokenize(Compiler.normalize(expression));
        tokens.forEach(printStream::println);
    }

    public static void printParseTree(PrintStream printStream, String expression, CompileOptions options) {
        Lexer lexer = new Lexer(options);
        Parser parser = new Parser(lexer.tokenize(Compiler.normalize(expression)));
        ParseTreeNode node = parser.parse();
        node.accept(new ParseTreeVisitor() {
            private static final int INDENT = 4;
            private int indentLevel = -1;

            @Override
            public void enterVisit(ParseTreeNode node) {
                indentLevel++;
                printStream.print(StringUtils.newString(' ', indentLevel * INDENT));
                if (node.getToken().getType() == TokenType.EMPTY) {
                    printStream.println("<ROOT>");
                } else {
                    printStream.println(node.getToken().toString());
                }
            }

            @Override
            public void leaveVisit(ParseTreeNode node) {
                indentLevel--;
            }
        });
    }

    public static void printByteCode(PrintStream stream, String expression, CompileOptions options) {
        ByteCode code = Compiler.compile(expression, options);
        for (int i = 0; i < code.getLength(); i++) {
            stream.println(code.get(i).toString());
        }
    }
}