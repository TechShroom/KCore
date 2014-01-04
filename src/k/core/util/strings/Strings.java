package k.core.util.strings;

public class Strings {
    /**
     * Returns the amount of <tt>c</tt>'s in <tt>s</tt>
     * 
     * @param s
     *            - the string to check
     * @param c
     *            - the char to count
     * @return the number of <tt>c</tt>'s in <tt>s</tt>
     */
    public static int count(String s, char c) {
        // regex with negated char = removal of all but the char
        return s.replaceAll("[^\\Q" + c + "\\E]", "").length();
    }
}
