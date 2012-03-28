package tabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



public abstract class AbstractTab extends JPanel
{
    protected String[] TITLES;
    
    private static final String TIME_REGEX = "(\\d{2}:\\d{2}:\\d{3}(?: \\(\\d+\\))?(?: - )?)";

    private static final String LINK_REGEX = "<A Href=\"$1\">$1</A>";
    
    private JEditorPane textArea;

    /**
     * Create the panel.
     */
    public AbstractTab()
    {
        setBackground( Color.LIGHT_GRAY );

        textArea = new JEditorPane();
        textArea.setEditorKit( new HTMLEditorKit() );
        textArea.setForeground( Color.BLACK );

        Font font = UIManager.getFont( "Label.font" );
        String bodyRule = "body { font-family: " + font.getFamily() + "; "
            + "font-size: " + font.getSize() + "pt; }";
        ( (HTMLDocument)textArea.getDocument() ).getStyleSheet()
            .addRule( bodyRule );

        textArea.setEditable( false );
        //textArea.addHyperlinkListener( this );

        JScrollPane scrollPane = new JScrollPane( textArea );
        scrollPane.setPreferredSize( new Dimension( 600, 500 ) );
    }
    
    public void showText(String toShow)
    {

        toShow = toShow.replaceAll( TIME_REGEX, LINK_REGEX );
        toShow = toShow.replaceAll( "\\n", "<br />" );
        textArea.setText( toShow );
        
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

}
