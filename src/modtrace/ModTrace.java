package modtrace;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import tabs.AIBatTab;
import tabs.AIBatTabbedPane;
import tabs.ModTraceTab;

import aibat.OsuFileParser;

public class ModTrace implements Runnable {

    public final static String VERSION = "ModTrace v0.1b";
    public ModTraceTab modTraceTab;

    // Maps the original file, parsed, to the corresponding new version
    private Map<OsuFileParser, File> parsedOrigFiles;

    public ModTrace(List<File> osuFiles, AIBatTabbedPane tabs) {
	// this.osuFiles = osuFiles;

	if (!askToStart())
	    return;

	OsuFilesCopier ofc = new OsuFilesCopier(osuFiles);
	parsedOrigFiles = ofc.getParsedOrigFiles();

	modTraceTab = new ModTraceTab(this);
	tabs.add(modTraceTab.getTabName(), modTraceTab);
	tabs.focusLast();
    }

    // Asks if the user wants to start ModTrace
    private boolean askToStart() {
	int n = JOptionPane.showConfirmDialog(null, "Would you like to start "
		+ VERSION + "?\n\n"
		+ "(ModTrace is an experimental function of AIBat that \n"
		+ "automatically tracks changes saved from the .osu editor.)",
		"Start ModTrace?", JOptionPane.YES_NO_OPTION);
	return n == 0;
    }

    // Runs a DiffComparator for each pair and maps titles to changes
    public Map<String, String> compareAll() {
	TreeMap<String, String> result = new TreeMap<String, String>();
	for (Map.Entry<OsuFileParser, File> entry : parsedOrigFiles.entrySet()) {
	    DiffComparator d = new DiffComparator(entry.getKey(),
		    new OsuFileParser(entry.getValue()));
	    result.put(entry.getKey().getDiff(), d.compare());
	}
	return result;
    }

    @Override
    public void run() {
	// TODO Should something be here?
	// Oh yeah, probably update.
	// Hacky solution: check every 10 seconds if interrupted
	while (true) {
	    try {
		Thread.sleep(10000);
		System.out.println("Sleeping again for 10 sec");// TODO remove
	    }
	    catch (InterruptedException e) {
		e.printStackTrace();
		modTraceTab.stop();
		return;
	    }

	}
    }

    // public static void main(String[] args) {
    // }

}
