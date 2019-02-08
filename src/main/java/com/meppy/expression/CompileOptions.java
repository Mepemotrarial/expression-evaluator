package com.meppy.expression;

/**
 * Contains options for the {@link Compiler#compile} method.
 */
public class CompileOptions {
    private final boolean interpretCircumflexAsPower;

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions() {
        this(false);
    }

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions(boolean interpretCircumflexAsPower) {
        this.interpretCircumflexAsPower = interpretCircumflexAsPower;
    }

    /**
     * Gets a value indicating whether circumflex symbols (^) are interpreted as raising to power,
     * rather than exclusive-OR. Raising to power also has different priority than XOR.
     */
    public boolean getInterpretCircumflexAsPower() {
        return interpretCircumflexAsPower;
    }
}