package k.core.util.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public class Operation {
    private BigDecimal num1, num2;
    private EOperation op;
    private String original;

    public static final String OPERATIONS_REGEX = "[\\Q+-*/^\\E]",
            NUM_REGEX = "\\d+(\\.\\d+)?", OPT_NEG_NUM_REGEX = "\\-?"
                    + NUM_REGEX, OPT_NNEG_NUM_REGEX = "n?" + NUM_REGEX,
            OPT_ALL_NEG_NUM_REGEX = "[n\\-]?" + NUM_REGEX;

    public Operation(double first, double second, EOperation operation,
            String orig) {
        num1 = BigDecimal.valueOf(first);
        num2 = BigDecimal.valueOf(second);
        op = operation;
        original = orig;
        if (op == null) {
            throw new RuntimeException("original produced a bad result: "
                    + original);
        }
        // System.out.println("{ConstrOperation} original = " + original);
    }

    public String getOrig() {
        return original;
    }

    @Override
    public String toString() {
        return original;
    }

    /**
     * 
     * @return the number created by this operation, or
     *         {@link java.lang.Double#NaN } if not valid.
     */
    public double doOperation() {
        double ret = Double.NaN;
        // System.out.println("{Operation} Computing " + this.original);
        switch (op) {
        case DIVIDE:
            ret = num1.divide(num2, MathContext.DECIMAL128).doubleValue();
            break;
        case FACT:
            ret = ExtraMath.factorial(num1.doubleValue());
            break;
        case MULTIPLY:
            ret = num1.multiply(num2).doubleValue();
            break;
        case PLUS:
            ret = num1.add(num2).doubleValue();
            break;
        case POW:
            ret = Math.pow(num1.doubleValue(), num2.doubleValue());
            break;
        case SROOT:
            ret = Math.sqrt(num1.doubleValue());
            break;
        case SUBTRACT:
            ret = num1.subtract(num2).doubleValue();
            break;
        default:
            break;
        }
        // System.out.println("{Operation} Returning " + ret);
        return ret;
    }

    /**
     * Computes all {@link org.djl.Operation Operation} in the array.
     * 
     * @param ops
     *            an array of pre-sorted EMDAS Operations.
     * @return the results of the given Operation array. The arrays length is
     *         equal to <code>ops.length</code>
     */
    public static String[] compute(Operation[] ops) {
        ArrayList<String> temp = new ArrayList<String>();
        for (Operation o : ops) {
            temp.add(o.doOperation() + "");
        }
        return temp.toArray(new String[0]);
    }
}
