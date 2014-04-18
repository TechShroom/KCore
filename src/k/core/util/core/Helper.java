package k.core.util.core;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class Helper {
    public static class Base64 {
        public static String toB64(String data) {
            return DatatypeConverter.printBase64Binary(data.getBytes());
        }

        public static String fromB64(String b64data) {
            return new String(DatatypeConverter.parseBase64Binary(b64data));
        }
    }

    public static class Files {
        public static String topLevel;
        static {
            // uses the pwd as the top level
            topLevel = new File("").getAbsolutePath().replace(
                    File.separatorChar, '/');
        }

        public static File getFileRelativeToTopLevel(String file) {
            return new File(topLevel, file);
        }
    }

    public static class BetterArrays {
        public static void print(Object[] array) {
            print(array, System.err);
        }

        public static void print(Object[] array, PrintStream o) {
            if (array == null) {
                o.println("Array is null.");
            }
            o.println(array.getClass().getComponentType().getName()
                    + "[] (length: " + array.length + ") contents:");
            int index = 0;
            for (Object object : array) {
                String out = "";
                String indexs = index + ": ";
                String clazs = "";
                if (object == null) {
                    out = "<null object>";
                    clazs = array.getClass().getComponentType().getName();
                } else if (object instanceof String
                        && ((String) object).equals("")) {
                    out = "<empty string>";
                    clazs = object.getClass().getName();
                } else {
                    out = object.toString();
                    clazs = object.getClass().getName();
                }
                o.println(indexs + "(" + clazs + ") " + out);
                index++;
            }
        }

        public static <T, D> T[] arrayTranslate(Class<T> generic, D[] src) {
            ArrayList<T> temp = new ArrayList<T>(src.length);
            for (D o : src) {
                if (o != null && generic.isAssignableFrom(o.getClass())) {
                    temp.add(generic.cast(o));
                } else if (o == null) {
                    temp.add((T) null);
                } else {
                    System.err.println("Lost " + o
                            + " because it was not of type "
                            + generic.getName());
                }
            }
            // Array is not generic
            @SuppressWarnings("unchecked")
            T[] arrType = (T[]) Array.newInstance(generic, src.length);
            return temp.toArray(arrType);
        }

        public static <T> T[] randomArray(T[] in) {
            if (in.length == 0 || in.length == 1) {
                return in;
            }
            // Array is not generic
            @SuppressWarnings("unchecked")
            T[] test = (T[]) Array.newInstance(
                    in.getClass().getComponentType(), in.length);
            boolean solved = false;
            boolean[] taken = new boolean[test.length];
            int total = test.length;
            Random r = new Random(new Random().nextInt());
            int index = 0;
            while (!solved) {
                int ra = r.nextInt(test.length);
                if (!taken[ra]) {
                    test[ra] = in[index];
                    taken[ra] = true;
                    index++;
                    total--;
                }
                if (total == 0) {
                    solved = true;
                }
            }
            return test;
        }

        public static void dump(Object[] array) {
            for (Object t : array) {
                System.out.println(t);
            }
        }

        public static <T> T[] repeatRandomArray(T[] in, int count) {
            // Array is not generic
            @SuppressWarnings("unchecked")
            T[] array = (T[]) Array.newInstance(in.getClass()
                    .getComponentType(), in.length);
            System.arraycopy(in, 0, array, 0, in.length);
            while (count > -1) {
                array = BetterArrays.randomArray(array);
                count--;
            }
            return array;
        }

        public static String dump0(Object[] array) {
            if (array == null) {
                return "<null array>";
            }
            if (array.length == 0) {
                return "[]";
            }
            String ret = "[";
            for (Object o : array) {
                if (o instanceof String) {
                    o = "'" + o + "'";
                }
                ret += o + ", ";
            }
            ret = ret.substring(0, ret.length() - 2) + "]";
            return ret;
        }

        /**
         * I don't know what this does
         * 
         * @param in
         *            - i have no idea
         * @param outtype
         *            - i still have no idea
         * @return - an int array
         */
        public static int[] specificTraslate(byte[] in, int[] outtype) {
            if (in != null && in.length > 0) {
                int[] out = outtype.length >= in.length ? outtype
                        : new int[in.length];
                int index = 0;
                for (byte b : in) {
                    out[index] = b;
                    index++;
                }
                return out;
            }
            return (int[]) Array.newInstance(outtype.getClass()
                    .getComponentType(), 0);
        }

        public static byte[] intToByteArray(int value) {
            return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
                    (byte) (value >>> 8), (byte) value };
        }

        public static <T> T[] reverse(T[] stuff) {
            // Array is not generic
            @SuppressWarnings("unchecked")
            T[] out = (T[]) Array.newInstance(stuff.getClass()
                    .getComponentType(), stuff.length);
            for (int i = 0; i < stuff.length; i++) {
                out[stuff.length - i - 1] = stuff[i];
            }
            return out;
        }

        /*
         * This is a cool trick which allows us to return the requested array
         * even with primitives as the component type.
         */
        public static <T> T reverseNonGeneric(T t) {
            int tlen = Array.getLength(t);
            // Array is not generic
            @SuppressWarnings("unchecked")
            T out = (T) Array
                    .newInstance(t.getClass().getComponentType(), tlen);
            for (int i = 0; i < tlen; i++) {
                Array.set(out, tlen - i - 1, Array.get(t, i));
            }
            return out;
        }

        public static void fillArray(Object array, Object value) {
            for (int i = 0; i < Array.getLength(array); i++) {
                Array.set(array, i, value);
            }
        }

        public static Object createAndFill(Class<?> type, int size, Object value) {
            Object array = Array.newInstance(type, size);
            fillArray(array, value);
            return array;
        }

        public static <T> T splice(T array, int start, int end, int step) {
            if (start < 0) {
                throw new IndexOutOfBoundsException("start < 0");
            }
            if (end > Array.getLength(array)) {
                throw new IndexOutOfBoundsException("end > length");
            }
            if (end < start) {
                throw new IllegalArgumentException("start < end");
            }
            if (step == 0) {
                throw new IllegalArgumentException("step == 0");
            }
            int len = end - start; // get length when step == 1
            if (step > 1) {
                int mod = len % step; // modulo to get leftovers
                len -= mod; // remove them to floor the result
                len /= step; // divide by step
            }
            // Array is not generic
            @SuppressWarnings("unchecked")
            T out = (T) Array.newInstance(array.getClass().getComponentType(),
                    len);
            if (step < 0) {
                step = -step;
                for (int i = end - 1, index = 0; i >= start; i -= step, index++) {
                    Array.set(out, index, Array.get(array, i));
                }
            } else {
                for (int i = start, index = 0; i < end; i += step, index++) {
                    Array.set(out, index, Array.get(array, i));
                }
            }
            return out;
        }
    }

    public static class ProgramProps {

        public static String[] normalizeCommandArgs(String[] args) {
            ArrayList<String> joined = new ArrayList<String>();
            String key = "";
            String value = "";
            boolean incomplete = false;
            for (int i = 0; i < args.length; i++) {
                // get the current argument
                String curr = args[i];
                if (curr.startsWith("-") && !incomplete) {
                    // it is a key, but we don't already have a key.
                    // set the "looking for value" mode, sets the key as the
                    // current value, and removes whatever old value was there.
                    incomplete = true;
                    key = curr;
                    value = "";
                } else if (curr.startsWith("-")) {
                    // it is a key, and we have a key already.
                    // sets the current key + value (value will be "") and the
                    // next key to this one.
                    joined.add(key);
                    joined.add(value);
                    key = curr;
                    value = "";
                } else {
                    // this is a value, handle concatenation.
                    if (incomplete) {
                        // there is a key waiting for us
                        if (i + 1 < args.length && args[i + 1].startsWith("-")) {
                            // Last part of this value, add the key+value pair
                            // and unset the incomplete flag
                            incomplete = false;
                            if (value.equals("")) {
                                value = curr;
                            } else {
                                value += " " + curr;
                            }
                            joined.add(key);
                            joined.add(value);
                            key = "";
                            value = "";
                        } else if (i + 1 >= args.length) {
                            // Also last part, but last in array, break out of
                            // the loop.
                            incomplete = false;
                            if (value.equals("")) {
                                value = curr;
                            } else {
                                value += " " + curr;
                            }
                            joined.add(key);
                            joined.add(value);
                            key = "";
                            value = "";
                            break;
                        } else {
                            // Continue making value additions, this is the
                            // multi-space handler that is built-in.
                            incomplete = true;
                            if (value.equals("")) {
                                value = curr;
                            } else {
                                value += " " + curr;
                            }
                        }
                    } else {
                        // there is no key for our value, so we throw an error
                        // because this will only happen when a user inputs
                        // something like "value"
                        throw new IllegalArgumentException("Value missing key!");
                    }
                }
            }
            if (incomplete) {
                // cleanup
                joined.add(key);
                joined.add(value);
            }
            // convert to array
            return joined.toArray(new String[joined.size()]);
        }

        private static Properties clprops = new Properties();

        public static void acceptPair(String key, String val) {
            if (val == null) {
                throw new NullPointerException("value");
            }
            if (key == null) {
                throw new NullPointerException("key");
            }
            key = key.replace("-", "");
            clprops.put(key, val);
            System.err.println("Added " + key + ":" + val);
        }

        public static void acceptAll(String[] args) {
            for (int i = 0; i < args.length; i += 2) {
                acceptPair(args[i], args[i + 1]);
            }
        }

        public static String getProperty(String key) {
            return getProperty(key, "");
        }

        public static String getProperty(String key, String def) {
            return clprops.getProperty(key, def);
        }

        public static boolean getPropertyB(String key) {
            return getPropertyB(key, false);
        }

        public static boolean getPropertyB(String key, boolean def) {
            return Boolean.valueOf(clprops.getProperty(key,
                    Boolean.toString(def)));
        }

        public static int getPropertyI(String key) {
            return getPropertyI(key, 0);
        }

        public static int getPropertyI(String key, int def) {
            return Integer.valueOf(clprops.getProperty(key,
                    Integer.toString(def)));
        }

        public static float getPropertyF(String key) {
            return getPropertyF(key, 0f);
        }

        public static float getPropertyF(String key, float def) {
            return Float.valueOf(clprops.getProperty(key, Float.toString(def)));
        }

        public static double getPropertyD(String key) {
            return getPropertyD(key, 0d);
        }

        public static double getPropertyD(String key, double def) {
            return Double
                    .valueOf(clprops.getProperty(key, Double.toString(def)));
        }

        public static boolean hasKey(String key) {
            return clprops.containsKey(key);
        }
    }
}
