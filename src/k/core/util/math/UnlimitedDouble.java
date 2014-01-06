package k.core.util.math;

import k.core.util.Helper.BetterArrays;
import k.core.util.arrays.ResizableArray;
import k.core.util.strings.Strings;

public class UnlimitedDouble implements Cloneable, Comparable<UnlimitedDouble> {
    /**
     * An empty UD for use anywhere you need a pure empty value (think of it as
     * 'null') <br>
     * <br>
     * A special use for this is the identity property. In all operations, this
     * acts as the identity number for that operation. <br>
     * <br>
     * The methods that implement the comparison operators (>, <, >=, <=) are
     * undefined for this value.
     */
    private static final UnlimitedDouble EMPTY = new UnlimitedDouble("0");
    static {
        EMPTY.digits = new ResizableArray(new char[0]);
        EMPTY.decimal = EMPTY.digits.size();
        EMPTY.negative = false;
    }

    /**
     * The digits, not including the decimal or negative.
     */
    private ResizableArray digits = null;
    /**
     * The decimal place, inserted between <tt>decimal</tt> and
     * <tt>decimal+1</tt>
     */
    private int decimal = 0xDEADBEEF;

    /**
     * If this UD is negative or not
     */
    private boolean negative = false;

    public UnlimitedDouble(String value) {
        parse0(value);
    }

    public UnlimitedDouble(UnlimitedDouble value) {
        // clone digits so that we don't overwrite
        digits = value.digits.clone();
        decimal = value.decimal;
        negative = value.negative;
    }

    /* Private methods */

    /**
     * Handles parsing for String constructor
     * 
     * @param value
     *            - the original string
     */
    private void parse0(String value) {
        if (Strings.count(value, '.') > 1) {
            throw new NumberFormatException(value);
        }
        // check for negatives, but do not include them
        String withoutDec = value.replace(".", "")
                .substring(((negative = value.charAt(0) == '-') ? 1 : 0))
                .replaceAll("0+(?!0)(\\d+(\\.\\d+)?)", "$1");
        // only digits
        if (!withoutDec.matches("^\\d+$")) {
            throw new NumberFormatException(value);
        }
        decimal = value.indexOf('.');
        // convert string
        digits = new ResizableArray(withoutDec.toCharArray());
        // decimal place = length of digits when there is none
        if (decimal < 0) {
            decimal = digits.size();
        }
    }

    private int rtlDecimal() {
        return digits.size() - decimal;
    }

    /* (private) Static methods */

    /**
     * Makes a and b's decimal place line up for adding and subtracting.
     * 
     * @param a
     *            - UD one
     * @param b
     *            - UD two
     */
    private static void pad(UnlimitedDouble a, UnlimitedDouble b) {
        int newDecimal = Math.max(a.rtlDecimal(), b.rtlDecimal());
        int diff = newDecimal - Math.min(a.rtlDecimal(), b.rtlDecimal());
        if (diff == 0) {
            // no work needed, they are equal
            return;
        }
        char[] chars = (char[]) BetterArrays.createAndFill(char.class, diff,
                '0');
        if (a.rtlDecimal() < newDecimal) {
            a.digits.addAll(chars);
        } else if (b.rtlDecimal() < newDecimal) {
            b.digits.addAll(chars);
        }
        if (a.rtlDecimal() != b.rtlDecimal()) {
            throw new IllegalStateException("Couldn't pad " + a + " to match "
                    + b);
        }
    }

    /* Public methods */

    public UnlimitedDouble add(UnlimitedDouble b) {
        if (b.equals(EMPTY)) {
            return this;
        }
        if (equals(EMPTY)) {
            return b;
        }
        UnlimitedDouble a = this.clone();
        b = b.clone();
        // don't use originals, we align the char arrays ourselves in pad()
        pad(a, b);
        UnlimitedDouble result = empty();
        UnlimitedDouble abs_a = a.abs(), abs_b = b.abs();
        return result;
    }

