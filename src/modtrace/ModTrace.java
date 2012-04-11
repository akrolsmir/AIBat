package modtrace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import aibat.OsuFileParser;

public class ModTrace {

    public final static String VERSION = "ModTrace v0.1b";

    private List<File> osuFiles;
    private Map<File, OsuFileParser> parsedOrigFiles;

    public ModTrace(List<File> osuFiles) {
	this.osuFiles = osuFiles;

	if (!startModTrace())
	    return;

	OsuFilesCopier ofc = new OsuFilesCopier(osuFiles);
	parsedOrigFiles = ofc.getParsedOrigFiles();
    }

    // Asks if the user wants to start ModTrace
    private boolean startModTrace() {
	int n = JOptionPane.showConfirmDialog(null, "Would you like to start "
		+ VERSION + "?\n\n"
		+ "(ModTrace is an experimental function of AIBat that \n"
		+ "automatically tracks changes saved from the .osu editor.)",
		"Start ModTrace?", JOptionPane.YES_NO_OPTION);
	return n == 0;
    }

    private void compareAll() {
	for (Map.Entry<File, OsuFileParser> entry : parsedOrigFiles.entrySet()) {
	    DiffComparator d = new DiffComparator(new OsuFileParser(
		    entry.getKey()), entry.getValue());
	}
    }

    // public static void main(String[] args) {
    // System.out.println(new ModTrace(null).beginModTrace());
    // }
}
