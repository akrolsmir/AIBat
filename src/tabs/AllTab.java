package tabs;

import aibat.Consolidator;

public class AllTab extends AIBatTab {
    
    private Consolidator c;

    public AllTab(Consolidator c)
    {
	super();
	this.c = c;
	fillAllContent();
	showText(allContentToString(FORMAT_TO_HTML));
    }

    @Override
    protected void fillAllContent() {
	allContent.put("General and Metadata", c.checkGenMeta());
	allContent.put("Timing Sections", c.getRedPointsCheck());
	allContent.put("Combo Colours", c.getColoursCheck());
	allContent.put("Tags", c.checkTags());
	allContent.put("Kiai", c.getKiaiCheck());

    }

}
