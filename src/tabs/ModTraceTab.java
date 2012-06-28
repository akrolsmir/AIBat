package tabs;

import java.util.TreeMap;

import modtrace.ModTrace;

public class ModTraceTab extends ContentTab implements Runnable {

    private static final long REFRESH_INTERVAL = 10000;
    private ModTrace mt;
    private Thread thread;
    private final static String WELCOME_MSG = "<u><a style=\"font-family:georgia;font-size:16\">Welcome to ModTrace!</a></u>\n"
	    + "ModTrace is an automated mod-tracking system that takes your changes "
	    + "from the osu! editor and puts them in mod format.\n\n"

	    + "<u><a style=\"font-family:georgia;font-size:16\">Usage</a></u>\n"
	    + "To use ModTrace, simply open a map in AIBat, then begin your mod. "
	    + "Whenever you save in the editor, "
	    + "the ModTrace tab will show all the changes you've made since it began.\n\n"

	    + "You can also use ModTrace to assist you with rechecks! "
	    + "When you open the map in AIBat for a recheck, if you overwrite none, "
	    + "ModTrace shows you what the mapper has changed since you last ran ModTrace.\n\n"

	    + "<u><a style=\"font-family:georgia;font-size:16\">Warning</a></u>\n"
	    + "ModTrace currently looks at hit objects and hit objects only. "
	    + "This means that metadata and timing changes WILL NOT be tracked. "
	    + "If you are going to make timing changes, do those first, "
	    + "then refresh AIBat and overwrite all before modding the hit objects.";

    public ModTraceTab(ModTrace mt) {
	super();
	this.mt = mt;
	// new Thread(this).start();
	thread = new Thread(this);
	thread.start();
	// run();
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

    @Override
    public void run() {
	while (true) {
	    fillAllContent();
	    String toShow = allContentToString(FORMAT_TO_HTML);
	    showText(toShow);
	    // invalidate();
	    // validate();
	    System.out.println("ModTrace Shown");
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
