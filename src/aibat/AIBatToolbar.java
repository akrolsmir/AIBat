package aibat;

import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class AIBatToolbar extends JToolBar {

    private AIBatWindow window;
    private JTextField searchField;

    public AIBatToolbar(AIBatWindow w) {
	super();
	this.window = w;

	// setFloatable(false);
	// setRollover(true);

	//TODO load seperately
	//TODO credit famfamfam
	JButton openButton = new JButton("Open", createImageIcon(
		"folder_explore.png", ""));
	openButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		window.openFolder();
	    }
	});
	add(openButton);

	JButton refreshButton = new JButton("Refresh", createImageIcon(
		"arrow_refresh.png", ""));
	add(refreshButton);
	refreshButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		window.refreshCurrentFolder();
	    }
	});

	JButton copyButton = new JButton("Copy", createImageIcon(
		"page_copy.png", ""));
	add(copyButton);
	copyButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		window.copyAllWarningsToClipboard();
	    }
	});

	// addSeparator();
	addSeparator(new Dimension(15, 0));

	add(new JLabel(createImageIcon("magnifier.png", "wtf?")));

	searchField = new JTextField("Search...");
	searchField.setColumns(5);
	
	//Highlights the field when clicked
	searchField.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
		searchField.selectAll();
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
	    }
	});
	
	//Searches for the text, consumes the enter.
	searchField.addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent e) {
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER)// && !fileOpened )
		{
		    e.consume();
		    window.search(searchField.getText());
		}
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {

	    }
	});
	add(searchField);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path, String description) {
	java.net.URL imgURL = getClass().getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL, description);
	}
	else {
	    Util.errorMessage("Couldn't find file: " + path);
	    return null;
	}
    }

}
