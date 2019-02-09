package com.meppy.expression.test;

import com.meppy.expression.ByteCode;
import com.meppy.expression.CompileOptions;
import com.meppy.expression.Compiler;
import com.meppy.expression.EvaluationContext;
import com.meppy.expression.FunctionEvaluationResult;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Locale;

public final class Test {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        ByteCode cc = Compiler.compile("The distance between points [a] and [b] is [Sqrt(Pow(a.X - b.X) + Pow(a.Y - b.Y))].");
        EvaluationContext ec = new EvaluationContext(null);
        ec.getLocalVars().put("a", new Point(5, 10));
        ec.getLocalVars().put("b", new Point(11, 8));
        System.out.println(cc.evaluate(ec));

        ByteCode code0 = Compiler.compile("Result is: [(1 + fact(Value)) / 3 @ \"#.00\" : \"bg_BG\"]", new CompileOptions(false));
        ByteCode code01 = Compiler.compile("Result is: [(1 + fact(Value)) / 3]", new CompileOptions(false));
        ByteCode code1 = Compiler.compile("[(1 + fact(Value)) / 3 @ \"#.00\" : \"bg_BG\"]", new CompileOptions(false));
        ByteCode code2 = Compiler.compile("[(1 + fact(Value)) / 3]", new CompileOptions(false));
        ByteCode code3 = Compiler.compile("[(1 + Abs(-10)) / 3]", new CompileOptions(false));
        ByteCode code4 = Compiler.compile("[(1 + 10) / 3]", new CompileOptions(false));
        EvaluationContext context = new EvaluationContext(new Pojo(5), Locale.ROOT,
            (name, params) -> {
                switch (name) {
                    case "fact":
                        return new FunctionEvaluationResult((double)params[0] + 5);
                }

                return FunctionEvaluationResult.notEvaluated();
            });

        ByteCode predicate = Compiler.compile("y > oaAvg + k * oaSd || y < oaAvg - k * oaSd", new CompileOptions());
        ByteCode code_2 = Compiler.compile("Order volume [y] is outside limits ([oaAvg - k * oaSd], [oaAvg + k * oaSd]).", new CompileOptions(false));

//        execute(code0, context, "Performing 1 million calculations [concatenation, custom functions, property lookup, formatting] took %1$.2f seconds.");
//        execute(code01, context, "Performing 1 million calculations [concatenation, custom functions, property lookup] took %1$.2f seconds.");
//        execute(code1, context, "Performing 1 million calculations [custom functions, property lookup, formatting] took %1$.2f seconds.");
//        execute(code2, context, "Performing 1 million calculations [custom functions, property lookup] took %1$.2f seconds.");
//        execute(code3, context, "Performing 1 million calculations [functions] took %1$.2f seconds.");
//        execute(code4, context, "Performing 1 million calculations [] took %1$.2f seconds.");

        context = new EvaluationContext(null);
        //context.getLocalVars().put("oa", new Aggregate(15.25, 10.5));
        context.getLocalVars().put("oaAvg", 15.25);
        context.getLocalVars().put("oaSd", 10.5);
        context.getLocalVars().put("k", 0.7);
        context.getLocalVars().put("y", 7.8);
        execute(predicate, context, "Performing 1 million calculations [DBM rule expression] took %1$.2f seconds.");

        context.getLocalVars().put("y", 7.8);
        if ((Boolean)predicate.evaluate(context)) {
            System.out.println(code_2.evaluate(context));
        }
        context.getLocalVars().put("y", 10);
        if ((Boolean)predicate.evaluate(context)) {
            System.out.println(code_2.evaluate(context));
        }
    }

    private static void execute(ByteCode code, EvaluationContext context, String message) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000; i++) {
            code.evaluate(context);
        }
        double elapsed = (System.currentTimeMillis() - time) / 1000.0;
        System.out.println(String.format(message, elapsed));
    }
}


final class Pojo {
    private final double value;
    public Pojo(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

final class Aggregate {
    private final double avg;
    private final double sd;
    public Aggregate(double avg, double sd) {
        this.avg = avg;
        this.sd = sd;
    }
    public double getAvg() {
        return avg;
    }
    public double getSd() {
        return sd;
    }
}