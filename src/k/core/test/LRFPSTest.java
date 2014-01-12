package k.core.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import k.core.util.timing.LowResFPS;

public class LRFPSTest {
    static JFrame frame = new JFrame("title");

    public static void main(String[] args) throws InvocationTargetException,
	    InterruptedException {
	frame.setSize(200, 100);
	frame.setLocation(400, 600);
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	loop();
    }

    private static void loop() throws InvocationTargetException,
	    InterruptedException {
	final int index = LowResFPS.genIndex();
	LowResFPS.init(index);
	while (true) {
	    int delta = LowResFPS.update(index);
	    System.err.println(delta);
	    do_complicated_logic();
	    SwingUtilities.invokeAndWait(new Runnable() {

		@Override
		public void run() {
		    frame.setTitle("FPS: "
			    + String.valueOf(LowResFPS.getFPS(index)));
		}
	    });
	}
    }

    private static void do_complicated_logic() {
	for (int i = 0; i < 123; i++) {
	    dont_burn_the_processor();
	}
    }

    private static void dont_burn_the_processor() {
	try {
	    int b00m = Integer.parseInt("B00M", Character.MAX_RADIX);
	    try {
		Thread.sleep((long) (b00m / 500000.0));
	    } catch (InterruptedException boom) {
		throw new RuntimeException("boom!", boom);
	    }
	} catch (NumberFormatException e) {
	    throw new RuntimeException(e);
	}
    }
}
