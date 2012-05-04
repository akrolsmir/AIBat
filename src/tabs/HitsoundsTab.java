package tabs;

import aibat.Consolidator;
import aibat.OsuFileChecker;
import aibat.OsuFileParser;

public class HitsoundsTab extends AIBatTab {
    
    private final static String EXPLANATION = "The following shows the locations of all objects with the specified hitsound, exported into a format for\n"
	+ "Bookmarks in the .osu file. Click to copy the entire line to the clipboard, and paste into the appropriate line\n"
	+ "(directly below \"[Editor]\") in the .osu file.\n\n";
    private Consolidator c;
    
    public HitsoundsTab(Consolidator c) {
	// SO KLUDGY... WHY MUST SUPER BE FIRST?
	super();
	this.c = c;
	fillAllContent();
	showText(allContentToString(FORMAT_TO_PLAIN));
	System.out.println(allContentToString(FORMAT_TO_PLAIN));
    }

    @Override
    protected void fillAllContent() {
	allContent.put("Explanation", EXPLANATION);
	for (OsuFileParser o : c.getOsuFileParsers()) {
	    //toShow.append("<B><U>" + o.getDiffBoxed() + "</U></B>\n");
	    StringBuilder toShow = new StringBuilder();
	    OsuFileChecker ofc = o.getOsuFileChecker();
	    ofc.processHitsoundBookmark();
	    String whistle = "Bookmarks: " + ofc.getWhistle();
	    String finish = "Bookmarks: " + ofc.getFinish();
	    String clap = "Bookmarks: " + ofc.getClap();
	    toShow.append("Whistles:\n");
	    toShow.append("<a href=\"" + whistle + "\">" + whistle + "</a>\n");
	    toShow.append("Finishs:\n");
	    toShow.append("<a href=\"" + finish + "\">" + finish + "</a>\n");
	    toShow.append("Claps:\n");
	    toShow.append("<a href=\"" + clap + "\">" + clap + "</a>\n");
	    allContent.put(o.getDiffBoxed(), toShow.toString());
	}

    }
    
    @Override
    public String getTabName() {
	return "Hitsounds";
    }

}
