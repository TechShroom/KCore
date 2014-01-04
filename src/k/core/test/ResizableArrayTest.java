package k.core.test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import k.core.util.arrays.ResizableArray;
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
        long end = System.currentTimeMillis() - start;
        System.err.println("Took " + end);
    }

    private static void testRA() {
        long start = System.currentTimeMillis();
        ResizableArray array = new ResizableArray(double.class);
        printLength(array);
        for (int i = 0; i < 10000; i++) {
            array.add(Math.random());
        }
        print(array);
        long end = System.currentTimeMillis() - start;
        System.err.println("Took " + end);
    }

    private static void printLength(Object array) {
        try {
            System.out.println(Reflect.invokeMethod(int.class, "size", array));
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void print(Object array) {
        // System.out.println(array);
    }

}
