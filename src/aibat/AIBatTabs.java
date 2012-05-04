package aibat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class AIBatTabs extends JTabbedPane implements HyperlinkListener {
    private ArrayList<String[][]> allWarnings;

    private ArrayList<String> allDiffNames;

    // Number of checks that aren't specific .osu file checks
    public final static int numOverall = 3;

    // @formatter:off

    private final static String[] generalChecks = { 
	"Folder Size",// 0
	    "MP3 Bitrate",// 1
	    "Background",// 2 (Has BG + background size)
	    "Custom Hitsounds",// 3 File size > 44
    };

    private final static String[] skinSBChecks = { 
	"Missing Skin Files", // 0
	    "Skin Dimensions", // 1
	    "Multiple .osb Files", // 2
	    "Unused Image Files", // 3
	    "Codeless SB Elements", // 4
	    "Missing SB Elements",// 5 Could not find in folder
	    "SB Elements Not .png", // 6
	    "Epilepsy Warning", // 7
    };

    private final static String[] allChecks = { 
	"General and Metadata",// 0
	    "Timing Sections",// 1
	    "Combo Colours",// 2
	    "Tags",// 3
	    "Kiai"// 4
    };

    private final static String[] osuChecks = { "Snapping",// 0
	    "Breaks",// 1
	    "Spinner Length",// 2
	    "Spinner New Combo", // 3
	    "Catmull Sliders",// 4
	    "Stack Leniency",// 5
	    "Kiai Times Not On Downbeat",// 6
	    "Preview Point",// 7
    };
    // @formatter:on
    private static final String TIME_REGEX = "(\\d{2}:\\d{2}:\\d{3}(?: \\(\\d+\\))?(?: - )?)";

    private static final String LINK_REGEX = "<A Href=\"$1\">$1</A>";

    private boolean popupDisabled = false;

    private Consolidator c;

    // Without a consolidator
    public AIBatTabs() {
	super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    // Add a General panel, an All .osu files panel, and a panel for each diff
    public AIBatTabs(Consolidator con) {
	super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	c = con;
	allWarnings = new ArrayList<String[][]>();
	allDiffNames = new ArrayList<String>();
	addTab("General", generalPanel(c));
	addTab("Skin/SB", skinSBPanel(c.getSkinSBChecker()));
	addTab("All .osu Files", allPanel(c));
	for (OsuFileParser o : c.getOsuFileParsers()) {
	    OsuFileChecker ofc = o.getOsuFileChecker();
	    allDiffNames.add(o.getDiff());
	    addTab(o.getDiff(), osuPanel(ofc));
	}

    }

    // Panel for general warnings
    private JPanel generalPanel(Consolidator c) {
	// @formatter:off
	String[] results = { c.checkFolderSize(),
		c.checkBitrate(), // mp3 bitrate
		c.getSkinSBChecker().checkBG(),
		c.checkCustomHitsounds(),// custom hitsounds
	};
	// @formatter:on

	String[][] warn = { generalChecks, results };
	allWarnings.add(warn);
	return panelWithHTMLText(warningMessage(warn));
    }

    private JPanel skinSBPanel(SkinSBChecker s) {
	// @formatter:off
	String[] results = { s.getMissingSkin(), s.getSkinProblems(),

	s.checkMultipleOsb(), s.checkUnusedImages(), s.checkCodelessElements(),
		s.checkMissingElements(), s.checkPNG(), s.suggestEpilepsy(), };
	// @formatter:on

	String[][] warn = { skinSBChecks, results };

	allWarnings.add(warn);
	return panelWithHTMLText(warningMessage(warn));
    }

    // Panel for consistency in all .osu files
    private JPanel allPanel(Consolidator c) {
	// @formatter:off
	String[] results = { c.checkGenMeta(), c.getRedPointsCheck(),
		c.getColoursCheck(), c.checkTags(), c.getKiaiCheck(), };
	// @formatter:on

	String[][] warn = { allChecks, results };
	allWarnings.add(warn);
	return panelWithHTMLText(warningMessage(warn));// TODO
    }

    // Panel for each .osu file
    private JPanel osuPanel(OsuFileChecker ofc) {
	// Array to store all warnings in the osu file.
	if (ofc == null)
	    return null;
	//@formatter:off
	String[] results = { ofc.getSnapCheck(),
		ofc.getBreaksCheck(),
		ofc.getSpinLengthCheck(),
		ofc.getSpinNCCheck(),
		ofc.getCatmullCheck(),
		ofc.getStackLenCheck(),
		ofc.getWrongKiais(),
		ofc.getPreviewCheck(), };
	// @formatter:on

	String[][] warn = { osuChecks, results };
	allWarnings.add(warn);
	return panelWithHTMLText(warningMessage(warn));
    }

    // Utility
    // *****************************************************************************

    private String warningMessage(String[][] warn) {
	StringBuffer toShow = new StringBuffer();
	for (int i = 0; i < warn[0].length; i++) {
	    toShow.append("<B><U>" + warn[0][i] + "</U></B>\n");
	    // Check to see if warning is null (didn't initialize fully), empty
	    // (no warning).
	    if (warn[1][i] == null)
		System.out.println("Failed to properly initialize "
			+ warn[0][i]);// TODO handle properly
	    else if (warn[1][i].length() > 0) {
		toShow.append(warn[1][i]);
	    }
	    toShow.append("\n");
	}
	return toShow.toString();
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
	    Object[] o = { "OK", "Don't show this popup again" };
	    Util.copyStringToClipboard(e.getDescription());
	    if (!popupDisabled) {
		int n = JOptionPane.showOptionDialog(this,
			"Successfully copied to clipboard.", "Copied",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.INFORMATION_MESSAGE, null, o, // the titles
								  // of buttons
			o[0]); // default button title
		popupDisabled = n == JOptionPane.NO_OPTION;
	    }
	}
    }

    private JPanel panelWithHTMLText(String toShow) {
	JPanel panel = new JPanel();
	panel.setBackground(Color.LIGHT_GRAY);

	JEditorPane textArea = new JEditorPane();
	textArea.setEditorKit(new HTMLEditorKit());
	textArea.setForeground(Color.BLACK);

	Font font = UIManager.getFont("Label.font");
	String bodyRule = "body { font-family: " + font.getFamily() + "; "
		+ "font-size: " + font.getSize() + "pt; }";
	((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule(
		bodyRule);

	toShow = toShow.replaceAll(TIME_REGEX, LINK_REGEX);
	toShow = toShow.replaceAll("\\n", "<br />");
	textArea.setText(toShow);
	textArea.setEditable(false);
	textArea.addHyperlinkListener(this);

	JScrollPane scrollPane = new JScrollPane(textArea);
	scrollPane.setPreferredSize(new Dimension(600, 500));
	// textArea.scrollRectToVisible( new Rectangle( 100, 100, 100, 100 ) );

	panel.add(scrollPane);
	// try
	// {
	// Rectangle r = textArea.modelToView( 10 );
	// System.out.println(r==null);
	// textArea.scrollRectToVisible( r );
	// }
	// catch ( BadLocationException e )
	// {
	// e.printStackTrace();
	// }

	return panel;
    }

    // Generic panel that displays text
    // private JPanel panelWithText( String toShow )
    // {
    // JPanel panel = new JPanel();
    // panel.setBackground( Color.LIGHT_GRAY );
    //
    // JTextArea textArea = new JTextArea( 30, 55 );
    // textArea.setForeground( Color.BLACK );
    // textArea.setFont( new Font( "SansSerif", Font.PLAIN, 12 ) );
    // textArea.setText( toShow );
    // textArea.setEditable( false );
    //
    // JScrollPane scrollPane = new JScrollPane( textArea );
    // // Rectangle r = textArea.getBounds();
    // textArea.scrollRectToVisible( new Rectangle( 0, 0, 0, 0 ) );
    //
    // panel.add( scrollPane );
    // return panel;
    // }

    // The following three format all warnings for forum use, then copy
    // TODO update formatting to match forums, esp. with [list]
    private String getAllWarnings() {
	String result = "";
	for (int i = 0; i < allWarnings.size(); i++) {
	    String warning = allWarningsHelper(allWarnings.get(i));
	    if (warning.length() == 0)
		continue;
	    switch (i) {
	    case 0:
		result += "[General]";
		break;
	    case 1:
		result += "[Skin/SB]";
		break;
	    case 2:
		result += "[All .osu Files]";
		break;
	    default:
		result += "[" + allDiffNames.get(i - numOverall) + "]";
	    }
	    result += "\n";
	    result += warning + "\n";
	}
	result = "[box=AIBat Warnings]These warnings were generated by "
		+ "[url=http://osu.ppy.sh/forum/viewtopic.php?f=2&t=55305]"
		+ AIBatWindow.VERSION
		+ "[/url], based on the current ranking guidelines and rules."
		+ " Some things (like breaks and spinners) can be ignored,"
		+ " but just know that it's generally bad practice.\n\n"
		+ result.replaceAll("\n\n\n", "\n\n") + "[/box]";
	return result;
    }

    private String allWarningsHelper(String[][] warn) {
	String result = "";
	for (int i = 0; i < warn[0].length; i++) {
	    // s = warning for a certain item, "" if nothing.
	    String s = warn[1][i];
	    if (s.length() > 0) {
		result += warn[0][i] + ":\n" + s + "\n";
	    }
	}
	// if ( result.length() == 0 )
	// result = "No problems found.\n";
	return result;
    }

    public void copyAllWarningsToClipboard() {
	Util.copyStringToClipboard(getAllWarnings());
	JOptionPane.showMessageDialog(null,
		"Successfully copied to clipboard.", "Copied",
		JOptionPane.INFORMATION_MESSAGE);
    }

    // TODO integrate as another tab?
    public void exportHitsound() {
	StringBuilder toShow = new StringBuilder();
	toShow.append("The following shows the locations of all objects with the specified hitsound, exported into a format for\n"
		+ "Bookmarks in the .osu file. Click to copy the entire line to the clipboard, and paste into the appropriate line\n"
		+ "(directly below \"[Editor]\") in the .osu file.\n");
	for (OsuFileParser o : c.getOsuFileParsers()) {
	    toShow.append("<B><U>" + o.getDiffBoxed() + "</U></B>\n");
	    OsuFileChecker ofc = o.getOsuFileChecker();
	    ofc.processHitsoundBookmark();
	    String whistle = "Bookmarks: " + ofc.getWhistle();
	    String finish = "Bookmarks: " + ofc.getFinish();
	    String clap = "Bookmarks: " + ofc.getClap();
	    toShow.append("Whistles:\n");
	    toShow.append("<A Href=\"" + whistle + "\">" + whistle + "</A>\n");
	    toShow.append("Finishs:\n");
	    toShow.append("<A Href=\"" + finish + "\">" + finish + "</A>\n");
	    toShow.append("Claps:\n");
	    toShow.append("<A Href=\"" + clap + "\">" + clap + "</A>\n");
	}
	JPanel p = panelWithHTMLText(toShow.toString());
	this.insertTab("Hitsounds", null, p, null, 0);
	this.setSelectedIndex(0);
    }

}
