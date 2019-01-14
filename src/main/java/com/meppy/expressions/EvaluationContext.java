package com.meppy.expressions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Objects;
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
     * The locale associated with the context. This locale is used when formatting a value using
     * the formatting specifier ('@') and no locale is specified.
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
     * Called when a function name cannot be recognized as one of the built-in functions
     * to allow clients to evaluate the function.
     */
    private final BiFunction<String, Object[], FunctionEvaluationResult> dispatchFunctionCall;

    /**
     * Called when an object needs to be parsed from its string representation and the parser
     * is either not able to parse it or not able to create an object of the respective type.
     */
    private final Function<String, FunctionEvaluationResult> parseObject;

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
            case "Now":
            case "E":
            case "PI":
            case "Today":
                return 0;
            case "Abs":
            case "Asc":
            case "Atn":
            case "Chr":
            case "CBool":
            case "CDate":
            case "CDbl":
            case "CInt":
            case "CLong":
            case "CSng":
            case "CStr":
            case "Cos":
            case "Acos":
            case "Exp":
            case "Int":
            case "IsNull":
            case "IsNumeric":
            case "LCase":
            case "Len":
            case "Log":
            case "Pow":
            case "Rnd":
            case "Sgn":
            case "Sin":
            case "Asin":
            case "Space":
            case "Sqr":
            case "Sqrt":
            case "Str":
            case "StrReverse":
            case "Tan":
            case "Trim":
            case "UCase":
            case "TypeOf":
            case "Round":
                return 1;
            case "InStr":
            case "InStrRev":
            case "Left":
            case "Right":
            case "StrComp":
            case "String":
            case "Min":
            case "Max":
                return 2;
            case "IIf":
            case "Mid":
            case "Replace":
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
            case "Now":
                return functions.Now();
            case "E":
                return functions.E();
            case "PI":
                return functions.PI();
            case "Today":
                return functions.Today();
            case "Abs":
                return functions.Abs(getDouble(p[0]));
            case "Asc":
                return functions.Asc(getString(p[0]));
            case "Atn":
                return functions.Atn(getDouble(p[0]));
            case "Chr":
                return functions.Chr(getInt(p[0]));
            case "CBool":
                return functions.CBool(p[0]);
            case "CDate":
                return functions.CDate(p[0]);
            case "CDbl":
                return functions.CDbl(p[0]);
            case "CInt":
                return functions.CInt(p[0]);
            case "CLong":
                return functions.CLong(p[0]);
            case "CSng":
                return functions.CSng(p[0]);
            case "CStr":
                return functions.CStr(p[0]);
            case "Cos":
                return functions.Cos(getDouble(p[0]));
            case "Acos":
                return functions.Acos(getDouble(p[0]));
            case "Exp":
                return functions.Exp(getDouble(p[0]));
            case "Int":
                return functions.Int(getDouble(p[0]));
            case "IsNull":
                return functions.IsNull(p[0]);
            case "IsNumeric":
                return functions.IsNumeric(p[0]);
            case "LCase":
                return functions.LCase(getString(p[0]));
            case "Len":
                return functions.Len(getString(p[0]));
            case "Log":
                return functions.Log(getDouble(p[0]));
            case "Pow": {
                double a = getDouble(p[0]);
                double b = 2;
                if (p.length > 1) {
                    b = getDouble(p[1]);
                }
                return functions.Pow(a, b);
            }
            case "Rnd":
                return functions.Rnd(getInt(p[0]));
            case "Sgn":
                return functions.Sgn(getDouble(p[0]));
            case "Sin":
                return functions.Sin(getDouble(p[0]));
            case "Asin":
                return functions.Asin(getDouble(p[0]));
            case "Space":
                return functions.Space(getInt(p[0]));
            case "Sqr":
                return functions.Sqr(getDouble(p[0]));
            case "Sqrt":
                return functions.Sqrt(getDouble(p[0]));
            case "Str":
                return functions.Str(getDouble(p[0]));
            case "StrReverse":
                return functions.StrReverse(getString(p[0]));
            case "Tan":
                return functions.Tan(getDouble(p[0]));
            case "Trim":
                return functions.Trim(getString(p[0]));
            case "UCase":
                return functions.UCase(getString(p[0]));
            case "InStr":
                return functions.InStr(getString(p[0]), getString(p[1]));
            case "InStrRev":
                return functions.InStrRev(getString(p[0]), getString(p[1]));
            case "Left":
                return functions.Left(getString(p[0]), getInt(p[1]));
            case "Right":
                return functions.Right(getString(p[0]), getInt(p[1]));
            case "Round":
                return functions.Round(getDouble(p[0]));
            case "StrComp":
                return functions.StrComp(getString(p[0]), getString(p[1]));
            case "String":
                return functions.String(getInt(p[0]), getInt(p[1]));
            case "IIf":
                return functions.IIf(getBool(p[0]), p[1], p[2]);
            case "Mid":
                return functions.Mid(getString(p[0]), getInt(p[1]), getInt(p[2]));
            case "Replace":
                return functions.Replace(getString(p[0]), getString(p[1]), getString(p[2]));
            case "TypeOf":
                return functions.TypeOf(p[0]);
            case "Min":
                return Math.min(getDouble(p[0]), getDouble(p[1]));
            case "Max":
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
    private boolean getBool(Object value) {
        return functions.CBool(value);
    }

    /**
     * Returns the specified value as an integer.
     */
    private int getInt(Object value) {
        return functions.CInt(value);
    }

    /**
     * Returns the specified value as a double.
     */
    private double getDouble(Object value) {
        return functions.CDbl(value);
    }

    /**
     * Returns the specified value as a string.
     */
    private String getString(Object value) {
        return functions.CStr(value);
    }

    /**
     * Evaluates the identifier with the specified name.
     * @param name The identifier to evaluate.
     * @return The result of the evaluation.
     */
    protected Object evaluateIdentifier(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (StringUtils.isNullOrEmpty(name))
            return null;

        Object obj = findObject(name);
        if (obj != null)
            return obj;

        if ("true".compareToIgnoreCase(name) == 0) {
            return true;
        } else if ("false".compareToIgnoreCase(name) == 0) {
            return false;
        }

        Object value = localVars.get(name);
        if (value != null) {
            return value;
        }

        // Attempt to resolve the specified identifier as a property on the underlying object
        if (target != null) {
            Method propertyGetter = target.getClass().getMethod("get" + name);
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
        return null;
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
     * Returns the object representing the specified reference.
     * @param target The name of the object.
     * @param fields A sequence of fields contained in the reference.
     * @return The resolved object.
     */
    protected Object resolveReference(String target, Collection<String> fields) {
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
        if (Objects.equals(objectName, "this")) {
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
    MemberInfo createMemberInfo(String objectName, String propertyName) throws NoSuchMethodException {
        Object targetObject = findObject(objectName);

        if (targetObject == null && target != null) {
            // Attempt to resolve the specified identifier as a property on the underlying object
            try {
                Method propertyGetter = target.getClass().getMethod("get" + propertyName);
                targetObject = propertyGetter.invoke(target);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // TODO:
            }
        }

        if (targetObject == null) {
            throw new EvaluationException(String.format("Could not resolve '%1$s'.", objectName));
        }

        return new MemberInfo(targetObject, propertyName, this);
    }

    /// <summary>
    /// Creates a MemberInfo object representing a property with the
    /// specified name on the object represented by the specified MemberInfo.
    /// </summary>
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