package com.meppy.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides a context for evaluating a byte code.
 */
public class EvaluationContext {
    /**
     * The object this context will operate on.
     */
    private final Object target;

    /**
     * The locale associated with the context. This locale is used when formatting
     * a value using the formatting specifier ('@') and no locale is specified.
     */
    private final Locale locale;

    /**
     * Implementation of most available functions.
     */
    private final Functions functions;

    /**
     * A collection with defined variables and their values.
     */
    private Dictionary<String, Object> localVars;

    /**
     * Called when a function name is not recognized as one of the built-in functions to allow clients to evaluate the function.
     */
    private final BiFunction<String, Object[], FunctionEvaluationResult> dispatchFunctionCall;

    /**
     * Called when an object needs to be parsed from its string representation and the parser
     * is either not able to parse it or not able to create an object of the respective type.
     */
    private final Function<String, FunctionEvaluationResult> parseObject;

    /**
     * Initializes a new instance of the {@link EvaluationContext} class.
     */
    public EvaluationContext() {
        this(null, Locale.ROOT, null, null);
    }

    /**
     * Initializes a new instance of the {@link EvaluationContext} class for the specified object.
     */
    public EvaluationContext(Object target) {
        this(target, Locale.ROOT, null, null);
    }

    /**
     * Initializes a new instance of the {@link EvaluationContext} class for the specified object and locale.
     */
    public EvaluationContext(Object target, Locale locale) {
        this(target, locale, null, null);
    }

    /**
     * Initializes a new instance of the {@link EvaluationContext} class for the specified object and locale.
     */
    public EvaluationContext(Object target, Locale locale,
                             BiFunction<String, Object[], FunctionEvaluationResult> dispatchFunctionCall) {
        this(target, locale, dispatchFunctionCall, null);
    }

    /**
     * Initializes a new instance of the {@link EvaluationContext} class for the specified object and locale.
     */
    public EvaluationContext(Object target, Locale locale,
                             BiFunction<String, Object[], FunctionEvaluationResult> dispatchFunctionCall,
                             Function<String, FunctionEvaluationResult> parseObject) {
        this.target = target;
        this.locale = locale;
        this.dispatchFunctionCall = dispatchFunctionCall;
        this.parseObject = parseObject;
        this.functions = new Functions(locale);
        this.localVars = new Hashtable<>();
    }

    /**
     * Returns the number of parameters required by the function with the specified name.
     * This method should exclude optional parameters, if any.
     * @param name The name of the function.
     * @return The number of required parameters.
     */
    public int getFunctionParamCount(String name) {
        switch (name) {
            case "now":
            case "e":
            case "pi":
            case "today":
                return 0;
            case "abs":
            case "asc":
            case "atn":
            case "chr":
            case "cbool":
            case "cdate":
            case "cdbl":
            case "cint":
            case "clong":
            case "csng":
            case "cstr":
            case "cos":
            case "acos":
            case "exp":
            case "int":
            case "isNull":
            case "isNumeric":
            case "lcase":
            case "len":
            case "log":
            case "pow":
            case "rnd":
            case "sgn":
            case "sin":
            case "asin":
            case "space":
            case "sqr":
            case "sqrt":
            case "str":
            case "strReverse":
            case "tan":
            case "trim":
            case "ucase":
            case "typeOf":
            case "round":
                return 1;
            case "inStr":
            case "inStrRev":
            case "left":
            case "right":
            case "strComp":
            case "string":
            case "min":
            case "max":
                return 2;
            case "iif":
            case "mid":
            case "replace":
                return 3;
            default:
                break;
        }

        return 0;
    }

