package tabs;

import java.util.TreeMap;

import aibat.AIBatMenu;

import modtrace.ModTrace;

public class ModTraceTab extends ContentTab implements Runnable {

    private static final long REFRESH_INTERVAL = 4000;

    private ModTrace mt;

    private Thread thread;

    private AIBatMenu menu;

    private final static String WELCOME_MSG = "<b><u><a style=\"font-family:georgia;font-size:16\">Usage</a></u>\n"
	    + "&#x2713; Open a map in AIBat (done!)\n"
	    + "1. Mod that map in the osu! editor.\n"
	    + "2. Save.\n"
	    + "3. The ModTrace tab shows your changes!</b>\n\n"

	    + "<u><a style=\"font-family:georgia;font-size:16\">Tips</a></u>\n"
	    + "'ModTrace' > 'Pause Modtrace' will pause the automatic refreshing.\n\n"

	    + "'ModTrace' > 'Show Bookmarks' will indicate in-editor bookmarks. "
	    + "Use them to insert comments to the mapper!\n\n"

	    + "Use ModTrace for rechecks, too! "
	    + "Open the fixed map in AIBat and continue from previous; "
	    + "the mapper's changes since you last ran ModTrace are shown.\n\n"

	    + "<u><a style=\"font-family:georgia;font-size:16\">WARNING</a></u>\n"
	    + "ModTrace currently looks at hit objects and hit objects ONLY. "
	    + "This means that timing and metadata changes ARE NOT tracked. "
	    + "If you are going to make timing changes, do those FIRST and resnap all, "
	    + "then refresh AIBat and start ModTrace over.";

    public ModTraceTab(ModTrace mt, AIBatMenu menu) {
	super();
	this.mt = mt;
	this.menu = menu;
	thread = new Thread(this);
	thread.start();
	DEFAULT_TEXT = WELCOME_MSG.replaceAll("\\n", "<br />");
    }

    @Override
    public String getTabName() {
	return "*ModTrace";
    }

    @Override
    protected void fillAllContent() {
	allContent = new TreeMap<String, String>();
	allContent.putAll(mt.compareAll());
    }

    public void refresh() {
	if (menu.pauseModTrace())
	    return;
	fillAllContent();
	String toShow = allContentToString(FORMAT_TO_HTML);
	showText(toShow);
	// invalidate();
	// validate();
	System.out.println("ModTrace refreshed");
    }

    @Override
    public void run() {
	while (true) {
	    refresh();
	    try {
		Thread.sleep(REFRESH_INTERVAL);
	    }
	    catch (InterruptedException e) {
		e.printStackTrace();
		return;
	    }
	}
    }

    public void stop() {
	thread.interrupt();
    }

}
