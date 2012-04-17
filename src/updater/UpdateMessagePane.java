package updater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import aibat.Util;

public class UpdateMessagePane extends JEditorPane {

    private static final String LINK_FORMAT = "<a href=\"%1$s\">%1$s</a>";
    private static final String NOTIFICATION = "There is a newer version of AIBat available.\n"
	    + "Upgrade to get new features, optimizations, and bugfixes!";

    public UpdateMessagePane(String newDownloadLink) {
	
	Font font = UIManager.getFont("Label.font");
	String bodyRule = "body { font-family: " + font.getFamily() + "; "
		+ "font-size: " + font.getSize() + "pt; }";
	// Format to do HTML
	setEditorKit(new HTMLEditorKit());
	setForeground(Color.BLACK);
	((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);

	setEditable(false);
	setBackground(Color.LIGHT_GRAY);
	addHyperlinkListener(new OpenHyperlinkInBrowser());
	setText((NOTIFICATION + "\nDirect download link: "
		+ String.format(LINK_FORMAT, newDownloadLink)
		+ "\nForum thread: " + String.format(LINK_FORMAT,
		"http://osu.ppy.sh/forum/t/55305")).replaceAll("\\n", "<br />"));
    }
    
    private class OpenHyperlinkInBrowser implements HyperlinkListener
    {

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
	    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
	    {
		String url = e.getDescription();
		try {
		    java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		catch (Exception e1) {
		    Util.errorException(e1);
		}
	    }
	}
    }

}
