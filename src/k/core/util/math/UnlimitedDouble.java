package k.core.util.math;

import k.core.util.arrays.ResizableArray;

public class UnlimitedDouble {
    /**
     * The digits, not including the decimal.
     */
    private ResizableArray digits = new ResizableArray(char.class, 0);
    /**
     * The decimal place, inserted between <tt>decimal</tt> and
     * <tt>decimal+1</tt>
     */
    private int decimal = digits.size();

    public UnlimitedDouble(String value) {
        parse0(value);
    }

    public UnlimitedDouble(UnlimitedDouble value) {
        this(value.toString());
    }

    /* Private methods */

    private void parse0(String value) {
        String withoutDec = value.replace(".", "");
    }

    /* Public methods */

    @Override
    public String toString() {
        return "";
    }

    public UnlimitedDouble add(UnlimitedDouble b) {
        UnlimitedDouble a = this;
        return new UnlimitedDouble("0");
    }

    public UnlimitedDouble multiply(UnlimitedDouble b) {
        UnlimitedDouble a = this;
        return new UnlimitedDouble("0");
    }

    /* Static methods */

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
}
