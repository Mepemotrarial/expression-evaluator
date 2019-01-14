package com.meppy.expressions;

/**
 * Provides methods for compiling expressions to byte code.
 */
public final class Compiler {
    private Compiler() {
    }

    /**
     * Returns a {@link ByteCode} object corresponding to the specified source code.
     * The returned byte code can be subsequently evaluated by calling its {@link ByteCode#evaluate} method.
     * @param source The script to compile.
     * @return The compiled script.
     */
    public static ByteCode compile(String source) {
        return compile(source, true);
    }

    /**
     * Returns a {@link ByteCode} object corresponding to the specified source code.
     * The returned byte code can be subsequently evaluated by calling its {@link ByteCode#evaluate} method.
     * @param source The script to compile.
     * @param normalize <code>true</code> to encase source in square brackets if it is not encased already; otherwise, <code>false</code>.
     * @return The compiled script.
     */
    public static ByteCode compile(String source, boolean normalize) {
        return compile(source, new CompileOptions(normalize));
    }

    /**
     * Returns a {@link ByteCode} object corresponding to the specified source code.
     * The returned byte code can be subsequently evaluated by calling its evaluate method.
     * @param source The script to compile.
     * @param options The compilation options.
     * @return The compiled script.
     */
    public static ByteCode compile(String source, CompileOptions options) {
        String expression = options.getNormalize() ? normalize(source) : source;

        Lexer lexer = new Lexer(expression, options);
        Parser parser = new Parser(lexer.getTokens());
        return new ByteCode(parser.parse());
    }

    private static String normalize(String expression) {
        if (expression == null) {
            return "";
        }

        String result = expression.trim();
        if (result.isEmpty()) {
            return "";
        }

        if (result.charAt(0) != '[') {
            result = "[" + result;
        }
        if (result.charAt(result.length() - 1) != ']') {
            result = result + "]";
        }

        return result;
    }
}