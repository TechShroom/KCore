package k.core.util.gui;

import java.io.PrintStream;

public class ConsoleTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PrintStream out = SideConsole.earlyChainO(System.out);
        PrintStream err = SideConsole.earlyChainE(System.err);
        System.setOut(out);
        System.setErr(err);
        System.err.println("early_error_test");
        System.out.println("early_stdout_test");
        SideConsole c = new SideConsole(false);
        c.error(true);
        System.err.println("error_test");
        System.out.println("stdout_test");
    }

}
