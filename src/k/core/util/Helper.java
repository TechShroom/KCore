package k.core.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;

public class Helper {
    public static class Files {
        public static File topLevel;
        static {
            // uses the pwd as the top level
            topLevel = new File("").getAbsoluteFile();
        }

        public static File getFileRelativeToTopLevel(String file) {
            return new File(topLevel, file);
        }
    }

    public static class Window {

        public static void drop(JFrame frame) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frm = frame.getSize();
            frame.setLocation((screen.width / 2) - frm.width / 2,
                    (screen.height / 2) - frm.height / 2);
        }

        public static void setBackground(Color c, JFrame fr) {
            Container frame = fr.getContentPane();
            frame.setBackground(c);
        }

        public static void kill(JFrame win) {
            if (win == null) {
                return;
            }
            WindowEvent close = new WindowEvent(win, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(close);
        }

    }

    @SuppressWarnings("unchecked")
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
            return temp.toArray((T[]) Array.newInstance(generic, src.length));
        }

        public static <T> T[] randomArray(T[] in) {
            if (in.length == 0 || in.length == 1) {
                return in;
            }
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
            T[] out = (T[]) Array.newInstance(stuff.getClass()
                    .getComponentType(), stuff.length);
            for (int i = 0; i < stuff.length; i++) {
                out[stuff.length - i - 1] = stuff[i];
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
    }

    public static class ProgramProps {

        public static String[] normalizeCommandArgs(String[] args) {
            ArrayList<String> joined = new ArrayList<String>();
            String key = "";
            String value = "";
            boolean incomplete = false;
            for (int i = 0; i < args.length; i++) {
                String curr = args[i];
                if (curr.startsWith("-") && !incomplete) {
                    incomplete = true;
                    key = curr;
                } else if (curr.startsWith("-")) {
                    incomplete = false;
                    joined.add(key);
                    joined.add("");
                    joined.add(curr);
                    joined.add("");
                } else {
                    if (incomplete) {
                        if (i + 1 < args.length && args[i + 1].startsWith("-")) {
                            // Last part
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
                            // Also last part, but last in array
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
                            // Continue making value aditions
                            incomplete = true;
                            if (value.equals("")) {
                                value = curr;
                            } else {
                                value += " " + curr;
                            }
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Value missing key! This shouldn't be happening.");
                    }
                }
            }
            if (incomplete) {
                joined.add(key);
                joined.add(value);
            }
            return joined.toArray(new String[joined.size()]);
        }

        private static Properties clprops = new Properties();

        public static void acceptPair(String key, String val) {
            key = key.replace("-", "");
            clprops.put(key, val);
            System.err.println("Added " + key + ":" + val);
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
