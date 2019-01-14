package com.meppy.expressions;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.ArrayDeque;

/**
 * Encapsulates the compiled result of a parsed tree and performs evaluations against that compiled result in various contexts.
 */
public final class ByteCode {
    /**
     * Specifies the type of an operation or data.
     */
    private enum Op {
        /** Indicates a text. */
        TEXT,
        /** Indicates a floating point number. */
        FLOAT,
        /** Indicates an integer number. */
        INT,
        /** Indicates a string literal within an expression. */
        STRING,
        /** Indicates the 'null' keyword. */
        NULL,
        /** Indicates a color literal. */
        COLOR,
        /** Indicates a quantity expression. */
        QUANTITY,
        /** Indicates an addition operation. */
        ADD,
        /** Indicates a subtraction operation. */
        SUB,
        /** Indicates an unary minus operation. */
        MINUS,
        /** Indicates a multiplication operation. */
        MUL,
        /** Indicates a division operation. */
        DIV,
        /** Indicates an integer division operation. */
        MOD,
        /** Indicates the raise to power operation. */
        POWER,
        /** Indicates a less than comparison operation. */
        LESS,
        /** Indicates a greater than comparison operation. */
        GREATER,
        /** Indicates an equality comparison operation. */
        EQUAL,
        /** Indicates a not equality comparison operation. */
        NOT_EQUAL,
        /** Indicates a less than or equal comparison operation. */
        LESS_OR_EQUAL,
        /** Indicates a greater than or equal comparison operation. */
        GREATER_OR_EQUAL,
        /** Indicates a boolean negation opeation. */
        NOT,
        /** Indicates a bitwise and operation. */
        AND,
        /** Indicates a bitwise exclusive or operation. */
        XOR,
        /** Indicates a bitwise or operation. */
        OR,
        /** Indicates a conditional and operation. */
        CONDITIONAL_AND,
        /** Indicates a conditional or operation. */
        CONDITIONAL_OR,
        /** Indicates a member access operation. */
        DOT,
        /** Indicates a reference operation. */
        REFERENCE,
        /** Indicates a formatting specifier. */
        FORMAT,
        /** Indicates a culture specifier. */
        CULTURE,
        /** Indicates a formatting operation. */
        FORMATTING,
        /** Indicates a discard operation. */
        DISCARD,
        /** Indicates a function call. */
        FUNCTION_CALL,
        /** Indicates an identifier. */
        IDENTIFIER,
        /** Indicates an identifier representing an object or a member reference. */
        OBJECT_OR_MEMBER,
        /** Indicates expression separator. */
        EXPRESSION_SEPARATOR,
    }

    /**
     * Provides numeric calculation operators.
     */
    static final class Calc {
        private static final String EXCEPTION_MESSAGE = "The operator %1$s cannot be applied to operands of type '%2$s' and '%3$s'.";

        private Calc() {
        }

        /**
         * Contains the priority of the types. Objects are promoted to types
         * of higher priority when performing operation on operands of different types.
         */
        private static Dictionary<Class<?>, Integer> typePriority;

        static {
            typePriority = new Hashtable<>();
            typePriority.put(Boolean.class, -1);
            typePriority.put(Byte.class, 1);
            typePriority.put(Short.class, 2);
            typePriority.put(Integer.class, 3);
            typePriority.put(Long.class, 4);
            typePriority.put(Float.class, 5);
            typePriority.put(Double.class, 6);
            typePriority.put(Date.class, 7);
            typePriority.put(String.class, 8);
        }

        /**
         * Promotes the specified value to the specified type.
         */
        private static Object convertValue(Object value, Class<?> type) throws ParseException {
            if (value == null) {
                return null;
            }

            if (type == value.getClass()) {
                return value;
            }

            if (value instanceof Boolean) {
                return convertBoolean((Boolean)value, type);
            } else if (value instanceof Number) {
                return convertNumber((Number)value, type);
            } else if (value instanceof Date) {
                return convertDate((Date)value, type);
            } else if (value instanceof String) {
                return convertString((String)value, type);
            }

            return value;
        }

