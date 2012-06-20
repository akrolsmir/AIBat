package tabs;

import java.util.TreeMap;

import modtrace.ModTrace;

public class ModTraceTab extends ContentTab implements Runnable {

    private static final long REFRESH_INTERVAL = 3000;
    private ModTrace mt;
    private Thread thread;

    public ModTraceTab(ModTrace mt) {
	super();
	this.mt = mt;
	//new Thread(this).start();
	thread = new Thread(this);
	thread.start();
	// run();
    }

    @Override
    public String getTabName() {
	return "ModTrace";
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
//	    invalidate();
//	    validate();
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