    public UnlimitedDouble subtract(UnlimitedDouble b) {
        if (b.equals(EMPTY)) {
            return this;
        }
        if (equals(EMPTY)) {
            return b;
        }
        // flip the negative value on b and add
        b = b.clone();
        b.negative = !b.negative;
        return add(b);
    }

    public UnlimitedDouble multiply(UnlimitedDouble b) {
        if (b.equals(EMPTY)) {
            return this;
        }
        if (equals(EMPTY)) {
            return b;
        }
        UnlimitedDouble a = this;
        UnlimitedDouble result = empty();
        return result;
    }

    public UnlimitedDouble abs() {
        UnlimitedDouble clone = clone();
        clone.negative = false;
        return clone;
    }

    /**
     * Returns <tt>this > b</tt>
     * 
     * @param b
     *            - the other value to compare against
     * @return <tt>this > b</tt>
     */
    public boolean greaterThan(UnlimitedDouble b) {
        // greater than
        return compareTo(b) == 1;
    }

    /**
     * Returns <tt>this >= b</tt>
     * 
     * @param b
     *            - the other value to compare against
     * @return <tt>this >= b</tt>
     */
    public boolean greaterThanOrEqual(UnlimitedDouble b) {
        // not less than
        return compareTo(b) != -1;
    }

    /**
     * Returns <tt>this < b</tt>
     * 
     * @param b
     *            - the other value to compare against
     * @return <tt>this < b</tt>
     */
    public boolean lessThan(UnlimitedDouble b) {
        // less than
        return compareTo(b) == -1;
    }

    /**
     * Returns <tt>this <= b</tt>
     * 
     * @param b
     *            - the other value to compare against
     * @return <tt>this <= b</tt>
     */
    public boolean lessThanOrEqual(UnlimitedDouble b) {
        // not greater than
        return compareTo(b) != 1;
    }

    /**
     * Returns when this number is a float
     * 
     * @return if this number has a decimal point
     */
    public boolean hasDecimal() {
        return decimal != digits.size();
    }

    /**
     * The number of digits
     * 
     * @return the amount of digits in this number
     */
    public int length() {
        return digits.size();
    }

    /* Overridden defaults */

