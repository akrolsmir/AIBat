package tabs;

import aibat.SkinSBChecker;

public class SkinSBTab extends ContentTab {

    private SkinSBChecker s;

    public SkinSBTab(SkinSBChecker s) {
	super();
	this.s = s;
	fillAllContent();
	showText(allContentToString(FORMAT_TO_HTML));
	tabName = "Skin/SB";
    }

    @Override
    protected void fillAllContent() {
	allContent.put("Missing Skin Files", s.getMissingSkin());
	allContent.put("Skin Dimensions", s.getSkinProblems());
	allContent.put("Multiple .osb Files", s.checkMultipleOsb());
	allContent.put("Unused Image Files", s.checkUnusedImages());
	allContent.put("Codeless SB Elements", s.checkCodelessElements());
	allContent.put("Missing SB Elements", s.checkMissingElements());
	allContent.put("SB Elements Not .png", s.checkPNG());
	//allContent.put("Epilepsy Warning", s.suggestEpilepsy());
    }

}
