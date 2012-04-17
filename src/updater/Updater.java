package updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JFrame;

import aibat.Util;

public class Updater {

    private final static String NEW_DOWNLOAD_LINK_LOC = "https://raw.github.com/akrolsmir/AIBat/updater/downloadlink";
    // private final static String NEW_DOWNLOAD_LINK_LOC =
    // "https://raw.github.com/akrolsmir/AIBat/master/src/aibat/AIBatWindow.java";
    private final static String OLD_DOWNLOAD_LINK = "http://puu.sh/mK97";

    public Updater() {
	String newDownloadLink = getFirstLine(NEW_DOWNLOAD_LINK_LOC);
	if (!OLD_DOWNLOAD_LINK.equals(newDownloadLink)) {

	    JFrame frame = new JFrame("Update Available");
	    frame.getContentPane().add(new UpdateMessagePane(newDownloadLink));
	    frame.setLocationRelativeTo(null);
	    frame.pack();
	    frame.setVisible(true);
	}
    }

    public static String getFirstLine(String address) {
	try {
	    URL url = new URL(address);
	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    url.openStream()));
	    String result = in.readLine();
	    in.close();
	    return result;
	}
	catch (Exception e) {
	    Util.errorException(e);
	    return "";
	}

    }

    public static void main(String[] args) throws Exception {
	// Scanner scanIn = new Scanner(System.in);
	// String scanned = scanIn.nextLine();
	// scanIn.close();
	System.out.println(getFirstLine(NEW_DOWNLOAD_LINK_LOC));
	new Updater();
    }

}