    /**
     * Invokes the function with the specified name.
     * <p>
     * If the specified name does not match any of the built-in functions, the DispatchFunctionCall is raised.
     * @param name The name of the function to invoke.
     * @param p The list of arguments of the function.
     * @return The result of the invocation.
     */
    public Object invokeFunction(String name, Object... p) {
        int requiredParamCount = getFunctionParamCount(name);
        if (p.length < requiredParamCount) {
            throw new EvaluationException(String.format("The function '%1$s' expects %2$d number of parameters," +
                "but instead received %3$d.", name, requiredParamCount, p.length));
        }

        switch (name) {
            case "now":
                return functions.now();
            case "e":
                return functions.e();
            case "pi":
                return functions.pi();
            case "today":
                return functions.today();
            case "abs":
                return functions.abs(getDouble(p[0]));
            case "asc":
                return functions.asc(getString(p[0]));
            case "atn":
                return functions.atn(getDouble(p[0]));
            case "chr":
                return functions.chr(getInt(p[0]));
            case "cbool":
                return functions.cbool(p[0]);
            case "cdate":
                return functions.cdate(p[0]);
            case "cdbl":
                return functions.cdbl(p[0]);
            case "cint":
                return functions.cint(p[0]);
            case "clong":
                return functions.clong(p[0]);
            case "csng":
                return functions.csng(p[0]);
            case "cstr":
                return functions.cstr(p[0]);
            case "cos":
                return functions.cos(getDouble(p[0]));
            case "acos":
                return functions.acos(getDouble(p[0]));
            case "exp":
                return functions.exp(getDouble(p[0]));
            case "int":
                return functions.integer(getDouble(p[0]));
            case "isNull":
                return functions.isNull(p[0]);
            case "isNumeric":
                return functions.isNumeric(p[0]);
            case "lcase":
                return functions.lcase(getString(p[0]));
            case "len":
                return functions.len(getString(p[0]));
            case "log":
                return functions.log(getDouble(p[0]));
            case "pow": {
                double a = getDouble(p[0]);
                double b = 2;
                if (p.length > 1) {
                    b = getDouble(p[1]);
                }
                return functions.pow(a, b);
            }
            case "rnd":
                return functions.rnd(getInt(p[0]));
            case "sgn":
                return functions.sgn(getDouble(p[0]));
            case "sin":
                return functions.sin(getDouble(p[0]));
            case "asin":
                return functions.asin(getDouble(p[0]));
            case "space":
                return functions.space(getInt(p[0]));
            case "sqr":
                return functions.sqr(getDouble(p[0]));
            case "sqrt":
                return functions.sqrt(getDouble(p[0]));
            case "str":
                return functions.str(getDouble(p[0]));
            case "strReverse":
                return functions.strReverse(getString(p[0]));
            case "tan":
                return functions.tan(getDouble(p[0]));
            case "trim":
                return functions.trim(getString(p[0]));
            case "ucase":
                return functions.ucase(getString(p[0]));
            case "inStr":
                return functions.inStr(getString(p[0]), getString(p[1]));
            case "inStrRev":
                return functions.inStrRev(getString(p[0]), getString(p[1]));
            case "left":
                return functions.left(getString(p[0]), getInt(p[1]));
            case "right":
                return functions.right(getString(p[0]), getInt(p[1]));
            case "round":
                return functions.round(getDouble(p[0]));
            case "strComp":
                return functions.strComp(getString(p[0]), getString(p[1]));
            case "string":
                return functions.string(getInt(p[0]), getInt(p[1]));
            case "iif":
                return functions.iif(getBool(p[0]), p[1], p[2]);
            case "mid":
                return functions.mid(getString(p[0]), getInt(p[1]), getInt(p[2]));
            case "replace":
                return functions.replace(getString(p[0]), getString(p[1]), getString(p[2]));
            case "typeOf":
                return functions.typeOf(p[0]);
            case "min":
                return Math.min(getDouble(p[0]), getDouble(p[1]));
            case "max":
                return Math.max(getDouble(p[0]), getDouble(p[1]));
            default:
                break;
        }

        if (dispatchFunctionCall != null) {
            FunctionEvaluationResult result = dispatchFunctionCall.apply(name, p);
            if (result.isEvaluated()) {
                return result.getResult();
            }
        }

        throw new EvaluationException(String.format("The function '%1$s' is not defined.", name));
    }

    /**
     * Returns the specified value as a boolean.
     */
    protected final boolean getBool(Object value) {
        return functions.cbool(value);
    }

    /**
     * Returns the specified value as an integer.
     */
    protected final int getInt(Object value) {
        return functions.cint(value);
    }

    /**
     * Returns the specified value as a double.
     */
    protected final double getDouble(Object value) {
        return functions.cdbl(value);
    }

    /**
     * Returns the specified value as a string.
     */
    protected final String getString(Object value) {
        return functions.cstr(value);
    }

    /**
     * Evaluates the identifier with the specified name.
     * @param name The identifier to evaluate.
     * @return The result of the evaluation.
     */
    protected Object evaluateIdentifier(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new EvaluationException("Identifier cannot be null or empty.");
        }
        if (StringUtils.equalsIgnoreCase(name, "null")) {
            return null;
        }

        Object obj = findObject(name);
        if (obj != null) {
            return obj;
        }

        if (StringUtils.equalsIgnoreCase(name, "true")) {
            return true;
        } else if (StringUtils.equalsIgnoreCase(name, "false")) {
            return false;
        }

