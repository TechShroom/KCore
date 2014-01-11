package k.core.util.timing;

/**
 * A lower resolution FPS counter than that in EmergencyLanding, due to the fact
 * that it doesn't rely on LWJGL's timing system, but on Java's default.
 * 
 * @author Kenzie Togami
 * 
 */
public class LowResFPS {

    static {
	// Starts Windows hack if needed
	String osName = System.getProperty("os.name");

	if (osName.startsWith("Win")) {
	    // On windows the sleep functions can be highly inaccurate by
	    // over 10ms making in unusable. However it can be forced to
	    // be a bit more accurate by running a separate sleeping daemon
	    // thread.
	    Thread timerAccuracyThread = new Thread(new Runnable() {
		@Override
		public void run() {
		    try {
			Thread.sleep(Long.MAX_VALUE);
		    } catch (Exception e) {
		    }
		}
	    });

	    timerAccuracyThread.setName("KCore Win Fix");
	    timerAccuracyThread.setDaemon(true);
	    timerAccuracyThread.start();
	}
    }
    public static final long millis = 1000, micro = 1000 * LowResFPS.millis;
    public static int maxFPSCounters = 100;
    private static int nextIndex = 1;
    /** time at last frame */
    private static long[] lastFrame = new long[LowResFPS.maxFPSCounters];
    /** frames per second */
    private static int[] fps = new int[LowResFPS.maxFPSCounters];
    /** last fps time */
    private static long[] lastFPS = new long[LowResFPS.maxFPSCounters];
    private static boolean[] enabled = new boolean[LowResFPS.maxFPSCounters];

    public static int update(int index) {
	int del = LowResFPS.getDelta(index);
	LowResFPS.updateFPS(index);
	return del;
    }

    public static void init(int index) {
	LowResFPS.init(index, LowResFPS.millis);
    }

    public static void init(int index, long divis) {
	LowResFPS.getDelta(index, divis);
	LowResFPS.lastFPS[index] = LowResFPS.getTime(divis);
    }

    /**
     * Calculate how many milliseconds have passed since last frame.
     * 
     * @param index
     *            - index of the LowResFPS counter
     * 
     * @return milliseconds passed since last frame
     */
    public static int getDelta(int index) {
	return LowResFPS.getDelta(index, LowResFPS.millis);
    }

    /**
     * Calculate how many divisions have passed since last frame.
     * 
     * @param index
     *            - index of the LowResFPS counter
     * @param divis
     *            - the division to return
     * 
     * @return divisions passed since last frame
     */
    public static int getDelta(int index, long divis) {
	long time = LowResFPS.getTime(divis);
	int delta = (int) (time - LowResFPS.lastFrame[index]);
	LowResFPS.lastFrame[index] = time;

	return delta;
    }

    public static long getTime(long divis) {
	return System.currentTimeMillis();
    }

    /**
     * Get the semi-accurate system time
     * 
     * @param index
     * 
     * @return The system time in milliseconds
     */
    public static long getTime() {
	return LowResFPS.getTime(LowResFPS.millis);
    }

    /**
     * Calculate the FPS
     */
    private static void updateFPS(int index) {
	if (LowResFPS.getTime() - LowResFPS.lastFPS[index] > 1000) {
	    LowResFPS.fps[index] = 0;
	    LowResFPS.lastFPS[index] += 1000;
	}
	LowResFPS.fps[index]++;
    }

    public static void enable(int index) {
	LowResFPS.enabled[index] = true;
    }

    public static void disable(int index) {
	LowResFPS.enabled[index] = false;
    }

    public static int genIndex() {
	if (LowResFPS.nextIndex > LowResFPS.maxFPSCounters) {
	    throw new IndexOutOfBoundsException("Too many LowResFPS counters");
	}
	return LowResFPS.nextIndex++;
    }

    public static int getFPS(int index) {
	return fps[index];
    }
}
