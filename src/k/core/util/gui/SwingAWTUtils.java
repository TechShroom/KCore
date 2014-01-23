package k.core.util.gui;

import javax.swing.SwingUtilities;

public class SwingAWTUtils {
    public void runOnDispatch(Runnable r) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeAndWait(r);
        }
    }
}
