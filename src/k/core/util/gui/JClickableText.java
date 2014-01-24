package k.core.util.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class JClickableText extends JLabel implements MouseListener {

    private static final long serialVersionUID = 2309113043112309166L;
    private Action act = null;
    private Rectangle bounds_temp = getBounds();
    private Color up, p;

    public JClickableText(String text, Color unpressed, Color pressed) {
        super(text);
        addMouseListener(this);
        up = unpressed;
        p = pressed;
        pressed(false);
    }

    public JClickableText(String text) {
        this(text, Color.BLACK, Color.LIGHT_GRAY);
    }

    private void pressed(boolean on) {
        if (on) {
            setForeground(p);
        } else {
            setForeground(up);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        pressed(true);
        pressed(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed(false);
        if (act != null && getProperBounds(bounds_temp).contains(e.getPoint())) {
            act.actionPerformed(new ActionEvent(this, 0, getText()));
        }
    }

    /**
     * Gets the bounds at x=0 and y=0 instead of with the offsets used by
     * {@link JComponent#getBounds(Rectangle)}
     * 
     * @see JComponent#getBounds(Rectangle)
     */
    public Rectangle getProperBounds(Rectangle rv) {
        rv = getBounds(rv);
        rv.x = rv.y = 0;
        return rv;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void setAction(Action a) {
        act = a;
    }

}
