package k.core.test;

import k.core.util.math.UnlimitedDouble;

public class MathTest {
    public static void main(String[] args) {
        UnlimitedDouble a = new UnlimitedDouble("4");
        System.err.println(a);
        UnlimitedDouble b = new UnlimitedDouble("60.0");
        System.err.println(b);
        UnlimitedDouble c = a.multiply(b); // a * b test
        System.err.println("UD c = " + c + ", should be " + 4 * 60);

        c = a.add(b); // a + b test
        System.err.println("UD c = " + c + ", should be " + (4 + 60));

        // invalid tests
        try {
            c = new UnlimitedDouble("!");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
        }
        try {
            c = new UnlimitedDouble("-1");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this shouldn't happen!)");
        }
        try {
            c = new UnlimitedDouble("1.00.02");
            System.err.println(c);
        } catch (NumberFormatException nfe) {
            System.err.println("invalid (this should happen!)");
        }

        // test to ensure valid things are not marked as invalid
        c = new UnlimitedDouble("-001.0003");
        System.err.println(c);
    }

}
