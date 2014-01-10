package k.core.util.math;

import java.util.ArrayList;

public class Operation {
    private UnlimitedDouble num1, num2;
    private EOperation op;
    private String original;

    public static final String OPERATIONS_REGEX = "[\\Q+-*/^\\E]",
            NUM_REGEX = "\\d+(\\.\\d+)?", OPT_NEG_NUM_REGEX = "\\-?"
                    + NUM_REGEX, OPT_NNEG_NUM_REGEX = "n?" + NUM_REGEX,
            OPT_ALL_NEG_NUM_REGEX = "[n\\-]?" + NUM_REGEX;

    public Operation(UnlimitedDouble first, UnlimitedDouble second,
            EOperation operation, String orig) {
        num1 = first;
        num2 = second;
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
     *         {@link UnlimitedDouble#empty() } if not valid.
     */
    public UnlimitedDouble doOperation() {
        UnlimitedDouble ret = UnlimitedDouble.empty();
        // System.out.println("{Operation} Computing " + this.original);
        switch (op) {
        case DIVIDE:
            System.err
                    .println("UD does not support divison, falling back to doubles");
            ret = UnlimitedDouble.parseUD(Double.toString(Double
                    .parseDouble(num1.toString())
                    / Double.parseDouble(num2.toString())));
            break;
        case FACT:
            ret = ExtraMath.factorial(num1);
            break;
        case MULTIPLY:
            System.err
                    .println("UD does not support FULL multiplication, falling back to doubles");
            ret = UnlimitedDouble.parseUD(Double.toString(Double
                    .parseDouble(num1.toString())
                    * Double.parseDouble(num2.toString())));
            break;
        case PLUS:
            ret = num1.add(num2);
            break;
        case POW:
            ret = num1.pow(num2);
            break;
        case SROOT:
            ret = UnlimitedDouble.parseUD(Double.toString(Math.sqrt(Double
                    .parseDouble(num1.toString()))));
            break;
        case SUBTRACT:
            ret = num1.subtract(num2);
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
