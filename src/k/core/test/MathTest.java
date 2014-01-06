package k.core.test;

import k.core.util.math.UnlimitedDouble;

public class MathTest {
    public static void main(String[] args) {
        UnlimitedDouble a = new UnlimitedDouble("4"), b = new UnlimitedDouble(
                "60.0"), c = UnlimitedDouble.empty();
        System.err.println(a);
        System.err.println(b);
        System.err.println(a.compareTo(b));
        System.err.println(b.compareTo(a));
        add(a, b);
        multiply(a, b);

        invalid();

        // test to ensure valid things are not marked as invalid
        c = new UnlimitedDouble("-001.0003");
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

    public static void invalid() {
        UnlimitedDouble c = UnlimitedDouble.empty();
        // invalid tests
        try {
            c = new UnlimitedDouble("!");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
            nfe.printStackTrace();
        }
        try {
            c = new UnlimitedDouble("-1");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this shouldn't happen!)");
            nfe.printStackTrace();
        }
        try {
            c = new UnlimitedDouble("1.00.02");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
            nfe.printStackTrace();
        }
    }

}
