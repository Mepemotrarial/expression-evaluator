package com.meppy.expression;

/**
 * Provides methods for compiling expressions to byte code.
 */
public final class Compiler {
    private Compiler() {
    }

    static String normalize(String expression) {
        if (expression == null) {
            return "";
        }

        boolean hasBrackets = expression.indexOf('[') != -1;
        if (hasBrackets) {
            return expression;
        }

        return "[" + expression.trim() + "]";
    }

    /**
     * Returns a {@link ByteCode} object corresponding to the specified source code.
     * The returned byte code can be subsequently evaluated by calling its {@link ByteCode#evaluate} method.
     * @param source The script to compile.
     * @return The compiled script.
     */
    public static ByteCode compile(String source) {
        return compile(source, new CompileOptions());
    }

    /**
     * Returns a {@link ByteCode} object corresponding to the specified source code.
     * The returned byte code can be subsequently evaluated by calling its evaluate method.
     * @param source The script to compile.
     * @param options The compilation options.
     * @return The compiled script.
     */
    public static ByteCode compile(String source, CompileOptions options) {
        String expression = normalize(source);

        Lexer lexer = new Lexer(options);
        Parser parser = new Parser(lexer.tokenize(expression));
        return new ByteCode(parser.parse());
    }
}