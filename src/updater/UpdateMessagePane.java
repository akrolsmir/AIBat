package updater;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import aibat.Util;
import java.awt.SystemColor;

public class UpdateMessagePane extends JEditorPane {

    private static final String LINK_FORMAT = "<a href=\"%1$s\">%1$s</a>";

    public UpdateMessagePane(String latestVersion, String downloadLink) {

	// Format to do HTML
	Font font = UIManager.getFont("Label.font");
	String bodyRule = "body { font-family: " + font.getFamily() + "; "
		+ "font-size: " + font.getSize() + "pt; }";

	setEditorKit(new HTMLEditorKit());
	((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);

	setEditable(false);
	setBackground(new Color(0xf0f0f0));
	addHyperlinkListener(new OpenHyperlinkInBrowser());
	setText(("There is a newer version of AIBat available: "
		+ latestVersion
		+ "\nUpgrade to get new features, optimizations, and bugfixes!\n"
		+ "\nDirect download link: "
		+ String.format(LINK_FORMAT, downloadLink) + "\nForum thread: " + String
		.format(LINK_FORMAT, "http://osu.ppy.sh/forum/t/55305"))
		.replaceAll("\\n", "<br />"));
    }

    private class OpenHyperlinkInBrowser implements HyperlinkListener {

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
	    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
		Util.openHyperlinkInBrowser(e.getDescription());
	    }
	}
    }

}
