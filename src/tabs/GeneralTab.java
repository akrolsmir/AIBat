package tabs;

import java.awt.LayoutManager;

import aibat.Consolidator;

public class GeneralTab extends AIBatTab {
    
    private Consolidator c;

    public GeneralTab (Consolidator c)
    {
	//SO KLUDGY... WHY MUST SUPER BE FIRST?
    }
    
    private static boolean workaround(Consolidator c) {
	//con = c;
	//RAAAA
	return true;
    }

    @Override
    protected void fillAllContent() {
	allContent.put("Folder Size", c.checkFolderSize());
	allContent.put("MP3 Bitrate", c.checkBitrate());
	allContent.put("Background", c.getSkinSBChecker().checkBG()); // (Has BG + background size)
	allContent.put("Custom Hitsounds", c.checkCustomHitsounds()); // File size > 44
    }
    
    public static void main (String[] args){
	new GeneralTab(null);
    }
}