    @Override
    public UnlimitedDouble clone() {
        try {
            UnlimitedDouble shallow = (UnlimitedDouble) super.clone();
            shallow.digits = digits.clone();
            shallow.decimal = decimal;
            shallow.negative = negative;
            return shallow;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * This is used by greaterThan and lessThan because it returns 1 for
     * <tt>y</tt> > <tt>this</tt>, 0 for equal, and -1 for <tt>y</tt> <
     * <tt>this</tt>
     */
    @Override
    public int compareTo(UnlimitedDouble y) {
        UnlimitedDouble x = this;
        char[] bdx = new char[x.length()], bdy = new char[y.length()], adx = new char[0], ady = new char[0];
        // read decimal
        if (x.hasDecimal()) {
            bdx = new char[x.decimal];
            adx = new char[x.rtlDecimal()];
            for (int i = 0; i < bdx.length; i++) {
                bdx[i] = (Character) x.digits.get(i);
            }
            for (int i = 0; i < adx.length; i++) {
                adx[i] = (Character) x.digits.get(decimal + i);
            }
        }
        if (y.hasDecimal()) {
            bdy = new char[y.decimal];
            ady = new char[y.rtlDecimal()];
            for (int i = 0; i < bdy.length; i++) {
                bdy[i] = (Character) y.digits.get(i);
            }
            for (int i = 0; i < ady.length; i++) {
                ady[i] = (Character) y.digits.get(decimal + i);
            }
        }
        // test non-decimal bits
        if (bdx.length > bdy.length) {
            return 1;
        } else if (bdx.length < bdy.length) {
            return -1;
        }
        int solved = 2; // 2 because 0 means equal
        int index = 0;
        while (solved == 2 && index < bdx.length) {
            if (bdx[index] != bdy[index]) {
                // read the char as a number, byte is the smallest way to do
                // this
                byte bx = Byte.parseByte(String.valueOf(bdx[index])), by = Byte
                        .parseByte(String.valueOf(bdy[index]));
                if (bx > by) {
                    solved = 1;
                } else if (bx == by) {
                    solved = 2;
                } else if (bx < by) {
                    solved = -1;
                }
            } else {
                solved = 2;
            }
        }
        // we just ran out of digits, the numbers are equal integers
        if (solved == 2) {
            // test decimal bits
            if (adx.length > ady.length) {
                return 1;
            } else if (adx.length < ady.length) {
                return -1;
            }
            index = 0;
            while (solved == 2 && index < adx.length) {
                if (adx[index] != ady[index]) {
                    // read the char as a number, byte is the smallest way to do
                    // this
                    byte bx = Byte.parseByte(String.valueOf(adx[index])), by = Byte
                            .parseByte(String.valueOf(ady[index]));
                    if (bx > by) {
                        solved = 1;
                    } else if (bx == by) {
                        solved = 2;
                    } else if (bx < by) {
                        solved = -1;
                    }
                } else {
                    solved = 2;
                }
            }
        }
        // equal!!!
        if (solved == 2) {
            solved = 0;
        }
        return solved;
    }

    /**
     * The compare speed for this method depends on the state of the objects:<br>
     * If <tt>this.negative != obj.negative</tt>, then it is a quick boolean
     * compare.<br>
     * Otherwise, if <tt>this.decimal != obj.decimal</tt>, then it is a quick
     * boolean and int compare.<br>
     * Otherwise, if <tt>this.digits.size() != obj.digits.size()</tt>, then it
     * is a bool-int-int compare.<br>
     * Otherwise, it takes more time depending on the size of <tt>digits</tt>
     * due to the nature of {@link ResizableArray#equals(Object)}<br>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnlimitedDouble) {
            UnlimitedDouble ud = (UnlimitedDouble) obj;
            // order matters here! booleans compare faster than ints which
            // compare faster than arrays, so fall-through ensures that it is
            // efficient
            return ud.negative == negative && ud.decimal == ud.decimal
                    && ud.digits.size() == digits.size()
                    && ud.digits.equals(digits);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // use toString() because strings follow the proper contract for our
        // equals method
        return toString().hashCode();
    }

    @Override
    public String toString() {
        if (equals(EMPTY)) {
            return "undefined";
        }
        ResizableArray copy = digits.clone();
        if (digits.size() > decimal) {
            copy.add(decimal - (negative ? 1 : 0), '.');
        }
        if (negative) {
            copy.add(0, '-');
        }
        return new String((char[]) copy.getArray());
    }

    /* (public) Static methods */

    public static UnlimitedDouble parseUD(String s) {
        return new UnlimitedDouble(s);
    }

    /**
     * Attempts to parse the given value. Tries the {@link Object#toString()}
     * method to get a valid string, and if it can be parsed it will be used.
     * 
     * @param value
     *            - the object to parse
     * @return an {@link UnlimitedDouble} containing the value
     * @throws NumberFormatException
     *             if the toString() method does not return a valid string.
     */
    public static UnlimitedDouble valueOf(Object value) {
        if (value instanceof UnlimitedDouble) {
            return new UnlimitedDouble((UnlimitedDouble) value);
        }
        return new UnlimitedDouble(value.toString());
    }

    /**
     * Gives out a new empty value for usage via {@link UnlimitedDouble#clone()
     * clone()}
     * 
     * @return {@link UnlimitedDouble#EMPTY EMPTY}
     *         {@link UnlimitedDouble#clone() .clone()}
     */
    public static UnlimitedDouble empty() {
        return EMPTY.clone();
    }

    /**
     * See {@link Math#max(int, int)}
     * 
     * @see Math#max(int, int)
     */
    public static UnlimitedDouble max(UnlimitedDouble a, UnlimitedDouble b) {
        return (a.greaterThanOrEqual(b)) ? a : b;
    }

    /**
     * See {@link Math#min(int, int)}
     * 
     * @see Math#min(int, int)
     */
    public static UnlimitedDouble min(UnlimitedDouble a, UnlimitedDouble b) {
        return (a.lessThanOrEqual(b)) ? a : b;
    }
}
