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
    public final static String ORIG_SUBDIR = "origFiles\\";// TODO private?

    private final static String OVERWRITE_MSG = "You've run ModTrace on this map before. What would you like to do?\n\n"
	    + "Continue from previous if:\n"
	    + " - You're returning to an unfinished mod.\n"
	    + " - You're viewing the mapper's changes after an update.\n"
	    + "Start over if:\n"
	    + " - You've just made timing changes and resnapped all notes.\n"
	    + " - You're beginning a new mod after an update.";

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

	    if (!destFile.exists()) {
		writeOrigFile(srcFile, destFile);
	    }
	    else {
		overwritePreference: switch (overwritePreference) {
		case NONE:
		    break overwritePreference;
		case NO_PREFERENCE:
		    // // If All or Yes, continue. If No, None or closed,
		    // finish.
		    // // Also, save All/None preference for the rest.
		    // Object[] options = { "All", "Yes", "No", "None" };
		    // switch (JOptionPane.showOptionDialog(null, OVERWRITE_MSG,
		    // "File already exists: \"" + destFile + "\"",
		    // JOptionPane.PLAIN_MESSAGE,
		    // JOptionPane.QUESTION_MESSAGE, null, options,
		    // options[2])) {
		    // case 0:
		    // overwritePreference = OP.ALL;
		    // case 1:
		    // break;
		    // case 3:
		    // overwritePreference = OP.NONE;
		    // case 2:
		    // default: // if window closed
		    // break overwritePreference;
		    // }
		    // case ALL:
		    // writeOrigFile(srcFile, destFile);
		    // break;
		    // }

		    Object[] options = { "Start Over", "Continue From Previous" };
		    switch (JOptionPane.showOptionDialog(null, OVERWRITE_MSG,
			    "ModTrace Previously Ran",
			    JOptionPane.PLAIN_MESSAGE,
			    JOptionPane.QUESTION_MESSAGE, null, options,
			    options[1])) {
		    case 0:
			overwritePreference = OP.ALL;
			break;
		    case 1:
		    default: // if window closed
			overwritePreference = OP.NONE;
			break overwritePreference;
		    }
		case ALL:
		    writeOrigFile(srcFile, destFile);
		    break;
		}
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