        private static Object convertBoolean(Boolean value, Class<?> type) {
            int intValue = value ? 1 : 0;
            if (type == Byte.class) {
                return (byte)intValue;
            } else if (type == Short.class) {
                return (short)intValue;
            } else if (type == Integer.class) {
                return intValue;
            } else if (type == Long.class) {
                return (long)intValue;
            } else if (type == Float.class) {
                return (float)intValue;
            } else if (type == Double.class) {
                return (double)intValue;
            } else if (type == Date.class) {
                throw new EvaluationException(String.format("Cannot convert boolean value %1$b to type Date.", value));
            } else if (type == String.class) {
                return value.toString();
            }

            return value;
        }

        private static Object convertNumber(Number value, Class<?> type) {
            if (type == Byte.class) {
                return value.byteValue();
            } else if (type == Short.class) {
                return value.shortValue();
            } else if (type == Integer.class) {
                return value.intValue();
            } else if (type == Long.class) {
                return value.longValue();
            } else if (type == Float.class) {
                return value.floatValue();
            } else if (type == Double.class) {
                return value.doubleValue();
            } else if (type == Date.class) {
                return new Date(value.longValue());
            } else if (type == String.class) {
                return value.toString();
            }

            return value;
        }

        private static Object convertDate(Date value, Class<?> type) {
            long millis = value.getTime();
            if (type == Byte.class) {
                return (byte)millis;
            } else if (type == Short.class) {
                return (short)millis;
            } else if (type == Integer.class) {
                return (int)millis;
            } else if (type == Long.class) {
                return millis;
            } else if (type == Float.class) {
                return (float)millis;
            } else if (type == Double.class) {
                return (double)millis;
            } else if (type == Date.class) {
                return new Date(millis);
            } else if (type == String.class) {
                return value.toString();
            }

            return value;
        }

        private static Object convertString(String value, Class<?> type) throws ParseException {
            if (type == Byte.class) {
                return Byte.parseByte(value);
            } else if (type == Short.class) {
                return Short.parseShort(value);
            } else if (type == Integer.class) {
                return Integer.parseInt(value);
            } else if (type == Long.class) {
                return Long.parseLong(value);
            } else if (type == Float.class) {
                return Float.parseFloat(value);
            } else if (type == Double.class) {
                return Double.parseDouble(value);
            } else if (type == Date.class) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                return dateFormat.parse(value);
            } else if (type == String.class) {
                return value;
            }

            return value;
        }

