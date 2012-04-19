package updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JFrame;

import aibat.AIBatWindow;
import aibat.Util;

public class Updater implements Runnable {

    //private final static String LATEST_VERSION_INFO_LOC = "https://raw.github.com/akrolsmir/AIBat/updater/src/updater/LatestVersionInfo";
    private final static String LATEST_VERSION_INFO_LOC = "https://raw.github.com/akrolsmir/AIBat/master/src/updater/LatestVersionInfo";
    private String latestVersion, downloadLink;
    
    public Updater() {
	getInfo(LATEST_VERSION_INFO_LOC);
	if (latestVersion == null)
	    return;
	if (!AIBatWindow.version.equals(latestVersion)) {

	    JFrame frame = new JFrame("Update Available");
	    frame.add(new UpdateMessagePane(latestVersion, downloadLink));
	    
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
    }

    private void getInfo(String address) {
	try {
	    URL url = new URL(address);
	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    url.openStream()));
	    latestVersion = in.readLine();
	    downloadLink = in.readLine();
	    in.close();
	}
	catch (Exception e) {
	    //Util.errorMessage("Update checker failed.");
	    Util.errorException(e, "Update checker failed.");
	    latestVersion = downloadLink = null;
	}

    }

//    public static void main(String[] args) throws Exception {
//	// Scanner scanIn = new Scanner(System.in);
//	// String scanned = scanIn.nextLine();
//	// scanIn.close();
//	// System.out.println(getFirstLine(NEW_DOWNLOAD_LINK_LOC));
//	//long start = System.currentTimeMillis();
//	//new Updater();
//	//Util.logTime(start);
//    }

    @Override
    public void run() {
	long start = System.currentTimeMillis();
	new Updater();
	Util.logTime(start);
	
    }

}
