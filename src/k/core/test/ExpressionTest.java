package k.core.test;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import k.core.util.math.EOperation;
import k.core.util.math.ExtraMath;

public class ExpressionTest {

    public static void main(String[] args) {
        JFrame j = new JFrame("ExprTest");
        JTextField field = new JTextField(65);
        System.setIn(new TextFieldReader(field));
        j.add(field);
        j.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        j.setLocation((screen.width / 2) - j.getWidth() / 2,
                (screen.height / 2) - j.getHeight() / 2);
        j.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        j.setVisible(true);
        read();
    }

    private static void read() {
        SpecialReader buffered = new SpecialReader(System.in);
        for (String line = ""; line != null; line = buffered.readLine()) {
            if (EOperation.hasOP(line)) {
                System.err.println(ExtraMath.solveExpression(line));
            }
        }
        try {
            buffered.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
