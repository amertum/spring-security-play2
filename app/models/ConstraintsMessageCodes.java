package models;

/**
 * @see "play.api.data.validation.Constraints"
 * @see "ValidationMessages.properties"
 */
public final class ConstraintsMessageCodes {

    private ConstraintsMessageCodes() {}

    public static final String ERROR_FORM = "error.form";
    public static final String ERROR_VERSION = "error.version";


    public static final String ERROR_INVALID = "error.invalid";
    public static final String ERROR_REQUIRED = "error.required";
    public static final String ERROR_NUMBER = "error.number";
    public static final String ERROR_REAL = "error.real";
    /** With params {@code scale} and {@code precision}. */
    public static final String ERROR_REAL_PRECISION = "error.real.precision";
    /** With param {@code value}. */
    public static final String ERROR_MIN = "error.min";
    /** With param {@code value}. */
    public static final String ERROR_MIN_STRICT = "error.min.strict";
    /** With param {@code value}. */
    public static final String ERROR_MAX = "error.max";
    /** With param {@code value}. */
    public static final String ERROR_MAX_STRICT = "error.max.strict";
    /** With params {@code minLength} and {@code maxLength}. */
    public static final String ERROR_LENGTH = "error.length";
    /** With param {@code length}. */
    public static final String ERROR_MIN_LENGTH = "error.minLength";
    /** With param {@code length}. */
    public static final String ERROR_MAX_LENGTH = "error.maxLength";
    public static final String ERROR_EMAIL = "error.email";
    /** With param {@code pattern}. */
    public static final String ERROR_PATTERN = "error.pattern";
    public static final String ERROR_CONFIRMATION = "error.confirmation";

}