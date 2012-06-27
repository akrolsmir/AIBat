package tabs;

import aibat.Consolidator;

public class GeneralTab extends ContentTab {

    private Consolidator c;

    public GeneralTab(Consolidator c) {
	// SO KLUDGY... WHY MUST SUPER BE FIRST?
	super();
	this.c = c;
	fillAllContent();
	showText(allContentToString(FORMAT_TO_HTML));
	tabName = "General";
    }

    @Override
    protected void fillAllContent() {
	allContent.put("Folder Size", c.checkFolderSize());
	allContent.put("MP3 Bitrate", c.checkBitrate());
	allContent.put("Background", c.getSkinSBChecker().checkBG());
	allContent.put("Custom Hitsounds", c.checkCustomHitsounds());
    }

}
