package k.core.util.math;

import static java.awt.GridBagConstraints.CENTER;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import k.core.util.gui.SpecialReader;
import k.core.util.gui.TextFieldReader;

public class ExpressionTest {
    static JLabel errorLabel = new JLabel();
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("ExprTest");
        JPanel j = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(
                "Enter an expression here, it will print to STDERR:");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        j.add(label, gbc);
        JTextField field = new JTextField(65);
        System.setIn(new TextFieldReader(field));
        gbc.gridy = 1;
        j.add(field, gbc);
        frame.add(j);
        gbc.gridy = 2;
        j.add(errorLabel, gbc);
        frame.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screen.width / 2) - j.getWidth() / 2,
                (screen.height / 2) - j.getHeight() / 2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        read();
    }

    private static void read() {
        SpecialReader buffered = new SpecialReader(System.in);
        for (String line = ""; line != null; line = buffered.readLine()) {
            // clean errors on input
            if (currentCleanerThread != null && currentCleanerThread.isAlive()) {
                currentCleanerThread.interrupt();
                try {
                    currentCleanerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (currentCleanerThread != null) {
                currentCleanerThread = null;
            }
            try {
                if (EOperation.hasOP(line)) {
                    System.err.println(ExtraMath.solveExpression(line));
                }
            } catch (NumberFormatException nfe) {
                writeException(nfe);
            } catch (RuntimeException re) {
                Throwable cause = re.getCause();
                if (cause instanceof NumberFormatException) {
                    writeException((NumberFormatException) cause);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            buffered.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeException(Exception e) {
        try {
            writeErrorAndClear(e.toString(), 15);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    static Thread currentCleanerThread = null;

    public static void writeErrorAndClear(final String err,
            final int secondsToKeep) throws InterruptedException,
            InvocationTargetException {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (currentCleanerThread != null && currentCleanerThread.isAlive()) {
            // clean the old replacer thread, or it will keep going
            currentCleanerThread.interrupt();
            currentCleanerThread.join();
        } else if (currentCleanerThread != null) {
            currentCleanerThread = null;
        }
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                errorLabel.setText(err);
                errorLabel.invalidate();
                frame.pack();
                frame.setLocation((screen.width / 2) - frame.getWidth() / 2,
                        (screen.height / 2) - frame.getHeight() / 2);
            }
        });
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(secondsToKeep * 1000);
                } catch (InterruptedException e) {
                }
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            errorLabel.setText("");
                            errorLabel.invalidate();
                            frame.pack();
                            frame.setLocation(
                                    (screen.width / 2) - frame.getWidth() / 2,
                                    (screen.height / 2) - frame.getHeight() / 2);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                currentCleanerThread = null;
            }
        };
        currentCleanerThread = new Thread(r, "Label Remover Thread");
        currentCleanerThread.setDaemon(true);
        currentCleanerThread.start();
    }
}
