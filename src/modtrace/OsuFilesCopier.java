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
    private Map<File, OsuFileParser> parsedOrigFiles = new TreeMap<File, OsuFileParser>();

    // Preference about overwriting;
    private enum OP {
	NO_PREFERENCE, ALL, NONE
    }

    private OP overwritePreference = OP.NO_PREFERENCE;

    // Copies each osu file to the local folder
    public OsuFilesCopier(List<File> osuFiles) {
	this.osuFiles = osuFiles;
	for (File srcFile : this.osuFiles) {

	    File destFile = new File(ORIG_SUBDIR + srcFile.getName() + ".orig");
	    parsedOrigFiles.put(srcFile, new OsuFileParser(destFile));

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
				    "File \""
					    + destFile
					    + "\" already exists. Overwrite?\n\n"
					    + "(Overwrite only if you want to use the the current .osu file as a new starting reference for mod tracking.)",
				    "File Already Exists",
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

    public Map<File, OsuFileParser> getParsedOrigFiles() {
	return parsedOrigFiles;
    }
}
