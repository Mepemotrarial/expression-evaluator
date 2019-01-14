package com.meppy.expressions;

/**
 * Contains options for the {@link Compiler#compile} method.
 */
public class CompileOptions {
    private final boolean normalize;
    private final boolean interpretExclamationAsReference;
    private final boolean interpretCircumflexAsPower;

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions() {
        this(true, false, false);
    }

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions(boolean normalize) {
        this(normalize, false, false);
    }

    /**
     * Initializes a new instance of the {@link CompileOptions} class.
     */
    public CompileOptions(boolean normalize, boolean interpretExclamationAsReference, boolean interpretCircumflexAsPower) {
        this.normalize = normalize;
        this.interpretExclamationAsReference = interpretExclamationAsReference;
        this.interpretCircumflexAsPower = interpretCircumflexAsPower;
    }

    /**
     * Gets a value indicating whether to encase source in square brackets if it is not encased already.
     */
    public boolean getNormalize() {
        return normalize;
    }

    /**
     * Gets a value indicating whether exclamation marks (!) are interpreted as references, rather than NOT.
     */
    public boolean getInterpretExclamationAsReference() {
        return interpretExclamationAsReference;
    }

    /**
     * Gets a value indicating whether circumflex symbols (^) are interpreted as raising to power,
     * rather than exclusive-OR. Raising to power also has different priority than XOR.
     */
    public boolean getInterpretCircumflexAsPower() {
        return interpretCircumflexAsPower;
    }
}
