package k.core.util.classes;

import java.util.Arrays;

public class PrimitiveTests {

    public static void main(String[] args) {
        String[] values = { "0", "1", "2", "3", "4", "5", "true" };
        Object[] insts = new Object[Primitives.PRIMITIVE_COUNT];
        int tmp = 0;
        for (Class<?> c : Primitives.getWArray()) {
            if (Primitives.isBool(c))
                System.err.println(c);
            if (Primitives.isByte(c))
                System.err.println(c);
            if (Primitives.isChar(c))
                System.err.println(c);
            if (Primitives.isInt(c))
                System.err.println(c);
            if (Primitives.isDouble(c))
                System.err.println(c);
            if (Primitives.isLong(c))
                System.err.println(c);
            if (Primitives.isShort(c))
                System.err.println(c);
            insts[tmp] = Primitives.parse(c, values[tmp]);
            tmp++;
        }
        System.err.println(Arrays.toString(insts));
        tmp = 0;
        for (Class<?> c : Primitives.getPArray()) {
            if (Primitives.isBool(c))
                System.err.println(c);
            if (Primitives.isByte(c))
                System.err.println(c);
            if (Primitives.isChar(c))
                System.err.println(c);
            if (Primitives.isInt(c))
                System.err.println(c);
            if (Primitives.isDouble(c))
                System.err.println(c);
            if (Primitives.isLong(c))
                System.err.println(c);
            if (Primitives.isShort(c))
                System.err.println(c);
            insts[tmp] = Primitives.parse(c, values[tmp]);
            tmp++;
        }
        System.err.println(Arrays.toString(insts));
    }

}
