package k.core.util.arrays;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import k.core.util.reflect.Reflect;

public class ResizableArrayTest {

    public static void main(String[] args) {
        testRA();
        testAL();
    }

    private static void testAL() {
        long start = System.currentTimeMillis();
        ArrayList<Double> array = new ArrayList<Double>();
        printLength(array);
        for (int i = 0; i < 10000; i++) {
            array.add(Math.random());
        }
        print(array);
        double d = array.get(0);
        System.err.println(d);
        long end = System.currentTimeMillis() - start;
        System.err.println("Took " + end);
    }

    private static void testRA() {
        long start = System.currentTimeMillis();
        ResizableArray<double[]> array = new ResizableArray<double[]>(
                double[].class);
        printLength(array);
        for (int i = 0; i < 10000; i++) {
            array.add(Math.random());
        }
        print(array);
        double d = (Double) array.get(0);
        System.err.println(d);
        long end = System.currentTimeMillis() - start;
        System.err.println("Took " + end);
    }

    private static void printLength(Object array) {
        try {
            System.out.println(Reflect.invokeMethod(int.class, "size", array));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void print(Object array) {
        // System.out.println(array);
    }

}
