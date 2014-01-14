package k.core.util.math;

import k.core.util.Helper.BetterArrays;
import k.core.util.arrays.ResizableArray;
import k.core.util.strings.Strings;

/**
 * A class that provides a virtually unlimited double, with the limitation being
 * the value of {@link ResizableArray#MAX_ARRAY_SIZE MAX_ARRAY_SIZE}, due to
 * array size limits.
 * 
 * (Note that this is a digit count limit, you can have up to that many
 * <b>digits</b>). This class is defined as immutable.<br>
 * <br>
 * Please note that add() does not support negatives, and therefore subtract
 * only supports negative values for b. Multiply supports only positive values
 * for a and only positive integers for b. <br>
 * <br>
 * 
 * TODO: <br>
 * Add divide(), optimize so that we can do operations quickly. Current
 * optimization ~80%. Add negative support and implement full multiplication.
 * 
 * @author Kenzie Togami
 * 
 */
public class UnlimitedDouble implements Cloneable, Comparable<UnlimitedDouble> {
    private static final UnlimitedDouble ONE = new UnlimitedDouble("1"),
            ZERO = new UnlimitedDouble("0");
    private static final boolean debugmode = Boolean.parseBoolean(System
            .getProperty("ud.debug")), negzeros = System.getProperty(
            "ud.negzero", null) != null;
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
    private static final UnlimitedDouble EMPTY = ZERO.clone();
    static {
        EMPTY.digits = new ResizableArray<char[]>(new char[0]);
        EMPTY.decimal = EMPTY.digits.size();
        EMPTY.negative = false;
    }

