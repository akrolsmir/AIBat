package modtrace;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class OsuFilesCopier {
    private List<File> osuFiles;

    // Preference about overwriting;
    private enum OP {
	NO_PREFERENCE, ALWAYS, NEVER
    }

    private OP overwritePreference = OP.NO_PREFERENCE;

    //Copies each osu file to the local folder
    public OsuFilesCopier(List<File> osuFiles) {
	this.osuFiles = osuFiles;
	for (File srcFile : this.osuFiles) {

	    File destFile = new File(srcFile.getName() + ".orig");
	    overwritePreference: switch (overwritePreference) {
	    case NEVER:
		break overwritePreference;
	    case NO_PREFERENCE:
		// If Always or Yes, continue. If No or Never, finish.
		// Also, save Always/Never preference for the rest.
		if (destFile.exists()) {
		    Object[] options = { "All", "Yes", "No", "None" };//TODO templating
		    switch (JOptionPane.showOptionDialog(null, "File \"" + destFile + "\" already exists. Overwrite?\n"
			    + "(Mod changes are compared against the .orig file.)",
			    "File Already Exists",
			    JOptionPane.PLAIN_MESSAGE,
			    JOptionPane.QUESTION_MESSAGE, null, options,
			    options[1])) {
		    case 0:
			overwritePreference = OP.ALWAYS;
		    case 1:
			break;
		    case 3:
			overwritePreference = OP.NEVER;
		    case 2:
			break overwritePreference;
		    }
		}
	    case ALWAYS:
		writeOrigFile(srcFile, destFile);
		break;
	    }
	}
    }

    private void writeOrigFile(File srcFile, File destFile) {
	try {
	    FileUtils.copyFile(srcFile, destFile);
	    System.out.println("Wrote: " + destFile);//TODO remove
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
