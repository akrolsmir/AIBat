package tabs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import aibat.Util;

public abstract class AIBatTab extends JPanel {
    // allContent maps section titles to their content
    protected Map<String, String> allContent = new TreeMap<String, String>();

    private static final String TIME_REGEX = "(\\d{2}:\\d{2}:\\d{3}(?: \\(\\d+\\))?)";//(?: - )?
    private static final String LINK_REGEX = "<a href=\"$1\">$1</a>";
    protected static final String FORMAT_TO_HTML = "<b><a href=\"%2$s\" style=\"font-family:georgia;font-size:16\">%1$s</a></b><br />%3$s<br />";

    private JEditorPane textArea = new JEditorPane();

    public AIBatTab() {
	drawGUI();
	// fillAllContent();
	// showText(allContentToString(FORMAT_TO_HTML));
    }

    private void drawGUI() {
	setBackground(Color.LIGHT_GRAY);

	Font font = UIManager.getFont("Label.font");
	String bodyRule = "body { font-family: " + font.getFamily() + "; "
		+ "font-size: " + font.getSize() + "pt; }";
	// Format textArea to do HTML
	textArea.setEditorKit(new HTMLEditorKit());
	textArea.setForeground(Color.BLACK);
	((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule(
		bodyRule);

	textArea.setEditable(false);
	// TODO fix so it's not *always* TEXT_CURSOR
	//textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
	textArea.addHyperlinkListener(new HyperlinkCopier());
	textArea.setText("Content Pane");
	JScrollPane scrollPane = new JScrollPane(textArea);

	// Replace with cool code
	scrollPane.setPreferredSize(new Dimension(600, 500));
	this.add(scrollPane);

    }

    public void showText(String toShow) {
	if (toShow == null || toShow.length() == 0) {
	    textArea.setText("Nothing to see!");
	    return;
	}
	textArea.setText(toShow);

	// TODO get scrolling to work properly
	// textArea.scrollRectToVisible( new Rectangle( 100, 100, 100, 100 ) );

	// try
	// {
	// Rectangle r = textArea.modelToView( 10 );
	// System.out.println(r==null);
	// textArea.scrollRectToVisible( r );
	// }
	// catch ( BadLocationException e )
	// {
	// e.printStackTrace();
	// }
    }

    private class HyperlinkCopier implements HyperlinkListener {
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
	    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
		Util.copyStringToClipboard(e.getDescription().replaceAll(
			"<br />", "\n"));
		// TODO Add hover reminder
	    }
	}
    }

    protected abstract void fillAllContent();

    protected String allContentToString(String format) {
	StringBuffer result = new StringBuffer();
	for (Map.Entry<String, String> entry : allContent.entrySet()) {

	    String title = entry.getKey();
	    String content = entry.getValue();

	    if (content == null || content.length() == 0)
		continue;
	    // This messy part displays an html version and but links to BBCode
	    String HTML_Content = content.replaceAll(TIME_REGEX, LINK_REGEX)
		    .replaceAll("\\n", "<br />") + "<br />";
	    content = "[b][u]" + title + "[/u][/b]\n" + content;
	    result.append(String.format(format, title, content, HTML_Content));

	}
	return result.toString();
    }

    // public static void main(String[] args) {
    // System.out.println(String.format(FORMAT_TO_HTML, "1d", "2d", "HTML"));
    // }

}
