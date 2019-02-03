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

## Operator priorities

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