        /**
         * Performs the specified unary operation on the specified argument.
         */
        static Object apply(Object a, Op op) {
            if (a == null) {
                return null;
            }

            if (op == Op.NOT && a instanceof Boolean) {
                return !((Boolean) a);
            }

            if (op == Op.MINUS) {
                if (a instanceof Byte) {
                    return -(Byte) a;
                } else if (a instanceof Short) {
                    return -(Short) a;
                } else if (a instanceof Integer) {
                    return -(Integer) a;
                } else if (a instanceof Long) {
                    return -(Long) a;
                } else if (a instanceof Float) {
                    return -(Float) a;
                } else if (a instanceof Double) {
                    return -(Double) a;
                }
            }

            throw new UnsupportedOperationException(
                String.format("The operator %1$s cannot be applied to operand of type '%2$s'.", op, a.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        static Object apply(Object a, Object b, Op op) throws ParseException {
            if (a == null || b == null) {
                if (op == Op.EQUAL) {
                    return a == b;
                } else if (op == Op.NOT_EQUAL) {
                    return a != b;
                } else {
                    return null;
                }
            }

            Class<?> aClass = a.getClass();
            Class<?> bClass = b.getClass();
            Integer aPriority = typePriority.get(aClass);
            Integer bPriority = typePriority.get(bClass);

            if (aPriority == null || bPriority == null) {
                throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, aClass.getName(), bClass.getName()));
            }

            Class<?> greater = aClass;
            if (bPriority > aPriority) {
                greater = bClass;
            }

            // Promote both objects to the greater type
            a = convertValue(a, greater);
            b = convertValue(b, greater);

            if (greater == Boolean.class) {
                return apply((Boolean) a, (Boolean) b, op);
            } else if (greater == Byte.class) {
                return apply((Byte) a, (Byte) b, op);
            } else if (greater == Short.class) {
                return apply((Short) a, (Short) b, op);
            } else if (greater == Integer.class) {
                return apply((Integer) a, (Integer) b, op);
            } else if (greater == Long.class) {
                return apply((Long) a, (Long) b, op);
            } else if (greater == Float.class) {
                return apply((Float) a, (Float) b, op);
            } else if (greater == Double.class) {
                return apply((Double) a, (Double) b, op);
            } else if (greater == String.class) {
                return apply((String) a, (String) b, op);
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, aClass.getName(), bClass.getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Boolean a, Boolean b, Op op) {
            switch (op) {
                case CONDITIONAL_AND: {
                    return a && b;
                }
                case CONDITIONAL_OR: {
                    return a || b;
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Byte a, Byte b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                case AND: {
                    return a & b;
                }
                case XOR: {
                    return a ^ b;
                }
                case OR: {
                    return a | b;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Short a, Short b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                case AND: {
                    return a & b;
                }
                case XOR: {
                    return a ^ b;
                }
                case OR: {
                    return a | b;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Integer a, Integer b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                case AND: {
                    return a & b;
                }
                case XOR: {
                    return a ^ b;
                }
                case OR: {
                    return a | b;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Long a, Long b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                case AND: {
                    return a & b;
                }
                case XOR: {
                    return a ^ b;
                }
                case OR: {
                    return a | b;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Float a, Float b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(Double a, Double b, Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case SUB: {
                    return a - b;
                }
                case MUL: {
                    return a * b;
                }
                case DIV: {
                    return a / b;
                }
                case MOD: {
                    return a % b;
                }
                case POWER: {
                    return Math.pow(a, b);
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }

        /**
         * Performs the specified operation on the specified arguments.
         */
        private static Object apply(String a, String b, ByteCode.Op op) {
            switch (op) {
                case ADD: {
                    return a + b;
                }
                case LESS: {
                    return a.compareTo(b) < 0;
                }
                case GREATER: {
                    return a.compareTo(b) > 0;
                }
                case EQUAL: {
                    return a.compareTo(b) == 0;
                }
                case NOT_EQUAL: {
                    return a.compareTo(b) != 0;
                }
                case LESS_OR_EQUAL: {
                    return a.compareTo(b) <= 0;
                }
                case GREATER_OR_EQUAL: {
                    return a.compareTo(b) >= 0;
                }
                default:
                    break;
            }

            throw new EvaluationException(String.format(EXCEPTION_MESSAGE, op, a.getClass().getName(), b.getClass().getName()));
        }
    }

    /**
     * The "byte" code.
     */
    private final List<Object> code;

    /**
     * A temporary object used during code generation.
     */
    private ArrayDeque<Token> stack;

    /**
     * Creates and returns an empty byte code. Evaluating empty byte code will produce null as a result.
     * <p>
     * Calling this method multiple times will return different instances.
     * @return An empty instance of the ByteCode class.
     */
    public static ByteCode empty() {
        return new ByteCode(null);
    }

    /**
     * Initializes a new instance of the ByteCode class from the specified parse tree root.
     */
    ByteCode(ParseTreeNode root) {
        code = new ArrayList<>();
        stack = new ArrayDeque<>();

        // Build the byte code out of the specified root
        if (root != null)
            buildCode(root);
    }

    /**
     * Builds a byte code from the subtree defined by the specified tree node.
     */
    private void buildCode(ParseTreeNode node) {
        Token t = node.getToken();

        if (!node.getChildren().isEmpty()) {
            stack.push(t);
            node.getChildren().forEach(this::buildCode);
            stack.pop();
        }

        if (t == null) {
            return;
        }

        switch (t.getType()) {
            case TEXT: {
                code.add(Op.TEXT);
                code.add(t.getText());
                break;
            }
            case FloatNumber: {
                code.add(Op.FLOAT);
                // Always parse in invariant culture because the float regular expression in Lexer is culture-independent
                code.add(Double.parseDouble(t.getText()));
                break;
            }
            case IntNumber: {
                code.add(Op.INT);
                // Always parse in invariant culture because the float regular expression in Lexer is culture-independent
                code.add(Integer.parseInt(t.getText()));
                break;
            }
            case String: {
                code.add(Op.STRING);
                code.add(t.getText());
                break;
            }
            case NULL: {
                code.add(Op.NULL);
                break;
            }
            case Color: {
                code.add(Op.COLOR);
                code.add(parseColor(t.getText()));
                break;
            }
            case OpAdd: {
                code.add(Op.ADD);
                break;
            }
            case OpSubtract: {
                if (node.getChildren().size() > 1) {
                    code.add(Op.SUB);
                } else {
                    code.add(Op.MINUS);
                }
                break;
            }
            case OpMultiply: {
                code.add(Op.MUL);
                break;
            }
            case OpDivide: {
                code.add(Op.DIV);
                break;
            }
            case OpMod: {
                code.add(Op.MOD);
                break;
            }
            case OpLess: {
                code.add(Op.LESS);
                break;
            }
            case OpGreater: {
                code.add(Op.GREATER);
                break;
            }
            case OpEqual: {
                code.add(Op.EQUAL);
                break;
            }
            case OpNotEqual: {
                code.add(Op.NOT_EQUAL);
                break;
            }
            case OpLessOrEqual: {
                code.add(Op.LESS_OR_EQUAL);
                break;
            }
            case OpGreaterOrEqual: {
                code.add(Op.GREATER_OR_EQUAL);
                break;
            }
            case OpNot: {
                code.add(Op.NOT);
                break;
            }
            case OpAnd: {
                code.add(Op.AND);
                break;
            }
            case OpXor: {
                code.add(Op.XOR);
                break;
            }
            case OpPower: {
                code.add(Op.POWER);
                break;
            }
            case OpOr: {
                code.add(Op.OR);
                break;
            }
            case OpConditionalAnd: {
                code.add(Op.CONDITIONAL_AND);
                break;
            }
            case OpConditionalOr: {
                code.add(Op.CONDITIONAL_OR);
                break;
            }
            case OpDot: {
                code.add(Op.DOT);
                code.add(node.getChildren().size()); // Dot count
                break;
            }
            case OpReference: {
                code.add(Op.REFERENCE);
                code.add(node.getChildren().size()); // Param count
                break;
            }
            case OpFormat: {
                code.add(Op.FORMATTING);
                break;
            }
            case Discard: {
                code.add(Op.DISCARD);
                break;
            }
            case Format: {
                code.add(Op.FORMAT);
                code.add(t.getText().substring(1, t.getText().length() - 1));
                break;
            }
            case Culture: {
                code.add(Op.CULTURE);
                code.add(t.getText().substring(1, t.getText().length() - 1));
                break;
            }
            case Identifier: {
                // If the operation at the top of the stack is not dereferencing this is a normal identifier.
                // Otherwise, this is an object or member reference
                Token parent = stack.peek();
                if (parent != null &&
                    (parent.getType() == TokenType.OpDot || parent.getType() == TokenType.OpReference))
                    code.add(Op.OBJECT_OR_MEMBER);
                else if (parent != null &&
                    (parent.getType() == TokenType.IntNumber || parent.getType() == TokenType.FloatNumber))
                    code.add(Op.QUANTITY);
                else
                    code.add(Op.IDENTIFIER);
                code.add(t.getText());
                break;
            }
            case FunctionCall: {
                code.add(Op.FUNCTION_CALL);
                code.add(node.getChildren().size()); // Param count
                code.add(t.getText());
                break;
            }
            case OpExpressionSeparator: {
                code.add(Op.EXPRESSION_SEPARATOR);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Attempts to parse the specified text as a color.
     * <p>
     * The color object itself is platform-dependent so the parse
     * implementation is delegated to the client during code evaluation.
     */
    private Object parseColor(String text) {
        return new Color(text);
    }

    /**
     * Evaluates the byte code in the specified context.
     */
    public Object evaluate(EvaluationContext context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParseException {
        ArrayDeque<Object> stack = new ArrayDeque<>();

        int i = 0;
        while (i < code.size()) {
            Op op = (Op)code.get(i); i++;

            switch (op) {
                case FLOAT: {
                    double a = (double)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case INT: {
                    int a = (int)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case STRING: {
                    String a = (String)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case NULL: {
                    stack.push(null);
                    break;
                }
                case COLOR: {
                    Color a = (Color)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case QUANTITY: {
                    String unit = (String)code.get(i); i++;
                    Op valueOp = (Op)code.get(i); i++;
                    double value;
                    if (valueOp == Op.INT)
                        value = (int)code.get(i);
                    else
                        value = (double)code.get(i);
                    i++;
                    stack.push(new Quantity(value, unit));
                    break;
                }
                case FORMAT: {
                    String a = (String)code.get(i); i++;
                    stack.push(a);

                    // Push invariant culture. If the locale is explicitly specified later, we will pop this one
                    stack.push(null);
                    break;
                }
                case DISCARD: {
                    // Push an empty formatting string to indicate that we would like to prevent
                    // the formatted expression from appearing in the output
                    stack.push("");
                    stack.push(null);
                    break;
                }
                case CULTURE: {
                    // Pop the invariant culture and push the specified one
                    String a = (String)code.get(i); i++;
                    stack.pop();
                    stack.push(a);
                    break;
                }
                case TEXT: {
                    String a = (String)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case IDENTIFIER: {
                    String a = (String)code.get(i); i++;
                    stack.push(new Identifier(a));
                    break;
                }
                case OBJECT_OR_MEMBER: {
                    // Its the name of an object or its property. Push it to the stack for
                    // subsequent processing when the Op.Dot operation is reached
                    String a = (String)code.get(i); i++;
                    stack.push(a);
                    break;
                }
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case POWER:

                case LESS:
                case LESS_OR_EQUAL:
                case GREATER:
                case GREATER_OR_EQUAL:
                case EQUAL:
                case NOT_EQUAL:

                case CONDITIONAL_AND:
                case CONDITIONAL_OR:

                case AND:
                case OR:
                case XOR: {
                    // Binary operation
                    Object b = evaluate(stack.pop(), context);
                    Object a = evaluate(stack.pop(), context);
                    stack.push(Calc.apply(a, b, op));
                    break;
                }
                case MINUS:
                case NOT: {
                    // Unary operation
                    Object a = evaluate(stack.pop(), context);
                    stack.push(Calc.apply(a, op));
                    break;
                }
                case DOT: {
                    int count = (int)code.get(i); i++;
                    List<String> r = new ArrayList<>();
                    for (int c = 0; c < count - 1; c++) {
                        r.add(0, (String) stack.pop());
                    }
                    Object target = stack.pop();

                    // Create a MemberInfo and push it in the stack
                    MemberInfo info = target instanceof String ?
                        context.createMemberInfo((String) target, r.get(0)) :
                        new MemberInfo(target, r.get(0), context);

                    for (int c = 1; c < r.size(); c++) {
                        info = context.createMemberInfo(info, r.get(c));
                    }

                    stack.push(info);
                    break;
                }
                case REFERENCE: {
                    int count = (int)code.get(i); i++;
                    List<String> r = new ArrayList<>();
                    for (int c = 0; c < count - 1; c++) {
                        r.add(0, (String) stack.pop());
                    }
                    String target = (String)stack.pop();

                    stack.push(context.resolveReference(target, r));
                    break;
                }
                case FORMATTING: {
                    String c = (String)stack.pop();
                    String b = (String)stack.pop();
                    Object a = evaluate(stack.pop(), context);

                    // The formatting string might be null or empty, particularly when discarding is specified.
                    // In this case do not push anything in the stack
                    if (!StringUtils.isNullOrEmpty(b)) {
                        stack.push(context.format(a, b, StringUtils.isNullOrEmpty(c) ? null : new Locale(c)));
                    }
                    break;
                }
                case FUNCTION_CALL: {
                    int paramCount = (int)code.get(i); i++;
                    String a = (String)code.get(i); i++;
                    Object[] parameters = new Object[paramCount];
                    for (int j = 0; j < paramCount; j++) {
                        parameters[paramCount - j - 1] = evaluate(stack.pop(), context);
                    }

                    stack.push(context.invokeFunction(a, parameters));
                    break;
                }
                case EXPRESSION_SEPARATOR: {
                    Object b = evaluate(stack.pop(), context);
                    /*Object a = */
                    evaluate(stack.pop(), context);
                    stack.push(b);
                    break;
                }
            }
        }

        if (stack.isEmpty()) {
            return null;
        }

        if (stack.size() == 1) {
            return evaluate(stack.pop(), context);
        }

        // Reverse the stack and concatenate the string representation of all elements contained within it
        ArrayDeque<Object> reversed = new ArrayDeque<>();
        while (!stack.isEmpty()) {
            reversed.push(stack.pop());
        }

        StringBuilder result = new StringBuilder();
        while (!reversed.isEmpty()) {
            result.append(evaluate(reversed.pop(), context));
        }

        return result;
    }

    /**
     * Evaluates the specified object.
     * <p>
     * If the object is a MemberInfo, returns its value.
     * If the object is an Identifier, returns its value.
     * If the object is a Color, tries to parse it through the context.
     * Otherwise, returns the object itself.
     */
    private Object evaluate(Object value, EvaluationContext context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (value instanceof MemberInfo) {
            return ((MemberInfo) value).getValue();
        } else if (value instanceof Identifier) {
            return context.evaluateIdentifier(((Identifier) value).getName());
        } else if (value instanceof Color) {
            return context.doParseObject(((Color) value).getRepresentation());
        } else if (value instanceof Quantity) {
            Quantity quantity = (Quantity)value;
            return context.evaluateQuantity(quantity.getValue(), quantity.getUnit());
        }

        return value;
    }


    /**
     * Gets the number of elements in the code.
     */
    int getLength() {
        return code.size();
    }

    /**
     * Gets the element at the specified position within the code.
     */
    Object get(int index) {
        return code.get(index);
    }

    /**
     * Gets a set with all identifiers within the code.
     */
    public Set<String> getIdentifiers() {
        Set<String> identifiers = new HashSet<>();

        for (int i = 0; i < code.size(); i++) {
            if (code.get(i) instanceof Op && code.get(i) == Op.IDENTIFIER) {
                identifiers.add((String)code.get(++i));
            }
        }

        return identifiers;
    }
}

/**
 * Represents identifiers in the code evaluation stack. The identifier is
 * then either evaluated or assigned depending on where it appears in an expression.
 */
final class Identifier {
    private final String name;

    /**
     * Initializes a new instance of the {@link Identifier} class.
     */
    Identifier(String name)
    {
        this.name = name;
    }

    String getName() {
        return name;
    }
}

/**
 * Represents a color in the code. The actual platform-specific color object is created during code evaluation.
 */
final class Color {
    private final String representation;

    /**
     * Initializes a new instance of the {@link Color} object.
     */
    Color(String representation) {
        this.representation = representation;
    }

    /**
     * Gets the string representation of the color.
     */
    String getRepresentation() {
        return representation;
    }
}

final class Quantity {
    private final double value;
    private final String unit;

    Quantity(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    double getValue() {
        return value;
    }

    String getUnit() {
        return unit;
    }
}