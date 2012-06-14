package aibat;

// import java.util.*;
// import java.io.*;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import modtrace.ModTrace;
import tabs.AIBatTabbedPane;
import tabs.HitsoundsTab;
import updater.Updater;

//TODO shouldn't inherit Action/Key listener
public class AIBatWindow extends JFrame implements ActionListener, KeyListener {
    public final static String VERSION = "AIBat v2.0";

    private JTextField textField;
    private String directory, songFolderLoc;
    private boolean fileOpened;
    private AIBatTabbedPane tabs;
    private JPanel panel;
    private Consolidator c;
    private Searcher2 searcher;

    private Thread modTraceThread = new Thread();

    public AIBatWindow() {
	super(VERSION);

	directory = System.getProperty("user.dir");
	try {
	    songFolderLoc = new SettingsManager().getSongFolderLoc();
	    if (songFolderLoc.length() > 0)
		directory = songFolderLoc;
	}
	catch (Exception e) {
	    Util.errorException(e);
	}
	AIBatMenu menu = new AIBatMenu(this);
	setJMenuBar(menu);

	panel = new JPanel(new BorderLayout());

	tabs = new AIBatTabbedPane();
	tabs.addKeyListener(this);
	tabs.addTab("Startup", startupPanel());

	panel.add(new AIBatToolbar(this), BorderLayout.PAGE_START);
	panel.add(tabs, BorderLayout.CENTER);
	add(panel);

	fileOpened = false;

    }

    private JPanel startupPanel() {
	JPanel panel = new JPanel();
	panel.setBackground(Color.LIGHT_GRAY);

	textField = new JTextField(40);
	textField.setForeground(Color.BLACK);
	textField.setText(System.getProperty("user.dir"));
	textField.addKeyListener(this);

	Button browseButton = new Button("Browse");
	browseButton.addActionListener(this);
	browseButton.setActionCommand("browseButton");
	browseButton.addKeyListener(this);

	Button enterButton = new Button("Enter");
	enterButton.addActionListener(this);
	enterButton.setActionCommand("enterButton");
	enterButton.addKeyListener(this);

	panel.add(textField);
	panel.add(enterButton);
	panel.add(browseButton);

	return panel;
    }

    public static void main(String[] args) {
	long start = System.currentTimeMillis();
	try {
	    UIManager
		    .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	}
	catch (Throwable e) {
	    e.printStackTrace();
	}
	final AIBatWindow window = new AIBatWindow();

	// Adds a confirmation box instead of directly closing
	window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	window.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		int n = JOptionPane.showOptionDialog(window,
			"Are you sure you want to exit?", "Confirm Close",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (n == JOptionPane.YES_OPTION)
		    System.exit(0);
	    }
	});

	// window.pack();
	window.setSize(800, 600);
	window.setLocationRelativeTo(null);

	// TODO thread the search function as well
	window.search();
	window.setVisible(true);
	window.getSearcher().focus();
	Util.logTime(start);
	new Thread(new Updater());
	Util.logTime(start);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
	if (e.getKeyChar() == KeyEvent.VK_ENTER)// && !fileOpened )
	{
	    ActionEvent fakeEvent = new ActionEvent(this, 42, "enterButton");
	    actionPerformed(fakeEvent);
	}
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
	if (evt.getActionCommand().equals("enterButton")) {
	    switchTo(textField.getText());
	}
	else if (evt.getActionCommand().equals("browseButton")) {
	    String s = chooseDirectory("Browse");
	    if (s != null) {
		textField.setText((s));
		directory = s;
	    }
	}
    }

    public String chooseDirectory(String type) {
	return chooseDirectory(type, type);
    }

    public String chooseDirectory(String type, String buttonText) {
	return Util.chooseDirectory(directory, type, type);
    }

    // Switch which folder to analyze
    public void switchTo(String newFolder) {
	try {
	    long start = System.currentTimeMillis();
	    if (songFolderLoc != null && songFolderLoc.equals(newFolder)) {
		Util.errorMessage(
			"Please don't try to load your entire osu! Songs Folder here.\n"
				+ "It'll probably take forever and lag your computer.",
			this);
		return;
	    }
	    File f = new File(newFolder);
	    // TODO thread properly, have loading screen
	    if (f.exists() && f.isDirectory()) {
		directory = newFolder;
		c = new Consolidator(f);
		panel.remove(tabs);
		tabs = new AIBatTabbedPane(c);
		panel.add(tabs, BorderLayout.CENTER);
		this.invalidate();
		this.validate();
		fileOpened = true;
		if (tabs.getTabCount() <= AIBatTabbedPane.NUM_OVERALL)
		    Util.errorMessage("No .osu files found.", this);
		this.setTitle(VERSION + " - " + Util.cutPath(newFolder));
		tabs.requestFocusInWindow();

		// TODO Start ModTrace
		modTraceThread.interrupt();
		modTraceThread = new Thread(new ModTrace(c.getOsuFiles(), tabs));
		modTraceThread.start();

	    }
	    else
		Util.errorMessage("Folder not found.", this);
	    Util.logTime(start);
	}
	catch (Exception e) {
	    Util.errorException(e, newFolder);
	}
    }

    public String getDirectory() {
	return directory;
    }

    public boolean isFileOpened() {
	return fileOpened;
    }

    public void copyAllWarningsToClipboard() {
	if (fileOpened)
	    tabs.copyAllWarningsToClipboard();
	else
	    Util.errorMessage("Please load a folder first.", this);
    }

    public void refreshCurrentFolder() {
	if (isFileOpened())
	    switchTo(getDirectory());
    }

    public void openFolder() {
	String s = chooseDirectory("Open");
	if (s != null) {
	    switchTo(s);
	}
    }

    public void exportHitsounds() {
	if (fileOpened) {
	    tabs.addTab("Hitsounds", new HitsoundsTab(c));
	    tabs.focusLast();
	}
	else {
	    Util.errorMessage("Please load a folder first.", this);
	}
    }

    public void search() {
	searcher = new Searcher2(this);
	if (searcher.getSongFolderLoc().length() > 0) {
	    tabs.addTab("Search", searcher.searchPanel());
	    tabs.focusLast();
	    searcher.focus();
	}
    }

    public void search(String toSearch) {
	search();
	searcher.searchText(toSearch);
    }

    public Searcher2 getSearcher() {
	return searcher;
    }

    public void explore() {
	try {
	    Util.openFolder(getDirectory());
	}
	catch (Exception e1) {
	    Util.errorException(e1);
	}

    }

}
