package k.core.util.math;

import static k.core.util.math.UnlimitedDouble.*;

public class MathTest {
    public static void main(String[] args) {
        UnlimitedDouble a = newInstance("4.010"), b = newInstance("60.0"), c = UnlimitedDouble
                .empty();
        System.err.println(a);
        System.err.println(b);
        System.err
                .println(String.format("%s > %s = ", a, b) + a.greaterThan(b));
        System.err.println(String.format("%s >= %s = ", a, b)
                + a.greaterThanOrEqual(b));
        System.err
                .println(String.format("%s > %s = ", b, a) + b.greaterThan(a));
        System.err.println(String.format("%s >= %s = ", b, a)
                + b.greaterThanOrEqual(a));
        add(a, b);
        subtract(a, b);
        multiply(a, b);

        invalid();

        // test to ensure valid things are not marked as invalid
        c = newInstance("-001.0003");
        System.err.println(c);
    }

    public static void multiply(UnlimitedDouble a, UnlimitedDouble b) {
        UnlimitedDouble c = a.multiply(b); // a * b test
        System.err.println("UD c = " + c);
    }

    public static void add(UnlimitedDouble a, UnlimitedDouble b) {
        UnlimitedDouble c = a.add(b); // a + b test
        System.err.println("UD c = " + c);
    }

    public static void subtract(UnlimitedDouble a, UnlimitedDouble b) {
        UnlimitedDouble c = a.subtract(b); // a - b test
        System.err.println("UD c = " + c);
    }

    public static void invalid() {
        UnlimitedDouble c = UnlimitedDouble.empty();
        // invalid tests
        try {
            c = newInstance("!");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
            // nfe.printStackTrace();
        }
        try {
            c = newInstance("-1");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this shouldn't happen!)");
            // nfe.printStackTrace();
        }
        try {
            c = newInstance("1.00.02");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
            // nfe.printStackTrace();
        }
    }

}
