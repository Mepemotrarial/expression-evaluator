# Expression Evaluator

Allows parsing and evaluation of simple expresions. The example below demonstrates how to use the evaluator:

```java
ByteCode byteCode = Compiler.compile("The distance between points [a] and [b] is [Sqrt(Pow(a.X - b.X) + Pow(a.Y - b.Y))].", false);
EvaluationContext evaluationContext = new EvaluationContext(null);
evaluationContext.getLocalVars().put("a", new Point(5, 10));
evaluationContext.getLocalVars().put("b", new Point(11, 8));
System.out.println(byteCode.evaluate(evaluationContext));
```

Running the example should print the following in the console:

`The distance between points java.awt.Point[x=5,y=10] and java.awt.Point[x=11,y=8] is 6.324555320336759.`

Once compiled, a byte code can be evaluated many number of times, in different contexts. You can also define custom functions and identifiers, format values (potentially in different cultures), and so on.
