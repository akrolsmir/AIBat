package tabs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import aibat.Consolidator;
import aibat.OsuFileChecker;
import aibat.OsuFileParser;
import aibat.Util;

public class AIBatTabbedPane extends JTabbedPane {

    // Number of checks that aren't specific .osu file checks
    public final static int NUM_OVERALL = 3;

    // List of tabs that are reports
    private List<AIBatTab> allReports = new ArrayList<AIBatTab>();

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
	for (OsuFileParser ofp : c.getOsuFiles()) {
	    allReports.add(new osuDiffTab(ofp));
	}
	// TODO ofc should not be a member of ofp, but rather separate?

	for (AIBatTab tab : allReports) {
	    addTab(tab.getTabName(), tab);
	}
    }

    private String getAllWarnings() {
	StringBuilder result = new StringBuilder();
	for (AIBatTab tab : allReports) {
	    String content = tab.allContentToString(AIBatTab.FORMAT_TO_BBCODE);
	    if (content != null && content.length() > 0)
		result.append("[" + tab.getTabName() + "]\n" + content);
	}
	return result.toString();
    }

    public void copyAllWarningsToClipboard() {
	Util.copyStringToClipboard(getAllWarnings(),
		"Copied all warnings to the clipboard",
		this.getTopLevelAncestor());
    }

    public void exportHitsound() {
	this.insertTab("Hitsounds", null, new HitsoundsTab(c), null, 0);
	this.setSelectedIndex(0);
    }

}
