The Expression Evaluator is a small library providing classes to parse and evaluate simple expressions. This page describes the API of the library as well as the syntax of the supported expressions.

# Expression syntax

The Expression Evaluator supports expressions of the following type:

    (a + 5) / 2

The sections below describe all supported components in more details.

## Operand types

The Expression Evaluator inherently supports the following types: Boolean, String, Double, Integer, Quantity, and Null. Additionally, the Expression Evaluator takes into consideration Byte, Short, Long, Float, and Date when performing calculations (see the Operators section below). Operands of these secondary types cannot be specified in expressions, but can be produced as a result of a function call or an identifier or variable evaluation.

### Boolean

Boolean values can be specified as literals in the expression by using the special keywords – `true` and `false` (casing doesn't matter). For example:

    true
    FALSE

Boolean values can also be returned from functions, identifiers, or variables, but most importantly, they are the result of the comparison operators (see the section below). The keywords `true` and `false` can actually be overridden during evaluation. For more information about this, check the Variables and EvaluationContext sections below.

### String

A string can be specified as a literal in the expression or it can be returned from a function. To specify a string literal, enclose it in double quotes:

    "This result is: " + (2 + 3)

In the above expression `"This result is: "` is a string literal. The actual string value is the content without the quotes. As is observed in the expression, strings can be used in certain operations (see the Operators section below).

### Double

A Double value can be specified in an expression by either using a dot (`.`) or the E notation. The following are examples of Double values:

    3.14
    1E-5
    1.3E2

Double values can also be returned from functions, identifier evaluations, as a result of arithmetic operation, and so on.

### Integer

Numbers without dot or E are interpreted as integers. For example:

    123
    05

### Quantity

Quantity is a special value type, which represents an arbitrary metric. Quantities can be specified in the expressions by using the <Number><Unit> notation, where <Number> is any Double or Integer value and <Unit> is a valid Identifier (see below). By default Quantities are evaluated to the specified number. The `evaluateQuantity` method of the `EvaluationContext` class can be overridden in order to provide custom evaluation of different quantities (see EvaluationContext below for an example). Here is an example of expressions containing quantities:

    20px + 50pt
    10hours

### Null

Null is a special value, which can be specified in an expression by using the `null` keyword (case insensitive). For example:

    myVar != NULL

### Secondary types

As noted earlier, the secondary types are Byte, Short, Long, Float, and Date. They cannot be specified directly in the expression but can be produced as a result of a function call, identifier evaluation, etc, and the Expression Evaluator takes them into consideration when performing arithmetic operations. For example, there are several built-in functions, which produce Date objects as a result (see the Functions section below).

### Custom types

The client can introduce objects of custom types in expressions, by providing the respective variables and custom functions. Consider the following expression:

    distance(a, b)

In this expression, `a` and `b` are predefined variables of type `Point`, and `distance` is a custom function, which accepts Point objects as arguments and returns the calculated distance between these points. This expression can be parsed and evaluated as long as a, b, and distance are provided by the client. For more information, check the Variables and Custom functions sections below.

## Operators

### Arithmetic operators

The following arithmetic operators are supported: `+` (addition), `–` (subtraction), `/` (division), `*` (multiplication), `%` (modulo), `–` (unary minus), and, optionally, `^` (raising to power). Apart from the unary minus, all other operations are binary. The Expression Evaluator is very flexible when performing arithmetic operations on operands of different type. The result depends on the type of the operands as indicated by the following table:

| Operand A | Operand B | Result | Notes |
|-----------|-----------|--------|-------|
| null	    | <anything> | null |
| Byte, Short, or Integer | Byte, Short, or Integer | Integer |
| Boolean | Byte, Short, or Integer | Integer | true evaluates to 1 and false evaluates to 0
| Boolean, Byte, Short, Integer, or Long | Long | Long | true evaluates to 1 and false evaluates to 0
| Boolean, Byte, Short, Integer, Long, or Float | Float | Float | true evaluates to 1 and false evaluates to 0
| Boolean, Byte, Short, Integer, Long, Float, or Double | Double | Double | true evaluates to 1 and false evaluates to 0
| Boolean | Date | <exception> |
| Byte, Short, Integer, Long, Float, Double, or Date | Date | Date | If the operator is different than addition or subtraction, an `EvaluationException` will be thrown. |
| <anything other than null> | String | String | If the operator is different than addition (effectively concatenation in this case), an `EvaluationException` will be thrown. |

Simply put, if the operands are of different type, they are converted to the same type based on the following priority: Boolean → Byte → Short → Integer → Long → Float → Double → Date → String. Note, that the same applies regardless of whether Operand A is the first operand and Operand B is the second operand, or vice versa.

All operations can overflow or underflow silently.

The unary minus operator can be applied to operands of the following types:

| Operand | Result |
|---------|--------|
|null | null |
| Byte, Short, or Integer | Integer |
| Long | Long |
| Float | Float |
| Double | Double |

If the arithmetic operators are applied to operands of types not listed in the above tables, an `EvaluationException` will be thrown during evaluation. Additionally, division and modulo operations will throw an `ArithmeticException` when the second argument evaluates to zero.

### Comparison operators

The following comparison operators are supported: `<` (less than), `<=` (less than or equal), `>` (greater than), `>=` (greater than or equal), `==` (equal), and `!=` (not equal). All operations are binary. The same type conversion applies when comparing operands of different types. When the operands are converted, they are compared using their `Comparable.compareTo` implementation. If one or both of the operands are `null`, the result will be `null`, except when comparing for equality: `null == null` will return `true`, and `null != null` will return `false`.

### Bitwise operators

The following bitwise operators are supported: `&` (bitwise and), `|` (bitwise or), and, optionally, `^` (bitwise xor). All operations are binary. The same type conversion applies here as well. However, these operators are only applicable to numeric and boolean operands (and `null`). If any of the operands is `null`, the result will be `null`.

### Logical operators

The following logical operators are supported: `&&` (conditional and), `||` (conditional or), and `!` (not). The first two operations are binary, the logical not is unary. These operators are only applicable to boolean operands (and `null`). In any other case, an `EvaluationException` will be thrown.

### The dot (.) operator

The dot operator can be used to reference the properties of an object. An object can appear in an expression by one of the following ways: when an Identifier or a Variable is evaluated (see below about identifiers and variables) or as a result of a custom function. The following examples illustrate the dot operator:

    point.X * point.X
    myFunction().Property

In the first example above `point` is either an identifier or a variable. When evaluating `point.X`, the Expression Evaluator will first resolve the point object, then it will try to invoke the method `getX` of that object and use the value returned from the method in the subsequent calculations. In the second example, the custom function `myFunction` is invoked first (see below about custom functions), then `getProperty` method is called on the returned object.

When using the dot operator, consider the following:

* The method invocation is performed through reflection. This can cause performance overhead in time-critical scenarios.
* Dot operations can be chained. For example: `a.MyProp.Value`.
* If the object does not contain the specified method, an `EvaluationException` will be thrown.

### Operator priorities

When several operators are used in the same expression and the order of precedence is not enforced by using parentheses, the following table applies (starting with the highest priority operators):

| Operator |
|----------|
| . (dot) |
| ! (not) |
| - (unary minus) |
| * (multiplication), / (division), % (modulo), ^ (raising to power) |
| + (addition), – (subtraction) |
| < (less than), <= (less than or equal), > (greater than), >= (greater than or equal) |
| == (equal), != (not equal) |
| & (bitwise and) |
| ^ (bitwise xor) |
| | (bitwise or) |
| && (conditional and) |
| || (conditional or) |

If there is more than one operator in a row, they have the same priority and are evaluated from left to right. For example, in the expression `a / b * c`, the division is performed first.

## Identifiers

An identifier is a literal, matching the regular expression: `[a-zA-Z_][a-z_A-Z0-9]*`, that is, it starts with a letter or underscore, followed by zero or more letters, numbers, and underscores. When an identifier is encountered in an expression, it is interprets and evaluated based on the context. Here are the possible scenarios:

* If the identifier is preceded by a number, it is interpreted as a Quantity. For example, "20px". When a quantity is evaluated, the `evaluateQuantity` method of the `EvaluationContext` class is invoked. For more information, see the EvaluationContext section below.
* If the identifier is followed by an opening parenthesis, it is interpreted as a function. For example, `now()`. When a function is evaluated, the parameters (if any) are evaluated first, then the function is called. For more information, see the Functions and EvaluationContext sections below.
* Otherwise, the identifier is interpreted as an object. In this case it is evaluated in the following manner:
    1. If the identifier is equal to "null" (case insensitive), it is evaluated to `null`.
    2. If the identifier is equal to "this" (case insensitive), it is evaluated as the *target* object. To read more about the target of an evaluation, check the EvaluationContext section below.
    3. The identifier is attempted to be resolved by calling the `resolveObject` method of the `EvaluationContext` class.
    4. If the resolution failed, the identifier is looked up in the variable table of the `EvaluationContext` class. This check is case sensitive. To read more about variables and how to define them, check the EvaluationContext section below.
    5. If the identifier is equal to "true" or "false" (case insensitive), it is evaluated to `true` or `false` respectively. Due to this evaluation processing, the literals true and false can be effectively overridden by either overriding the `resolveObject` method or by adding variables with the same name.
    6. If the target object is not `null`, the identifier is interpreted as a property of the *target* object.
    7. If all of the above fail, an `EvaluationException` is thrown.

If the identifier is part of a member reference chain (for example, `a.Prop.Value`, or `func().SomeFlag`), the above rules apply only to the leftmost identifier. The subsequent identifiers are all interpreted and evaluated as properties of the resulting object.

## Functions

As mentioned in the previous section, a function is an identifier, followed by an opening parenthesis `(`. When a function is evaluated, first all of its arguments are evaluated (in order of appearance). The parameters themselves can be function calls. Here are some examples of functions:

    sqrt(pow(x) + pow(y))
    mid("Good afternoon", 0, 4) == "Good"

The following rules apply when calling a functions:

* If a function is called with an insufficient number of arguments, an `EvaluationException` is thrown.
* If a function is called with excess arguments, the redundant arguments are ignored.
* If a function does not exist, the Evaluator checks if its a custom function (see the section below). If that's not the case, an `EvaluationException` is thrown.

The function naming follows the established in Java Camel-case convention.

### Built-in functions

The following table lists all built-in functions, their expected arguments and result.

| Function Signature | Description |
|--------------------|-------------|
| `now(): Date` | Returns the current system date and time, expressed in UTC. |
| `e(): Double` | Represents the natural logarithmic base, specified by the constant, e. |
| `pi(): Double` | Represents the ratio of the circumference of a circle to its diameter, specified by the constant, π. |
| `today(): Date` | Returns the current system date, expressed in UTC. |
| `abs(value: Double): Double` | Returns the absolute value of the specified Double number. |
| `asc(value: String): Integer` | Returns an Integer representing the character code corresponding to the first letter in a string. |
| `atn(value: Double): Double` | Returns the angle, measured in radians, whose tangent is the specified number. |
| `chr(c: Integer): String` | Returns a String containing the character associated with the specified character code. |
| `cbool(value: Object): Boolean` | Converts the specified object to a boolean. |
| `cdate(value: Object): Date` | Converts the specified object to Date. If conversion is not possible, an IllegalArgumentException will be thrown. |
| `cdbl(value: Object): Double` | Converts the specified object to Double. If conversion is not possible, returns 0. |
| `cint(value: Object): Integer` | Converts the specified object to Integer. If conversion is not possible, returns 0. |
| `clong(value: Object): Long` | Converts the specified object to Long. If conversion is not possible, returns 0. |
| `csng(value: Object): Float` | Converts the specified object to Float. If conversion is not possible, returns 0. |
| `cstr(value: Object): String` | Converts the specified object to String. If converting numbers and rounding is needed, but rounding is disabled, an ArithmeticException will be thrown. If value is null, returns an empty string. |
| `cos(value: Double): Double` | Returns the cosine of the specified angle, expressed in radians. |
| `acos(value: Double): Double` | Returns the angle, expressed in radians, whose cosine is the specified number. |
| `exp(value: Double): Double` | Returns e, raised to the specified power. |
| `int(value: Double): Integer` | Returns the integer portion of the specified number. |
| `isNull(value: Object): Boolean` | Returns a boolean value indicating whether the specified value is null. This is equivalent to writing "value != null". |
| `isNumeric(value: Object): Boolean` | Returns a boolean value indicating whether the specified object can be converted to a number. |
| `lcase(value: String): String` | Returns the specified string converted to lowercase. |
| `len(value: String): Integer` | Returns the number of characters in the specified string. |
| `log(value: Double): Double` | Returns the natural (base e) logarithm of the specified value. |
| `pow(value: Double, power: Double [= 2.0]): Double` | Returns a specified number raised to the specified power. If power is omitted, the default is 2.0. |
| `rnd(type: Integer): Double` | Returns a pseudorandom double value. If type is less than 0, it is used as a seed. If type is 0, the random value generated by the previous call of this function is returned. If type is greater than 0, a new random number in the range \[0..1) is generated. |
| `sgn(value: Double): Double` | Returns a value indicating the sign of the specified number. If value is negative, returns -1.0, if value is positive, returns 1.0. Otherwise, returns 0.0. |
| `sin(value: Double): Double` | Returns the sine of the specified angle, expressed in radians. |
| `asin(value: Double): Double` | Returns the angle, expressed in radians, whose sine is the specified number. |
| `space(count: Integer): String` | Returns a string consisting of the specified number of spaces. |
| `sqr(value: Double): Double` | Returns a double specifying the square root of the specified number. |
| `sqr(value: Double): Double` | Returns a double specifying the square root of the specified number. |
| `str(value: Double): String` | Returns a string representation of the specified number. |
| `strReverse(value: String): String` | Returns a string in which the character order of the specified string is reversed. |
| `tan(value: Double): Double` | Returns the tangent of the specified angle. |
| `trim(value: String): String` | Returns a string containing a copy of the specified string without leading and trailing spaces. |
| `ucase(value: String): String` | Returns the specified string converted to uppercase. |
| `inStr(a: String, b: String): Integer` | Returns an integer specifying the position of the first occurrence of one string within another. |
| `inStrRev(a: String, b: String): Integer` | Returns an integer specifying the position of the last occurrence of one string within another. |
| `left(a: String, length: Integer): String` | Returns a string containing the specified number of characters from the left side of the specified string. |
| `right(a: String, length: Integer): String` | Returns a string containing the specified number of characters from the right side of the specified string. |
| `round(value: Double): Double` | Rounds the specified value to the specified precision. |
| `strComp(a: String, b: String): Integer` | Returns an integer indicating the lexicographical ordering of the specified strings. |
| `string(count: Integer, c: Integer): String` | Returns a string containing the specified character repeating the specified number of times. |
| `iif(condition: Boolean, a: Object, b: Object): Object` | Returns one of two parts, depending on the value of the specified condition. If condition is true, returns a; otherwise, returns b. |
| `mid(value: String, pos: Integer, length: Integer): String` | Returns a string containing the specified number of characters from the specified string. |
| `replace(value: String, oldValue: String, newValue: String): String` | Returns a string in which the specified substring has been replaced with another substring. |

### Custom functions

Custom functions can be supplied during expression evaluation by either inheriting from the `EvaluationContext` class and overriding the `invokeFunction` method or by providing the so called `dispatchFunctionCall` callback when creating the `EvaluationContext` instance. The first method requires the creation of a custom `EvaluationContext` class. This is the recommended approach when you also need to override other methods, such as `evaluateIdentifier`, `evaluateQuantity`, etc. To read more about how to extend the `EvaluationContext` class, check the EvaluationContext section below.

The second approach requires passing a special callback function to the `EvaluationContext` constructor. The signature of the callback is: `BiFunction<String, Object[], FunctionEvaluationResult>`, where the first parameter is the name of the custom function, the second is the evaluated parameters, and the third represents the return value. When the custom function is evaluated, the return value should be wrapped in an instance of the `FunctionEvaluationResult` class. If the custom function is not recognized, the callback should return a `FunctionEvaluationResult.notEvaluated()` value. Here is an example of a custom function callback:

```java
EvaluationContext context = new EvaluationContext(null, Locale.ROOT,
    (name, params) -> {
        if (name.equals("customFunction")) {
            return new FunctionEvaluationResult("result");
        }
 
        return FunctionEvaluationResult.notEvaluated();
    });
```

## Formatting

The value of an evaluated expression can be formatted using standard formatting specifiers, such as "0.##", "MM/dd/yyyy", etc. To do this, suffix the expression with the `@` sign, followed by the formatting specifier. For example, if an expression evaluates to a double, you can format this double value to contain no more than two digits after the floating point:

    10.0 / 3.0 @ "0.##"

The above expression should produce the string `"3.33"` when evaluated.

If the evaluated value is not a number or a date, the formatting specifier is ignored and the value is simply converted to string using its `toString()` method. For additional information about decimal formatting, check [this tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html). For information about date formatting, check the [SimpleDateFormat](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) class in Java.

### Culture

When formatting an expression, the locale used is the one specified by the EvaluationContext (ROOT by default). You can either specify another locale when creating the `EvaluationContext` object, or you can specify the locale directly in the expression by using the following syntax:

    10.0 / 3.0 @ "0.##" : "de"

The above expression should produce the string `"3,33"` when evaluated because the value will be formatted using the German locale.

### Discard

In rare cases it might be desirable to ignore the evaluation of the expression. This can be done by using an exclamation symbol `!` at the end of the expression, right after the formatting specifier:

    10.0 / 3.0 @ !

The above expression should produce `null` when evaluated.

## Normalization and embedded expressions

In order to process expressions, the Expression Evaluator expects them to be in the so-called *canonical* form. This means that the expression needs to be enclosed in square brackets: `[` and `]`. The process of converting an expression to its canonical form is called *normalization*. By default the expressions are automatically normalized when compiled. However, you can use already normalized expressions, for example: `[1 + 2]`. This is especially useful when you want to create embedded expressions. Embedded expressions are basically expressions (in canonical form), embedded in a string. The whole string can then be compiled and evaluated. Consider the following example:

    Order volume [y] is outside limits ([avg - k * sd], [avg + k * sd]).

The above string contains three embedded expressions, namely, `[y]`, `[avg - k * sd]`, and `[avg + k * sd]`. When the string is processed, the individual expressions are compiled and evaluated using the same EvaluationContext, then the result of each expression is concatenated with the string parts to produce a single string value.

# API

The Expression Evaluator library consists of several classes, notably: `Compiler`, `ByteCode`, and `EvaluationContext`.

## Compiler

The Compiler class is used to compile an expression into *byte code*. The compiler accepts expressions in both canonical and non-canonical form, as well as strings with embedded expressions. The result of the compilation is an instance of the `ByteCode` class (see below), which can then be evaluated multiple times using one or different contexts. The compilation can throw various exceptions depending on the validity of the specified expressions. The most common exceptions are:

* `IllegalArgumentException` with the message "Failed to recognize character 'X' at position N within the input string 'S'." – This exception can happen during lexical analysis when encountering an invalid character. X is the character in question, N is its position within the string, and S is the original expression.
* `ParsingException` with the message "Unexpected end of the token stream. The expression is most likely incomplete." – This exception happens during construction of the parse tree if the parser reached the end of the token stream before constructing a valid tree.

Keep in mind that compilation is slow. Ideally, you want to compile an expression once and then evaluate it multiple times.

Below you can see an example of how to use the compiler:

```java
ByteCode byteCode = Compiler.compile("Order volume [y] is outside limits ([avg - k * sd], [avg + k * sd]).");
```

There are several compile overloads, which deal with expressions that are not normalized.

## ByteCode

Once an expression is compiled into a `ByteCode` object, it can be evaluated by calling the evaluate method and passing an instance of the `EvaluationContext` class. Here is an example:

```java
Object result = byteCode.evaluate(new EvaluationContext(null));
```

During evaluation the `EvaluationException` can be thrown. The following list covers some common cases when this could happen:

* When performing arithmetic operations between incompatible operands. For example, between Boolean and Date.
* When performing operations between operands of custom types.
* When calling a function with insufficient number of arguments.
* When calling an undefined function.
* When evaluating an undefined identifier (see the Identifiers section).
* If a member reference (see the Dot operator) cannot be found.

The customization of the evaluation process is achieved entirely through the context object passed as an argument. The next section describes this in greater details.

## EvaluationContext

The EvaluationContext provides means to customize the evaluation of an expression. When creating an `EvaluationContext` object you provide one or more of the following to its constructor:

* *target* – This is a mandatory parameter, but it is acceptable to pass in `null`. The target object is used when resolving the `this` keyword or when evaluating identifiers which cannot be resolved by other means (see Identifiers). In this case, the identifiers are interpreted as properties of the target object (if one is provided).
* *locale* – This is an optional parameter. If it is not provided, the context will use the ROOT locale. The locale is used when formatting (see the Formatting section) or when calling some of the built-in function – for example, lcase, ucase, and those involved in parsing and formatting numbers and dates.
* *dispatchFunctionCall* – This is an optional parameter. If specified, this callback will be invoked for all calls to functions that are not in the built-in set. See the Custom function section for an example.
* *parseObject* – This is an optional parameter. Currently, this callback is invoked only when parsing color literals in expressions. If the callback is not specified, the color literals are evaluated as strings. A color literal starts with the hash tag sign `#` followed by 3 pairs of hexadecimal numbers, where the three pairs specify the red, green, and blue channels of the color respectively. For example, `#FF00CC`.

### Variables

The context also provides means to define variables. This is done through the `EvaluationContext.getLocalVars()` method. To define a variable, simply add it to the dictionary returned by this method:

```java
EvaluationContext context = new EvaluationContext(null);
context.getLocalVars().put("myVariable", new CustomObject());
```

The variable dictionary is used when evaluating identifiers (see the Identifiers section).

### Extending the context

Finally, the EvaluationContext can be inherited in order to override of the following methods:

* `evaluateIdentifier` – This method is the entry point of identifier evaluation. If you override it, make sure to invoke the base implementation or the identifier resolution described in the Identifiers section will not work. A safer way to plug into the identifier resolution is by using the `resolveObject` method.
* `resolveObject` – This method is invoked when resolving an identifier. For the complete sequence used when resolving an identifier, see the Identifiers section.
* `evaluateQuantity` – This method is invoked when evaluating a Quantity. See the Quantity type for more information. For an implementation example, check the code below.
* `evaluateMember` – This method is invoked when a member reference cannot be resolved. For example, if you have the expression `[this.Prop]` and the target object does not have a method `getProp()`.
* `invokeFunction` – This method will be invoked whenever a function call is encountered in the expression. The base implementation handles the built-in functions so make sure to call it when overriding `invokeFunction`. Here is an example:

```java
final class CustomContext extends EvaluationContext {
    public CustomContext() {
        super(null);
    }
 
    @Override
    public Object invokeFunction(String name, Object... p) {
        switch (name) {
            case "factorial":
                return factorial((Integer)p[0]);
        }
 
        return super.invokeFunction(name, p);
    }
 
    private static int factorial(int v) {
        if (v <= 1) {
            return v;
        }
        return v * factorial(v - 1);
    }
 
    @Override
    protected double evaluateQuantity(double value, String unit) {
        if (unit.equals("pt")) {
            return value * 96 / 72;
        }
        return super.evaluateQuantity(value, unit);
    }
}
```
