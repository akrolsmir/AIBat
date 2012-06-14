package modtrace;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import aibat.OsuFileParser;

public class OsuFilesCopier {
    public final static String ORIG_SUBDIR = "origFiles\\";

    private List<File> osuFiles;
    private Map<OsuFileParser, File> parsedOrigFiles = new TreeMap<OsuFileParser, File>();

    // Preference about overwriting;
    private enum OP {
	NO_PREFERENCE, ALL, NONE
    }

    private OP overwritePreference = OP.NO_PREFERENCE;

    // Copies each osu file to the a .osu.orig in ORIG_SUBDIR
    public OsuFilesCopier(List<File> osuFiles) {
	this.osuFiles = osuFiles;
	for (File srcFile : this.osuFiles) {

	    File destFile = new File(ORIG_SUBDIR + srcFile.getName() + ".orig");

	    overwritePreference: switch (overwritePreference) {
	    case NONE:
		break overwritePreference;
	    case NO_PREFERENCE:
		// If All or Yes, continue. If No, None or closed, finish.
		// Also, save All/None preference for the rest.
		if (destFile.exists()) {
		    Object[] options = { "All", "Yes", "No", "None" };
		    switch (JOptionPane
			    .showOptionDialog(
				    null,
				    "A reference snapshot for this difficulty already exists. Would you like to overwrite it with a new one?\n\n"
					    + "(ModTrace compares your edited .osu file against the reference snapshot.\n"
					    + "In general, only overwrite the snapshot if you haven't begun your mod, but are about to start.)",
				    "File already exists: \"" + destFile + "\"",
				    JOptionPane.PLAIN_MESSAGE,
				    JOptionPane.QUESTION_MESSAGE, null,
				    options, options[2])) {
		    case 0:
			overwritePreference = OP.ALL;
		    case 1:
			break;
		    case 3:
			overwritePreference = OP.NONE;
		    case 2:
		    default: // if window closed
			break overwritePreference;

		    }
		}
	    case ALL:
		writeOrigFile(srcFile, destFile);
		break;
	    }

	    parsedOrigFiles.put(new OsuFileParser(destFile), srcFile);
	}
    }

    private void writeOrigFile(File srcFile, File destFile) {
	try {
	    FileUtils.copyFile(srcFile, destFile);
	    System.out.println("Wrote: " + destFile);// TODO remove
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public Map<OsuFileParser, File> getParsedOrigFiles() {
	return parsedOrigFiles;
    }
}
