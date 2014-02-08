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
 * only supports negative values for b. Multiply supports only positive integers
 * for b. <br>
 * <br>
 * 
 * TODO: <br>
 * Add divide(), optimize so that we can do operations quickly. Current
 * optimization ~80%. Add negative support and implement full multiplication.
 * 
 * @author Kenzie Togami
 * 
 */
public final class UnlimitedDouble extends Number implements
        Comparable<UnlimitedDouble> {
    /**
     * Serial version
     */
    private static final long serialVersionUID = -3795087690346750137L;
    public static final UnlimitedDouble ONE = newInstance("1"),
            ZERO = newInstance("0");
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
    public static final UnlimitedDouble EMPTY = new UnlimitedDouble(
            new ResizableArray<char[]>(char[].class, 0), 0, false);

    /**
     * The digits, not including the decimal or negative.
     */
    private final ResizableArray<char[]> digits;
    /**
     * The decimal place, inserted between <tt>decimal</tt> and
     * <tt>decimal+1</tt>
     */
    private final int decimal;

    /**
     * If this UD is negative or not
     */
    private final boolean negative;

    public static UnlimitedDouble newInstance(String value) {
        return parse0(value);
    }

    private UnlimitedDouble(UnlimitedDouble value) {
        // clone digits so that we don't overwrite
        value.digits.permitUndefined(false);
        value.digits.trimToSize();
        digits = value.digits.clone();
        decimal = value.decimal;
        negative = value.negative;
    }

    private UnlimitedDouble(ResizableArray<char[]> dig, int dec, boolean neg) {
        digits = dig;
        decimal = dec;
        negative = neg;
    }

    /* Private methods */

    private int rtlDecimal() {
        return digits.size() - decimal;
    }

    /* (private) Static methods */

    private static String trimZeros(String value) {
        if (value.matches(".+?\\.0+$")) {
            value = value.replaceFirst("\\.0+$", "");
        } else if (value.matches(".+?\\.\\d+0+$")) {
            value = value.replaceFirst("(\\.\\d+)0+$", "$1");
        }
        return value.replaceFirst("^(\\-)?0+(?!0)(\\d+(\\.\\d+)?)$", "$1$2");
    }

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

    /**
     * Handles parsing for String constructor
     * 
     * @param value
     *            - the original string
     */
    private static UnlimitedDouble parse0(String value) {
        if (Strings.count(value, '.') > 1) {
            throw new NumberFormatException(value);
        }
        // value length is about the real size!
        ResizableArray<char[]> dig = new ResizableArray<char[]>(char[].class,
                value.length());
        int dec = 0;
        boolean neg = false;
        if ("undefined".equals(value)) {
            dec = EMPTY.decimal;
            dig = EMPTY.digits;
            neg = EMPTY.negative;
        } else {
            // remove the invalids
            value = value.replace(new String(new char[] { 0 }), "");
            // do this BEFORE decimal place check, otherwise it screws up.
            value = trimZeros(value);
            value = value.substring(((neg = value.charAt(0) == '-') ? 1 : 0));
            // check for negatives, but do not include them
            String withoutDec = value.replace(".", "");
            // only digits
            if (!withoutDec.matches("^\\d+$")) {
                throw new NumberFormatException(value);
            }
            dec = value.indexOf('.');
            // convert string
            dig = new ResizableArray<char[]>(withoutDec.toCharArray());
            dig.permitUndefined(false);
            // decimal place = length of digits when there is none
            if (dec <= 0) {
                dec = dig.size();
            }
            // if this == zero, then remove the negative (set property
            // 'ud.negzero'
            // for negative zeros :3)
            if (!negzeros && neg && dig.get(0).equals('0')) {
                neg = false;
            }
        }
        return new UnlimitedDouble(dig, dec, neg);
    }

    /**
     * Used as a protected clone method, so that we can clone in our operations.
     * 
     * @param clone
     *            - the original
     * @return a new clone
     */
    static UnlimitedDouble privDup(UnlimitedDouble clone) {
        return new UnlimitedDouble(clone);
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
        UnlimitedDouble a = privDup(this);
        b = privDup(b);
        pad(a, b);
        UnlimitedDouble result = privDup(EMPTY), larger = max(a, b);
        // get the matching array for the numbers. uses getUnderlying due to
        // increased speed. We don't mod the original number arrays, they are
        // reversed into a new array. trimToSize is required here.
        a.digits.trimToSize();
        b.digits.trimToSize();
        char[] caa = a.digits.getUnderlyingArray(), cab = b.digits
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
        result = new UnlimitedDouble(res, res.size() - a.rtlDecimal(),
                result.negative);
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
        // unsupported as of now
        if (b.hasDecimal()) {
            return UnlimitedDouble.parseUD(Double.toString(Double.parseDouble(a
                    .toString()) * Double.parseDouble(b.toString())));
        }
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
        return new UnlimitedDouble(result.digits, result.decimal, !sign);
    }

    public UnlimitedDouble pow(UnlimitedDouble b) {
        if (this.equals(ONE) || b.equals(ZERO)) {
            return one();
        }
        if (this.equals(ZERO)) {
            return zero();
        }
        if (b.equals(ONE)) {
            return privDup(this);
        }
        return UnlimitedDouble.parseUD(Double.toString(Math.pow(
                Double.parseDouble(this.toString()),
                Double.parseDouble(b.toString()))));
    }

    public UnlimitedDouble abs() {
        return new UnlimitedDouble(digits, decimal, false);
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
        return decimal != digits.size() || decimal != 0;
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
        return new UnlimitedDouble(digits, decimal, !negative);
    }

    /**
     * Returns the integer part of this UD.
     * 
     * @return the integer part of this UD, or 'undefined' if this is undefined.
     */
    public String intPart() {
        String toStr = toString();
        if (toStr.equals("undefined") || decimal >= digits.size()) {
            return toStr;
        }
        return toStr.substring(0, toStr.indexOf('.'));
    }

    /* Overridden defaults */

    /**
     * 
     * This is used by greaterThan and lessThan because it returns 1 for
     * <tt>y</tt> > <tt>this</tt>, 0 for equal, and -1 for <tt>y</tt> <
     * <tt>this</tt> If you have a error regarding the function, please set the
     * system property 'ud.debug' to <tt>true</tt>. <br>
     * <br>
     * This should be used instead of equals because numbers with 0's in their
     * decimal places do not equal integers via {@link #equals(Object)}, but do
     * via <tt>compareTo</tt>.<br>
     * <br>
     * From {@link Comparable#compareTo(Object)}: <br>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(UnlimitedDouble y) {
        UnlimitedDouble x = this;
        // must trim zeros before compare, or it breaks SEVERLEY
        // but don't mod the originals, that can cause repercussions
        String before = x.toString();
        x = parse0(before);
        if (!before.equals(x.toString()) && debugmode) {
            System.err
                    .println("If you are getting bad results, compareTo is broken here. DEBUG: "
                            + before + " changed to " + x);
            Thread.dumpStack();
        }
        before = y.toString();
        y = parse0(before);
        if (!before.equals(y.toString()) && debugmode) {
            System.err
                    .println("If you are getting bad results, compareTo is broken here. DEBUG: "
                            + before + " changed to " + y);
            Thread.dumpStack();
        }
        // bdx = before x's decimal
        // bdy before y's decimal
        // adx = after x's decimal
        // ady = after y's decimal
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
        return newInstance(s);
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
        return newInstance(String.valueOf(value));
    }

    /**
     * Returns an empty value.
     * 
     * @return {@link #EMPTY}
     */
    public static UnlimitedDouble empty() {
        return EMPTY;
    }

    public static UnlimitedDouble one() {
        return ONE;
    }

    public static UnlimitedDouble zero() {
        return ZERO;
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

    @Override
    public int intValue() {
        try {
            return Integer.parseInt(intPart());
        } catch (NumberFormatException nfe) {
            return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    @Override
    public long longValue() {
        try {
            return Long.parseLong(intPart());
        } catch (NumberFormatException nfe) {
            return negative ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }

    @Override
    public float floatValue() {
        try {
            return Float.parseFloat(toString());
        } catch (NumberFormatException nfe) {
            return negative ? Float.MIN_VALUE : Float.MAX_VALUE;
        }
    }

    @Override
    public double doubleValue() {
        try {
            return Double.parseDouble(toString());
        } catch (NumberFormatException nfe) {
            return negative ? Double.MIN_VALUE : Double.MAX_VALUE;
        }
    }
}