        // Attempt to resolve the specified identifier as a property on the underlying object
        if (target != null) {
            Method propertyGetter = target.getClass().getMethod(StringUtils.getPropertyName(name));
            if (!propertyGetter.isAccessible()) {
                propertyGetter.setAccessible(true);
            }
            return propertyGetter.invoke(target);
        }

        throw new EvaluationException(String.format("Identifier '%1$s' could not be found.", name));
    }

    /**
     * Evaluates the member with the specified name against the specified target.
     * <p>
     * Member references are evaluated automatically. This method is called when
     * a member references could not be resolved through reflection.
     * @param target The target object.
     * @param name The name of the member.
     * @param last Indicates whether the resolved member is the last in a sequence of member references.
     *             For example, in the following expression "A.B.C", when A.B is being resolved, last is
     *             set to false, and when R.C is being resolved (where R is the value of A.B), last is set to true.
     * @return The value of the member.
     */
    protected Object evaluateMember(Object target, String name, boolean last) {
        throw new EvaluationException(String.format("The member '%1$s' could not be found on the target '%2$s'", name, target));
    }

    /**
     * Returns the object with the specified name.
     * <p>
     * If this method returns null, the object is looked for in the underlying list with local variables.
     * @param name The name of the object.
     * @return The found object.
     */
    protected Object resolveObject(String name) {
        return null;
    }

    /**
     * The value to evaluate.
     * @param value The value to evaluate.
     * @param unit The unit of the value.
     * @return The evaluated quantity.
     */
    protected double evaluateQuantity(double value, String unit) {
        return value;
    }

    /**
     * Formats the specified value according to the specified format.
     * @param value The value to format.
     * @param pattern The formatting specifier to use.
     * @param locale The culture to use when performing the formatting.
     * @return The string representing the value formatted according to the specified format.
     */
    String format(Object value, String pattern, Locale locale) {
        if (value == null) {
            return "";
        }

        if (locale == null) {
            locale = this.locale;
        }

        if (value instanceof Number) {
            Number number = (Number)value;
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            if (numberFormat instanceof DecimalFormat && !StringUtils.isNullOrEmpty(pattern)) {
                ((DecimalFormat) numberFormat).applyPattern(pattern);
            }
            if (number instanceof Float || number instanceof Double) {
                return numberFormat.format(number.doubleValue());
            } else {
                return numberFormat.format(number.longValue());
            }
        } else if (value instanceof Date) {
            Date date = (Date)value;
            DateFormat dateFormat = StringUtils.isNullOrEmpty(pattern) ?
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale) :
                new SimpleDateFormat(pattern, locale);
            return dateFormat.format(date);
        } else {
            return value.toString();
        }
    }

    /**
     * Finds the object with the specified name. For example, 'this' yields the underlying target.
     */
    private Object findObject(String objectName) {
        if (StringUtils.equalsIgnoreCase(objectName, "this")) {
            return target;
        }

        // Resolve through the callback
        Object value = resolveObject(objectName);
        if (value != null) {
            return value;
        }

        return localVars.get(objectName);
    }

    /**
     * Creates a {@link MemberInfo} object representing a property with the specified name on the specified object.
     */
    MemberInfo createMemberInfo(String objectName, String propertyName) {
        Object targetObject = findObject(objectName);

        if (targetObject == null && target != null) {
            // Attempt to resolve the specified identifier as a property on the underlying object
            try {
                Method propertyGetter = target.getClass().getMethod(StringUtils.getPropertyName(objectName));
                targetObject = propertyGetter.invoke(target);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new EvaluationException(String.format("Could not resolve '%1$s'.", objectName), e);
            }
        }

        if (targetObject == null) {
            throw new EvaluationException(String.format("Could not resolve '%1$s'.", objectName));
        }

        return new MemberInfo(targetObject, propertyName, this);
    }

    /**
     * Creates a {@link MemberInfo} object representing a property with the specified name
     * on the object represented by the specified {@link MemberInfo}.
     */
    MemberInfo createMemberInfo(MemberInfo info, String propertyName) {
        return new MemberInfo(info, propertyName, this);
    }

    /**
     * Invoked during evaluation to parse an object.
     * <p>
     * Upon failure to parse, the string representation is returned as a result.
     */
    Object doParseObject(String representation) {
        if (parseObject == null) {
            return representation;
        }

        FunctionEvaluationResult result = parseObject.apply(representation);
        if (result.isEvaluated()) {
            return result.getResult();
        }

        return representation;
    }


    /**
     * Gets the culture associated with the context.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets a collection with defined variables and their values.
     */
    public Dictionary<String, Object> getLocalVars() {
        return localVars;
    }
}