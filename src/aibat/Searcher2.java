package aibat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Searcher2 {

    private String songFolderLoc = "";

    private ArrayList<String> folderNames;

    private AIBatWindow window;

    private JScrollPane listScroller;

    private JLabel description;

    private JList resultsList;

    private JTextField searchField;

    private SettingsManager sm;

    public Searcher2(AIBatWindow w) {
	window = w;
	folderNames = new ArrayList<String>();
	try {
	    sm = new SettingsManager();
	    songFolderLoc = new SettingsManager().getSongFolderLoc();
	}
	catch (FileNotFoundException e) {
	    // TODO unnecessary?
	    Util.errorSettings();
	    return;
	}
	// TODO move to AIBatMenu?
	if (songFolderLoc.length() == 0) {
	    // if no song folder was chosen
	    if (!promptForSongsFolder())
		return;
	}
	File songFolder = new File(songFolderLoc);
	if (!songFolder.exists()) {
	    Util.errorMessage(
		    "osu! Songs folder not found, try selecting it again.",
		    window);
	    return;
	}
	for (File file : new File(songFolderLoc).listFiles()) {
	    if (file.isDirectory())
		folderNames.add(file.getAbsolutePath());
	}
	Collections.sort(folderNames);
    }

    public JPanel searchPanel() {
	ArrayList<String> searchResults = new ArrayList<String>();
	for (String folder : folderNames) {
	    folder = Util.cutPath(folder);
	    searchResults.add(folder);
	}

	resultsList = new JList(searchResults.toArray());
	resultsList
		.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	resultsList.setVisibleRowCount(-1);
	resultsList.setSelectedIndex(0);
	resultsList.addKeyListener(new listAction());
	resultsList.setAlignmentX(Component.CENTER_ALIGNMENT);

	listScroller = new JScrollPane(resultsList);
	listScroller.setPreferredSize(new Dimension(330, 370));
	listScroller.setAlignmentX(Component.CENTER_ALIGNMENT);

	searchField = new JTextField(25);
	searchField.setForeground(Color.BLACK);
	searchField.setEditable(true);
	searchField.addKeyListener(new searchAction());
	searchField.setAlignmentX(Component.CENTER_ALIGNMENT);

	description = new JLabel(
		"Search for or select a folder, and hit the \"Enter\" key to view:");
	description.setAlignmentX(Component.CENTER_ALIGNMENT);

	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 60, 100));

	panel.setBackground(Color.LIGHT_GRAY);
	panel.add(description);
	panel.add(Box.createRigidArea(new Dimension(0, 10)));
	panel.add(searchField);
	panel.add(Box.createRigidArea(new Dimension(0, 10)));
	panel.add(listScroller);
	// TODO create button for enter.

	return panel;
    }

    private void goToEntered() {
	String chosen = (String) resultsList.getSelectedValue();
	if (chosen != null) {
	    window.switchTo(songFolderLoc + "\\" + chosen);
	}
    }

    private class listAction implements KeyListener {
	@Override
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER)
		goToEntered();

	    else if (e.getKeyCode() == KeyEvent.VK_UP
		    && resultsList.getSelectedIndex() == 0)
		searchField.requestFocusInWindow();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
    }

    private class searchAction implements KeyListener {
	@Override
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
		resultsList.setSelectedIndex(1);
		resultsList.requestFocusInWindow();
	    }
	}

	@Override
	public void keyReleased(KeyEvent e) {
	    String searchTerm = searchField.getText();
	    searchTerm = searchTerm.toLowerCase();
	    ArrayList<String> searchResults = new ArrayList<String>();
	    for (String folder : folderNames) {
		folder = Util.cutPath(folder);
		if (searchTerm.length() == 0
			|| folder.toLowerCase().indexOf(searchTerm) >= 0)
		    searchResults.add(folder);
	    }

	    resultsList.setListData(searchResults.toArray());
	    resultsList.setSelectedIndex(0);

	    if (e != null && e.getKeyCode() == KeyEvent.VK_ENTER) {
		System.out.println("searchAction Entered");// TODO remove
		goToEntered();
	    }
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	    System.out.println("searchActionTyped Entered");// TODO remove
	}

    }

    // True if successful
    public boolean promptForSongsFolder() {
	int n = JOptionPane
		.showConfirmDialog(
			window,
			"Song folder not yet assigned."
				+ "\nPlease select the Songs folder used by osu! to enable searching."
				+ "\nWould you like to do this now?", "Error",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
	if (n == JOptionPane.OK_OPTION) {
	    String folder = window.chooseDirectory("Select osu! Songs Folder",
		    "Select");
	    if (folder == null)
		return false;
	    sm.setSongFolderLoc(folder);
	    songFolderLoc = folder;
	    return true;
	}
	return false;
    }

    public String getSongFolderLoc() {
	return songFolderLoc;
    }

    public boolean focus() {
	if (searchField == null)
	    return false;
	return searchField.requestFocusInWindow();
    }

    // For use when you already have something to search
    public void searchText(String toSearch) {
	searchField.setText(toSearch);
	searchField.getKeyListeners()[0].keyReleased(null);
    }

    // public static void main( String[] args )
    // {
    // new Searcher2( new AIBatWindow() );
    // }
}
