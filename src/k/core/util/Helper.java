package k.core.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFrame;

public class Helper {
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

	public static class Array {
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
	}

	public static class CommandLine {

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

		public static boolean hasKey(String key) {
			return clprops.containsKey(key);
		}
	}
}
