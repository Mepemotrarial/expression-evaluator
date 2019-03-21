package com.meppy.expression;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public final class ExpressionUnitTest {
    private static final double EPSILON = 1.0E-6;
    private static EvaluationContext evaluationContextDefault;
    private static EvaluationContext evaluationContextCustom;
    private static EvaluationContext evaluationContextCustomWithCallbacks;
    private static EvaluationContext evaluationContextCustomWithTarget;
    private static CompileOptions compileOptions;
    private static CompileOptions compileOptions1;

    @BeforeClass
    public static void setUp() {
        evaluationContextDefault = new EvaluationContext(null);
        evaluationContextCustom = new CustomContext();
        evaluationContextCustomWithCallbacks = new EvaluationContext(null, Locale.ROOT,
            (name, params) -> {
                if (name.equals("customFunction")) {
                    return new FunctionEvaluationResult("result");
                }

                return FunctionEvaluationResult.notEvaluated();
            },
            value -> {
                if (value.length() == 7) {
                    return new FunctionEvaluationResult(Color.decode(value));
                }

                return FunctionEvaluationResult.notEvaluated();
            });
        evaluationContextCustomWithTarget = new EvaluationContext(new CustomObject(new Point(5, 10)));
        compileOptions = new CompileOptions();
        compileOptions1 = new CompileOptions(true);
    }

    private static class CustomObject {
        private final Point point;
        public CustomObject(Point point) {
            this.point = point;
        }
        public Point getPoint() {
            return point;
        }
    }

    private static class CustomContext extends EvaluationContext {
        public CustomContext() {
            super(null);
        }

        @Override
        public Object invokeFunction(String name, Object... p) {
            switch (name) {
                case "byte":
                    return ((Integer)p[0]).byteValue();
                case "short":
                    return ((Integer)p[0]).shortValue();
                case "long":
                    return ((Integer)p[0]).longValue();
                case "float":
                    return ((Integer)p[0]).floatValue();
                case "factorial":
                    return factorial((Integer)p[0]);
                case "getPoint":
                    return new Point.Double(4.5, 5.5);
            }

            return super.invokeFunction(name, p);
        }

        @Override
        protected double evaluateQuantity(double value, String unit) {
            if (unit.equals("pt")) {
                return value * 96 / 72;
            }
            return super.evaluateQuantity(value, unit);
        }

        @Override
        protected Object resolveObject(String name) {
            if (name.equals("myObject")) {
                return new Point(1, 2);
            }
            return super.resolveObject(name);
        }

        private static int factorial(int v) {
            if (v <= 1) {
                return v;
            }
            return v * factorial(v - 1);
        }
    }

    @Test
    public void testSeparator() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.SEPARATOR);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.POWER, compileOptions1);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(8.0, result);
    }

    @Test
    public void testFunctionsNow() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_NOW, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Date.class, result.getClass());
        Assert.assertTrue(Math.abs(((Date)result).getTime() - new Date().getTime()) < 1000);
    }

    @Test
    public void testFunctionsE() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_E, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(Math.E, result);
    }

    @Test
    public void testFunctionsPi() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_PI, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(Math.PI, result);
    }

    @Test
    public void testFunctionsToday() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_TODAY, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Date.class, result.getClass());
    }

    @Test
    public void testFunctionsAbs() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ABS, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(1.2, result);
    }

    @Test
    public void testFunctionsAsc() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ASC, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals((int)'t', result);
    }

    @Test
    public void testFunctionsAscNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ASC_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(-1, result);
    }

    @Test
    public void testFunctionsAtn() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ATN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(Math.atan(0.5), result);
    }

    @Test
    public void testFunctionsChr() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CHR, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("d", result);
    }

    @Test
    public void testFunctionsCbool1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CBOOL1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testFunctionsCbool2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CBOOL2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testFunctionsCbool3() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CBOOL3, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testFunctionsCdate1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CDATE1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(new Date(2000000000), result);
    }

    @Test
    public void testFunctionsCdate2() throws InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.ROOT);
        Date now = new Date();
        String nowAsString = format.format(now);
        ByteCode code = Compiler.compile("[cdate(\"" + nowAsString + "\")]", compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(format.parse(nowAsString), result);
    }

    @Test
    public void testFunctionsCdate3() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CDATE3, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Date.class, result.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionsCdateInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CDATE_INVALID, compileOptions);
        code.evaluate(evaluationContextDefault);
    }

    @Test
    public void testFunctionsCdbl1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CDBL1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(25.1E2, result);
    }

    @Test
    public void testFunctionsCdbl2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CDBL2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0.0, result);
    }

    @Test
    public void testFunctionsCint1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CINT1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(2, result);
    }

    @Test
    public void testFunctionsCint2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CINT2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testFunctionsClong1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CLONG1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(4294967296L, result);
    }

    @Test
    public void testFunctionsClong2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CLONG2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0L, result);
    }

    @Test
    public void testFunctionsCsng1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CSNG1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(-5.5f, result);
    }

    @Test
    public void testFunctionsCsng2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CSNG2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0f, result);
    }

    @Test
    public void testFunctionsCstr1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CSTR1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("-5.5", result);
    }

    @Test
    public void testFunctionsCstr2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CSTR2, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("-5", result);
    }

    @Test
    public void testFunctionsCstr3() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_CSTR3, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(String.class, result.getClass());
        Assert.assertTrue(((String)result).length() > 0);
    }

    @Test
    public void testFunctionsCosAndAcos() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_COS_ACOS, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.PI / 3, (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsExp() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_EXP, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.exp(10), (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsInt() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INT, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testFunctionsIsNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ISNULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testFunctionsIsNumeric() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ISNUMERIC, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testFunctionsIsNumericDate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ISNUMERIC_DATE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testFunctionsIsNumericNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ISNUMERIC_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testFunctionsLcase() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_LCASE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("mixed", result);
    }

    @Test
    public void testFunctionsLen() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_LEN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(11, result);
    }

    @Test
    public void testFunctionsLog() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_LOG, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.log(10), (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsPow() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_POW, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.pow(3, 2), (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsRnd1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_RND1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(new Random(-1).nextDouble(), (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsRnd2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_RND2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
    }

    @Test
    public void testFunctionsRnd3() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_RND3, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
    }

    @Test
    public void testFunctionsSgn() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_SGN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(-1, (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsSinAndAsin() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_SIN_ASIN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.PI / 2, (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsSpace() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_SPACE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(StringUtils.newString(' ', 4), result);
    }

    @Test
    public void testFunctionsSqrAndSqrt() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_SQR_SQRT, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(1.0, (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsStr() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ROOT);
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_STR, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(numberFormat.format(1.234), result);
    }

    @Test
    public void testFunctionsStrReverse() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_STRREVERSE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("was I tac a ti saw", result);
    }

    @Test
    public void testFunctionsTan() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_TAN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(1.0, (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsTrim() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_TRIM, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("", result);
    }

    @Test
    public void testFunctionsUcase() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_UCASE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("MIXED", result);
    }

    @Test
    public void testFunctionsTypeOf() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_TYPEOF, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(Integer.class, result);
    }

    @Test
    public void testFunctionsTypeOfNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_TYPEOF_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNull(result);
    }

    @Test
    public void testFunctionsRound() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_ROUND, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.class, result.getClass());
        Assert.assertEquals(Math.round(1.5), (Double)result, EPSILON);
    }

    @Test
    public void testFunctionsInStr() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INSTR, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testFunctionsInStrNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INSTR_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(-1, result);
    }

    @Test
    public void testFunctionsInStrRev() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INSTRREV, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(13, result);
    }

    @Test
    public void testFunctionsInStrRevNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INSTRREV_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(-1, result);
    }

    @Test
    public void testFunctionsLeft() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_LEFT, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("Conjunction", result);
    }

    @Test
    public void testFunctionsRight() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_RIGHT, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("disjunction", result);
    }

    @Test
    public void testFunctionsStrComp() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_STRCOMP, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("first".compareTo("second"), result);
    }

    @Test
    public void testFunctionsString() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_STRING, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("ddddd", result);
    }

    @Test
    public void testFunctionsMin() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_MIN, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(-2.0, result);
    }

    @Test
    public void testFunctionsMax() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_MAX, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(1.0, result);
    }

    @Test
    public void testFunctionsIif() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_IIF, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("yes", result);
    }

    @Test
    public void testFunctionsMid1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_MID1, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("visible", result);
    }

    @Test
    public void testFunctionsMid2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_MID2, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("", result);
    }

    @Test
    public void testFunctionsReplace() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_REPLACE, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("I wasn't here", result);
    }

    @Test
    public void testFunctionsReplaceNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_REPLACE_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals("", result);
    }

    @Test(expected = EvaluationException.class)
    public void testFunctionsInsufficientParams() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_INSUFFICIENT_PARAMS, compileOptions);
        code.evaluate(evaluationContextDefault);
    }

    @Test
    public void testFunctionsExcessParams() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_EXCESS_PARAMS, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertEquals(2.0, result);
    }

    @Test(expected = EvaluationException.class)
    public void testFunctionsNotDefined() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTIONS_NOT_DEFINED, compileOptions);
        code.evaluate(evaluationContextDefault);
    }

    @Test
    public void testOpUnary() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_UNARY, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testOpByteBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BYTE_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5, result);
    }

    @Test
    public void testOpByteBitwise() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BYTE_BITWISE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32, result);
    }

    @Test
    public void testOpByteBitwiseWithPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BYTE_BITWISE, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(57.0, result);
    }

    @Test
    public void testOpByteComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BYTE_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpByteInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BYTE_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpShortBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_SHORT_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5, result);
    }

    @Test
    public void testOpShortBitwise() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_SHORT_BITWISE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32, result);
    }

    @Test
    public void testOpShortBitwiseWithPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_SHORT_BITWISE, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(57.0, result);
    }

    @Test
    public void testOpShortComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_SHORT_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpShortInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_SHORT_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpIntegerBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_INTEGER_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5, result);
    }

    @Test
    public void testOpIntegerBitwise() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_INTEGER_BITWISE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32, result);
    }

    @Test
    public void testOpIntegerBitwiseWithPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_INTEGER_BITWISE, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(57.0, result);
    }

    @Test
    public void testOpIntegerComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_INTEGER_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpIntegerInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_INTEGER_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpLongBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_LONG_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5L, result);
    }

    @Test
    public void testOpLongBitwise() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_LONG_BITWISE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32L, result);
    }

    @Test
    public void testOpLongBitwiseWithPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_LONG_BITWISE, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(57.0, result);
    }

    @Test
    public void testOpLongComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_LONG_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpLongInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_LONG_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpFloatBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_FLOAT_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5.5f, result);
    }

    @Test
    public void testOpFloatPower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_FLOAT_POWER, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32.0, result);
    }

    @Test
    public void testOpFloatComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_FLOAT_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpFloatInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_FLOAT_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpDoubleBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DOUBLE_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(5.5, result);
    }

    @Test
    public void testOpDoublePower() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DOUBLE_POWER, compileOptions1);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(32.0, result);
    }

    @Test
    public void testOpDoubleComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DOUBLE_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpDoubleInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DOUBLE_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpBooleanComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BOOLEAN_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpBooleanInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_BOOLEAN_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpDateBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DATE_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNotNull(result);
    }

    @Test
    public void testOpDateComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DATE_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNotNull(result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpDateInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DATE_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test(expected = EvaluationException.class)
    public void testOpDateUnary() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DATE_UNARY, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpStringBasic() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_STRING_BASIC, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("concatenation", result);
    }

    @Test
    public void testOpStringComparison() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_STRING_COMPARISON, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test(expected = EvaluationException.class)
    public void testOpStringInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_STRING_INVALID, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testPromoteBoolean() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_BOOLEAN, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("25.0false", result);
    }

    @Test(expected = EvaluationException.class)
    public void testPromoteBooleanToDate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_BOOLEAN_TO_DATE, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test(expected = EvaluationException.class)
    public void testPromoteBooleanToAny() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_BOOLEAN_TO_ANY, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testPromoteDate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_DATE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.toString().startsWith("Now: "));
    }

    @Test
    public void testOpPromoteToShort() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_TO_SHORT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testOpPromoteToInt() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_TO_INT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(6, result);
    }

    @Test
    public void testOpPromoteToLong() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_TO_LONG, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(10L, result);
    }

    @Test
    public void testOpPromoteToFloat() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_TO_FLOAT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(15F, result);
    }

    @Test
    public void testOpPromoteToDouble() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_PROMOTE_TO_DOUBLE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(21.0, result);
    }

    @Test(expected = ArithmeticException.class)
    public void testOpDivisionByZero() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_DIVISION_BY_ZERO, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testOpNullEquality() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_NULL_EQUALITY, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testOpNullInequality() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_NULL_INEQUALITY, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testOpNullInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_NULL_INVALID, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNull(result);
    }

    @Test
    public void testOpNullUnaryMinus() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.OP_NULL_UNARY_MINUS, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNull(result);
    }

    @Test
    public void testCustomFunction() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.CUSTOM_FUNCTION, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("Factorial of 10 is 3628800", result);
    }

    @Test
    public void testVariablesAndMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.VARIABLES_AND_MEMBER_REFERENCE, compileOptions);
        evaluationContextCustom.getLocalVars().put("a", new Point(5, 10));
        evaluationContextCustom.getLocalVars().put("b", new Point(8, 6));
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("The distance between points java.awt.Point[x=5,y=10] and java.awt.Point[x=8,y=6] is 5.0.", result);
    }

    @Test
    public void testTripleMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.TRIPLE_MEMBER_REFERENCE, compileOptions);
        evaluationContextCustom.getLocalVars().put("code", code);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(code.getIdentifiers().getClass(), result);
    }

    @Test
    public void testPrivateMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.PRIVATE_MEMBER_REFERENCE, compileOptions);
        evaluationContextCustom.getLocalVars().put("code", code);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(6, result);
    }

    @Test(expected = EvaluationException.class)
    public void testInvalidMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.INVALID_MEMBER_REFERENCE, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test(expected = EvaluationException.class)
    public void testInvalidMemberReference2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.INVALID_MEMBER_REFERENCE2, compileOptions);
        evaluationContextCustom.getLocalVars().put("code", code);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testFunctionMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTION_MEMBER_REFERENCE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(4.5, result);
    }

    @Test(expected = EvaluationException.class)
    public void testFunctionInvalidMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FUNCTION_INVALID_MEMBER_REFERENCE, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testTargetMemberReference() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.TARGET_MEMBER_REFERENCE, compileOptions);
        Object result = code.evaluate(evaluationContextCustomWithTarget);
        Assert.assertEquals(15.0, result);
    }

    @Test(expected = EvaluationException.class)
    public void testTargetMemberInvalidReference1() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.TARGET_MEMBER_INVALID_REFERENCE_1, compileOptions);
        code.evaluate(evaluationContextCustomWithTarget);
    }

    @Test(expected = EvaluationException.class)
    public void testTargetMemberInvalidReference2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.TARGET_MEMBER_INVALID_REFERENCE_2, compileOptions);
        code.evaluate(evaluationContextCustomWithTarget);
    }

    @Test
    public void testTargetMemberReference2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.TARGET_MEMBER_REFERENCE2, compileOptions);
        Object result = code.evaluate(evaluationContextCustomWithTarget);
        Assert.assertEquals(new Point(5, 10), result);
    }

    @Test(expected = EvaluationException.class)
    public void testInvalidIdentifier() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.INVALID_IDENTIFIER, compileOptions);
        code.evaluate(evaluationContextCustom);
    }

    @Test
    public void testResolveObject() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.RESOLVE_OBJECT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(1.0, result);
    }

    @Test
    public void testFormatDouble() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_DOUBLE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("1.23", result);
    }

    @Test
    public void testFormatInteger() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_INTEGER, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("12345", result);
    }

    @Test
    public void testFormatDate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_DATE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNotNull(result);
        Assert.assertEquals(String.class, result.getClass());
        Assert.assertEquals(10, ((String)result).length());
    }

    @Test
    public void testFormatDateDefault() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_DATE_DEFAULT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNotNull(result);
        Assert.assertEquals(String.class, result.getClass());
        Assert.assertTrue(((String)result).length() > 0);
    }

    @Test
    public void testFormatNull() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_NULL, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("", result);
    }

    @Test
    public void testFormatObject() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_OBJECT, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("Point2D.Double[4.5, 5.5]", result);
    }

    @Test
    public void testFormatCulture() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_CULTURE, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals("1,23", result);
    }

    @Test
    public void testFormatInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_INVALID, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNull(result);
    }

    @Test
    public void testFormatCultureInvalid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.FORMAT_CULTURE_INVALID, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNull(result);
    }

    @Test
    public void testDiscard() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.DISCARD, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertNull(result);
    }

    @Test
    public void testQuantity() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.QUANTITY, compileOptions);
        Object result = code.evaluate(evaluationContextCustom);
        Assert.assertEquals(52.1, result);
    }

    @Test
    public void testDispatchFunctionCall() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.DISPATCH_FUNCTION_CALL, compileOptions);
        Object result = code.evaluate(evaluationContextCustomWithCallbacks);
        Assert.assertEquals("result", result);
    }

    @Test(expected = EvaluationException.class)
    public void testDispatchFunctionCallWithMissingFunction() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.DISPATCH_FUNCTION_CALL_MISSING_FUNCTION, compileOptions);
        code.evaluate(evaluationContextCustomWithCallbacks);
    }

    @Test
    public void testParseObject() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.PARSE_OBJECT, compileOptions);
        Object result = code.evaluate(evaluationContextCustomWithCallbacks);
        Assert.assertNotNull(result);
        Assert.assertEquals(Color.class, result.getClass());
        Assert.assertEquals(Color.decode("#ff0123"), result);
    }

    @Test
    public void testParseObjectDefault() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.PARSE_OBJECT, compileOptions);
        Object result = code.evaluate(evaluationContextDefault);
        Assert.assertNotNull(result);
        Assert.assertEquals(String.class, result.getClass());
        Assert.assertEquals("#ff0123", result);
    }

    @Test
    public void testParseObject2() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteCode code = Compiler.compile(Expressions.PARSE_OBJECT2, compileOptions);
        Object result = code.evaluate(evaluationContextCustomWithCallbacks);
        Assert.assertNotNull(result);
        Assert.assertEquals(String.class, result.getClass());
        Assert.assertEquals("#123", result);
    }
}