package tabs;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import aibat.AIBatWindow;
import aibat.Consolidator;
import aibat.OsuFileParser;
import aibat.Util;

public class AIBatTabbedPane extends JTabbedPane {

    // Number of checks that aren't specific .osu file checks
    public final static int NUM_OVERALL = 3;

    // List of tabs that are reports
    private List<ContentTab> allReports = new ArrayList<ContentTab>();

    private Consolidator c;

    // Without a consolidator
    public AIBatTabbedPane() {
	super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    // Add a General panel, an All .osu files panel, and a panel for each diff
    public AIBatTabbedPane(Consolidator c) {
	super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	this.c = c;

	allReports.add(new GeneralTab(c));
	allReports.add(new SkinSBTab(c.getSkinSBChecker()));
	allReports.add(new AllTab(c));
	for (OsuFileParser ofp : c.getOsuFileParsers()) {
	    allReports.add(new osuDiffTab(ofp));
	}
	// TODO ofc should not be a member of ofp, but rather separate?

	for (ContentTab tab : allReports) {
	    addTab(tab.getTabName(), tab);
	}
    }

    private String getAllWarnings() {
	StringBuilder result = new StringBuilder();
	int count = this.getTabCount();
	for (int i = 0; i < count; i++) {
	    Component comp = this.getComponentAt(i);
	    if (!(comp instanceof ContentTab))
		continue;
	    ContentTab tab = (ContentTab) (comp);
	    String content = tab
		    .allContentToString(ContentTab.FORMAT_TO_BBCODE);
	    if (content != null && content.length() > 0)
		// TODO use if/when [notice] is worth it and parsing is fixed.
		result.append("\n[" + tab.getTabName() + "]\n[notice]"
			+ content + "[/notice]\n");
	    // result.append("\n[" + tab.getTabName() + "]\n" + content);
	}
	return result.toString();
    }

    public void copyAllWarningsToClipboard() {
	Util.copyStringToClipboard("[quote=\"" + AIBatWindow.VERSION + "\"]"
		+ getAllWarnings() + "[/quote]",
		"Copied all warnings to the clipboard",
		this.getTopLevelAncestor());
    }

    public void focusLast() {
	this.setSelectedIndex(this.getTabCount() - 1);
    }

}
