package tabs;

import aibat.OsuFileChecker;

public class osuDiffTab extends AIBatTab {

    private OsuFileChecker ofc;

    public osuDiffTab(OsuFileChecker ofc) {
	super();
	this.ofc = ofc;
	fillAllContent();
	showText(allContentToString(FORMAT_TO_HTML));
    }

    @Override
    protected void fillAllContent() {
	allContent.put("Snapping", ofc.getSnapCheck());
	allContent.put("Breaks", ofc.getBreaksCheck());
	allContent.put("Spinner Length", ofc.getSpinLengthCheck());
	allContent.put("Spinner New Combo", ofc.getSpinNCCheck());
	allContent.put("Catmull Sliders", ofc.getCatmullCheck());
	allContent.put("Stack Leniency", ofc.getStackLenCheck());
	allContent.put("Non-Downbeat Kiai Times", ofc.getWrongKiais());
	allContent.put("Preview Point", ofc.getPreviewCheck());
    }

}
