package k.core.util.math;

import java.util.HashMap;

public enum EOperation {
    PLUS("+"), SUBTRACT("-"), DIVIDE("/"), MULTIPLY("*"), POW("^"), SROOT(
            "\u221A"), FACT("!");

    private static HashMap<String, EOperation> mapping = new HashMap<String, EOperation>();

    static {
        for (EOperation v : values()) {
            mapping.put(v.op_s, v);
        }
    }

    private String op_s;

    EOperation(String operationString) {
        op_s = operationString;
    }

    public String constructPow(String in) {
        return this.equals(POW) ? "^(" + in + ")" : op_s;
    }

    public String getOp() {
        return op_s;
    }

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

    public static EOperation getOp(String s) {
        return mapping.get(s.replaceAll(Operation.OPT_ALL_NEG_NUM_REGEX + "("
                + Operation.OPERATIONS_REGEX + ")"
                + Operation.OPT_ALL_NEG_NUM_REGEX, "$2"));
    }
}