    /**
     * The digits, not including the decimal or negative.
     */
    private ResizableArray<char[]> digits = null;
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
        value.digits.permitUndefined(false);
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
        // remove the invalids
        value = value.replace(new String(new char[] { 0 }), "");
        // do this BEFORE decimal place check, otherwise it screws up.
        value = trimZeros(value);
        value = value.substring(((negative = value.charAt(0) == '-') ? 1 : 0));
        // check for negatives, but do not include them
        String withoutDec = value.replace(".", "");
        // only digits
        if (!withoutDec.matches("^\\d+$")) {
            throw new NumberFormatException(value);
        }
        decimal = value.indexOf('.');
        // convert string
        digits = new ResizableArray<char[]>(withoutDec.toCharArray());
        digits.permitUndefined(false);
        // decimal place = length of digits when there is none
        if (decimal < 0) {
            decimal = digits.size();
        }
        // if this == zero, then remove the negative (set property 'ud.negzero'
        // for negative zeros :3)
        if (!negzeros && negative && negate().equals(ZERO)) {
            negative = false;
        }
    }

    private int rtlDecimal() {
        return digits.size() - decimal;
    }

    private String trimZeros(String value) {
        if (value.matches(".+?\\.0+$")) {
            value = value.replaceFirst("\\.0+$", "");
        } else if (value.matches(".+?\\.\\d+0+$")) {
            value = value.replaceFirst("(\\.\\d+)0+$", "$1");
        }
        return value.replaceFirst("^(\\-)?0+(?!0)(\\d+(\\.\\d+)?)$", "$1$2");
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
        // don't use originals, we align the char arrays ourselves in pad()
        UnlimitedDouble a = this.clone();
        b = b.clone();
        pad(a, b);
        UnlimitedDouble result = empty(), larger = max(a, b);
        // get the matching array for the numbers. uses getUnderlying due to
        // increased speed. We don't mod the original number arrays, they are
        // reversed into a new array. trimToSize is required here.
        a.digits.trimToSize();
        b.digits.trimToSize();
        char[] caa = (char[]) a.digits.getUnderlyingArray(), cab = (char[]) b.digits
                .getUnderlyingArray();
        // create the array used to carry numbers
        byte[] carry = new byte[larger.length() + 1];
        ResizableArray<char[]> res = result.digits;
        caa = BetterArrays.reverseNonGeneric(caa);
        cab = BetterArrays.reverseNonGeneric(cab);
        int length = larger.length();
        for (int i = 0; i < length; i++) {
            byte ai = 0, bi = 0, over = carry[i];
            if (i < caa.length) {
                ai = Strings.getNumForChar(caa[i]);
            }
            if (i < cab.length) {
                bi = Strings.getNumForChar(cab[i]);
            }
            int ires = ai + bi + over;
            char[] cres = String.valueOf(ires).toCharArray();
            if (cres.length > 1) {
                carry[i + 1] = Strings.getNumForChar(cres[0]);
                res.add(cres[1]);
            } else {
                res.add(cres[0]);
            }
        }
        if (carry[carry.length - 1] != 0) {
            res.add(Strings.getCharForNum(carry[carry.length - 1]));
        }
        res.reverse();
        result.decimal = result.length() - a.rtlDecimal();
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
        return add(b.negate());
    }

    public UnlimitedDouble multiply(UnlimitedDouble b) {
        if (b.equals(EMPTY)) {
            return this;
        }
        if (equals(EMPTY)) {
            return b;
        }
        UnlimitedDouble a = this;
        UnlimitedDouble result = zero();
        boolean sign = a.negative == b.negative; // true = (+); false = (-)
        a = a.abs();
        b = b.abs();
        // extremely inefficient way to multiply...multiplying is repeated
        // addition, right?
        UnlimitedDouble counter = zero();
        while (counter.lessThan(b)) {
            result = result.add(a);
            counter = counter.add(one());
        }
        return new UnlimitedDouble(!sign ? "-" : "" + result);
    }

    public UnlimitedDouble pow(UnlimitedDouble b) {
        if (this.equals(ONE) || b.equals(ZERO)) {
            return one();
        }
        if (this.equals(ZERO)) {
            return zero();
        }
        if (b.equals(ONE)) {
            return clone();
        }
        return UnlimitedDouble.parseUD(Double.toString(Math.pow(
                Double.parseDouble(this.toString()),
                Double.parseDouble(b.toString()))));
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

    /**
     * Negates this UD (<tt>this</tt> * -1)
     * 
     * @return <tt>this</tt> * -1
     */
    public UnlimitedDouble negate() {
        UnlimitedDouble copy = clone();
        copy.negative = !negative;
        return copy;
    }

    /* Overridden defaults */

    @Override
    public UnlimitedDouble clone() {
        try {
            UnlimitedDouble shallow = (UnlimitedDouble) super.clone();
            digits.trimToSize();
            digits.permitUndefined(false);
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
     * 
     * This is used by greaterThan and lessThan because it returns 1 for
     * <tt>y</tt> > <tt>this</tt>, 0 for equal, and -1 for <tt>y</tt> <
     * <tt>this</tt> If you have a error regarding the function, please set the
     * system property 'ud.debug' to <tt>true</tt>. <br>
     * <br>
     * From {@link Comparable#compareTo(Object)}: <br>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(UnlimitedDouble y) {
        UnlimitedDouble x = this.clone();
        y = y.clone();
        // must trim zeros before compare, or it breaks SEVERLEY
        // but don't mod the originals, that can cause repercussions
        String before = x.toString();
        x.parse0(before);
        if (!before.equals(x.toString()) && debugmode) {
            System.err
                    .println("If you are getting bad results, compareTo is broken here. DEBUG: "
                            + before + " changed to " + x);
            Thread.dumpStack();
        }
        before = y.toString();
        y.parse0(before);
        if (!before.equals(y.toString()) && debugmode) {
            System.err
                    .println("If you are getting bad results, compareTo is broken here. DEBUG: "
                            + before + " changed to " + y);
            Thread.dumpStack();
        }
        char[] bdx = new char[x.length()], bdy = new char[y.length()], adx = new char[0], ady = new char[0];
        // read decimal
        if (x.hasDecimal()) {
            bdx = new char[x.decimal];
            adx = new char[x.rtlDecimal()];
            for (int i = 0; i < bdx.length; i++) {
                bdx[i] = (Character) x.digits.get(i);
            }
            for (int i = 0; i < adx.length; i++) {
                adx[i] = (Character) x.digits.get(x.decimal - 1 + i);
            }
        } else {
            for (int i = 0; i < bdx.length; i++) {
                bdx[i] = (Character) x.digits.get(i);
            }
        }
        if (y.hasDecimal()) {
            bdy = new char[y.decimal];
            ady = new char[y.rtlDecimal()];
            for (int i = 0; i < bdy.length; i++) {
                bdy[i] = (Character) y.digits.get(i);
            }
            for (int i = 0; i < ady.length; i++) {
                ady[i] = (Character) y.digits.get(y.decimal - 1 + i);
            }
        } else {
            for (int i = 0; i < bdy.length; i++) {
                bdy[i] = (Character) y.digits.get(i);
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
                try {
                    byte bx = Byte.parseByte(String.valueOf(bdx[index])), by = Byte
                            .parseByte(String.valueOf(bdy[index]));

                    if (bx > by) {
                        solved = 1;
                    } else if (bx == by) {
                        solved = 2;
                    } else if (bx < by) {
                        solved = -1;
                    }
                } catch (NumberFormatException nfe) {
                    return nfe.hashCode();
                }
            } else {
                solved = 2;
            }
            index++;
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
                    try {
                        byte bx = Byte.parseByte(String.valueOf(adx[index])), by = Byte
                                .parseByte(String.valueOf(ady[index]));
                        if (bx > by) {
                            solved = 1;
                        } else if (bx == by) {
                            solved = 2;
                        } else if (bx < by) {
                            solved = -1;
                        }
                    } catch (NumberFormatException nfe) {
                        return nfe.hashCode();
                    }
                } else {
                    solved = 2;
                }
                index++;
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
        ResizableArray<char[]> copy = digits.clone();
        if (digits.size() > decimal) {
            copy.add(decimal, '.');
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
     * @see UnlimitedDouble#EMPTY
     */
    public static UnlimitedDouble empty() {
        return EMPTY.clone();
    }

    public static UnlimitedDouble one() {
        return ONE.clone();
    }

    public static UnlimitedDouble zero() {
        return ZERO.clone();
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
