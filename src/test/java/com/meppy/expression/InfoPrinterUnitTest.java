package com.meppy.expression;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public final class InfoPrinterUnitTest {
    private static final String EXPRESSION_SIMPLE = "s > avg + k * sd || s < avg - k * sd";

    private static ByteArrayOutputStream outputStream;
    private static PrintStream printStream;

    @BeforeClass
    public static void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test
    public void testPrintTokens() throws UnsupportedEncodingException {
        InfoPrinter.printTokens(printStream, EXPRESSION_SIMPLE, new CompileOptions());
        printStream.flush();
        String result = outputStream.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(result.length() > 0);
    }

    @Test
    public void testPrintParseTree() throws UnsupportedEncodingException {
        InfoPrinter.printParseTree(printStream, EXPRESSION_SIMPLE, new CompileOptions());
        printStream.flush();
        String result = outputStream.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(result.length() > 0);
    }

    @Test
    public void testPrintByteCode() throws UnsupportedEncodingException {
        InfoPrinter.printByteCode(printStream, EXPRESSION_SIMPLE, new CompileOptions());
        printStream.flush();
        String result = outputStream.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(result.length() > 0);
    }
}