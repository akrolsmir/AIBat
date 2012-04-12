package aibat;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Credit to <a href="http://stackoverflow.com/users/20654/oscarryz">Oscar
 * Reyes</a>
 */
public class Notification extends JPanel {

    private final static int DELAY = 1000;
    private final static String HTML_FORMAT = "<html><p style=\"color:white\">%s</p></html>";
    private final static Color BG_COLOR = Color.BLACK;
    private JLabel textToShow;
    private static JFrame f;
    private Component parent;

    //Uses mouse location
    public Notification(String textToShow) {
	this(textToShow, null);
    }

    //Uses parent location
    public Notification(String textToShow, Component parent) {
	this.textToShow = new JLabel(String.format(HTML_FORMAT, textToShow));
	this.parent = parent;
	showMessage();
    }

    private void showMessage() {
	f = new JFrame();
	//setTranslucency(f);
	f.setUndecorated(true);
	f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
	textToShow.setBackground(BG_COLOR);
	textToShow.setOpaque(true);
	textToShow.setBorder(new EmptyBorder(2, 2, 2, 2));
	
	f.add(textToShow);
	f.pack();

	if (parent == null)
	{
		Point p = MouseInfo.getPointerInfo().getLocation();
		p.translate(0, 25);
		f.setLocation(p);
	}
	else
	{
	    f.setLocation(
		    parent.getWidth() + parent.getX() - f.getWidth() - 10,
		    parent.getHeight() + parent.getY() - f.getHeight() - 10);
	}
	
	f.setVisible(true);
	new Timer().schedule(new TimerTask() {

	    @Override
	    public void run() {
		f.dispose();
	    }

	}, DELAY);
    }

    // @Override
    // public void actionPerformed(ActionEvent e) {
    // now.setTime(System.currentTimeMillis());

    // }

    public static void main(String[] args) {
	JFrame parent = new JFrame();
	parent.add(new JLabel("HELJKLJKLEJKLJLEJLkEJJELJKLEKJLJE"));
	parent.setVisible(true);
	parent.setBounds(200, 200, 300, 100);
	parent.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	new Notification("Copied: \"00:08:123\"", parent);
    }

    // taken from:
    // http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/
    private static void setTranslucency(Window window) {
	try {
	    Class<?> awtUtilitiesClass = Class
		    .forName("com.sun.awt.AWTUtilities");
	    Method mSetWindowOpacity = awtUtilitiesClass.getMethod(
		    "setWindowOpacity", Window.class, float.class);
	    mSetWindowOpacity.invoke(null, window, Float.valueOf(0.65f));
	}
	catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
