package k.core.util.netty;

import k.core.util.core.Helper;

public class DataStructTest {
    String aVal = "'look, it worked, alright?' -" + getClass().getSimpleName();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Object[] all = new Object[] { "string", 1, 1.1d, 1.1f, 1l, (byte) 1,
                '|', true, new DataStructTest() };
        Helper.BetterArrays.print(all);
        // A data struct with all types in one.
        DataStruct dec = new DataStruct(all);
        DataStruct enc = new DataStruct(dec.toString());
        Helper.BetterArrays.print(enc.getAll());
    }

    @Override
    public String toString() {
        return aVal;
    }

}
