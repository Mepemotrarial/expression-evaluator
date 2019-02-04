package com.meppy.expression;

/**
 * Contains options for the {@link Compiler#compile} method.
 */
public class CompileOptions {
    private final boolean normalize;
    private final boolean interpretCircumflexAsPower;

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions() {
        this(true, false);
    }

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions(boolean normalize) {
        this(normalize, false);
    }

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions(boolean normalize, boolean interpretCircumflexAsPower) {
        this.normalize = normalize;
        this.interpretCircumflexAsPower = interpretCircumflexAsPower;
    }

    /**
     * Gets a value indicating whether to encase source in square brackets if it is not encased already.
     */
    public boolean getNormalize() {
        return normalize;
    }

    /**
     * Gets a value indicating whether circumflex symbols (^) are interpreted as raising to power,
     * rather than exclusive-OR. Raising to power also has different priority than XOR.
     */
    public boolean getInterpretCircumflexAsPower() {
        return interpretCircumflexAsPower;
    }
}