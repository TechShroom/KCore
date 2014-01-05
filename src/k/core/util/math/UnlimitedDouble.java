package k.core.util.math;

import k.core.util.Helper.Arrays;
import k.core.util.arrays.ResizableArray;
import k.core.util.strings.Strings;

public class UnlimitedDouble implements Cloneable, Comparable<UnlimitedDouble> {
    /**
     * An empty UD for use anywhere you need a pure empty value (think of it as
     * 'null')
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
        char[] chars = (char[]) Arrays.createAndFill(char.class, diff, '0');
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
        UnlimitedDouble a = this.clone();
        b = b.clone();
        // don't use originals, we align the char arrays ourselves in pad()
        pad(a, b);
        UnlimitedDouble result = empty();
        UnlimitedDouble abs_a = a.abs(), abs_b = b.abs();
        return result;
    }

    public UnlimitedDouble subtract(UnlimitedDouble b) {
        // flip the negative value on b and add
        b = b.clone();
        b.negative = !b.negative;
        return add(b);
    }

    public UnlimitedDouble multiply(UnlimitedDouble b) {
        UnlimitedDouble a = this;
        UnlimitedDouble result = empty();
        return result;
    }

    public UnlimitedDouble abs() {
        UnlimitedDouble clone = clone();
        clone.negative = false;
        return clone;
    }

    public boolean greaterThan(UnlimitedDouble b) {
        if (decimal > b.decimal) {
            // b is smaller via decimal, so we MUST be greater
            return true;
        }
        if (!hasDecimal() && !b.hasDecimal()) {
            // no decimals to fiddle with, and we have more digits, we are
            // greater
            return digits.size() > b.digits.size();
        }
        return false;
    }

    public boolean greaterThanOrEqual(UnlimitedDouble b) {
        // assuming equals faster than greaterThan
        return equals(b) || greaterThan(b);
    }

    public boolean lessThan(UnlimitedDouble b) {
        // reuse old method...not most efficient way to do this. Consider
        // rewriting greaterThan()
        return !greaterThanOrEqual(b);
    }

    public boolean lessThanOrEqual(UnlimitedDouble b) {
        // probably more efficient than using equals and lessThan
        return !greaterThan(b);
    }

    public boolean hasDecimal() {
        return decimal != digits.size();
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
    
    @Override
    public int compareTo(UnlimitedDouble ud) {
        return 0;
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
