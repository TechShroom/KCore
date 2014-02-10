package k.core.util.math;

import java.util.HashMap;

public enum EOperation {
    PLUS("+"), SUBTRACT("-"), DIVIDE("/"), MULTIPLY("*"), POW("^"), SROOT(
            "\u221A"), FACT("!");

    /**
     * The proper string mappings
     */
    private static HashMap<String, EOperation> mapping = new HashMap<String, EOperation>();

    static {
        for (EOperation v : values()) {
            mapping.put(v.op_s, v);
        }
    }

    /**
     * The internal operand
     */
    private String op_s;

    EOperation(String operationString) {
        op_s = operationString;
    }

    /**
     * Does proper POW conversions?
     * 
     * @param in
     *            - the string to wrap in a POW expression
     * @return "^(&lt;in&gt;)"
     */
    public String constructPow(String in) {
        return this.equals(POW) ? "^(" + in + ")" : op_s;
    }

    /**
     * Gets the backing operation string used to match in strings.
     * 
     * @return the backing operand
     */
    public String getOp() {
        return op_s;
    }

    /**
     * This method protects against words and phrases in strings. Accepts
     * <tt>pi</tt> and <tt>e</tt> in addition to PEMDAS.
     * 
     * @param line
     *            - the string to check
     * @return if the string could be solved with
     *         {@link ExtraMath#evalExpression(String)}, assuming it is not
     *         invalidly formatted.
     */
    public static boolean hasOP(String line) {
        /*
         * only allow strings made entirely of operators and the default number
         * (this works with negatives!)
         * 
         * note: this allows for invalid formats, this just checks against
         * words.
         */
        String newline = line.replaceFirst(String.format(
                "^(%s|%s|\\(|\\)|\\.|pi|e)+$", Operation.OPT_NEG_NUM_REGEX,
                Operation.OPERATIONS_REGEX), "");
        return !newline.equals(line);
    }

    /**
     * Returns the proper EOp for the given string operation
     * 
     * @param s
     *            - a string that contains an operation as defined by the
     *            {@link Operation#OPERATIONS_REGEX}
     * @return the matching EOperation
     */
    public static EOperation getOp(String s) {
        return mapping.get(s.replaceAll(Operation.OPT_ALL_NEG_NUM_REGEX + "("
                + Operation.OPERATIONS_REGEX + ")"
                + Operation.OPT_ALL_NEG_NUM_REGEX, "$2"));
    }
}
