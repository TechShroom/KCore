package k.core.test;

import k.core.util.math.UnlimitedDouble;

public class MathTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        UnlimitedDouble a = new UnlimitedDouble("4");
        UnlimitedDouble b = new UnlimitedDouble("60");
        UnlimitedDouble c = a.multiply(b); // a * b test
        System.err.println("UD c = " + c);

        c = a.add(b); // a + b test
        System.err.println("UD c = " + c);

        System.err.println("\n\n\n\n");

        // invalid tests
        c = new UnlimitedDouble("!");
        c = new UnlimitedDouble("-1");
        c = new UnlimitedDouble("1.00.02");

        // test to ensure valid things are not marked as invalid
        c = new UnlimitedDouble("1.0003");
    }

}
