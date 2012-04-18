package aibat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


public class AIBatMenu extends JMenuBar
{
    private JMenuItem openItem, exitItem, helpItem, aboutItem, expClipItem,
                    exploreItem, expHitsoundItem, changelogItem, searchItem,
                    settingsTextItem, refreshItem,

                    songFolderItem, addBmItem, removeBmItem, bmThisItem;

    private JMenu bookmarkMenu;// TODO remove

    private AIBatWindow window;

    private SettingsManager sm;

    private final static String HELP = "This is a program to check for problems with your beatmaps.\n"
        + "\n"
        + "1. Open your beatmap folder from File > Open Beatmap Folder. (shorcut: Ctrl+O)\n"
        + "2. Alternatively, browse to the file and click on/hit \"Enter\".\n"
        + "3. AIBat analyzes the contents of that file.\n"
        + "4. You can copy all warnings to your clipboard by going to File > Export...\n"
        + "    This will nicely format them for the osu! forums. (shortcut: Ctrl+Shift+C)"
        + "\n"
        + "Enjoy!\n"
        + "\n"
        + "Note: Still incomplete, see changelog.\n"
        + "If it doesn't work, chances are there's a decimal somewhere there shouldn't be.";

    private final static String ABOUT = AIBatWindow.version
        + "\n\n"
        + "Written by akrolsmir in Java.\n"
        + "Please post or PM suggestions, comments, and problems.\n\n"
        + "Kudos to:\nziin\nlolcubes\nSakura Hana\nBlissfulYoshi\nAyeen\nZhanger\nBreeze\nMiya\nCharles445\nAleks719\n\n"
        + "for their input and help.";


    public AIBatMenu( AIBatWindow w )
    {
        window = w;

        // FILE MENU
        FileAction fileAction = new FileAction();
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( 'F' );

        openItem = new JMenuItem( "Open Beatmap Folder", 'O' );
        openItem.addActionListener( fileAction );
        openItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O,
            ActionEvent.CTRL_MASK ) );
        exploreItem = new JMenuItem( "Explore Selected Folder", 'E' );
        exploreItem.addActionListener( fileAction );
        exploreItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_E,
            ActionEvent.CTRL_MASK ) );
        refreshItem = new JMenuItem( "Refresh Selected Folder", 'R' );
        refreshItem.addActionListener( fileAction );
        refreshItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) );
        expClipItem = new JMenuItem( "Export All Warnings To Clipboard",
            'C' );
        expClipItem.addActionListener( fileAction );
        expClipItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C,
            ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK ) );
        expHitsoundItem = new JMenuItem( "Export Hitsounds to Bookmarks", 'H' );
        expHitsoundItem.addActionListener( fileAction );
        exitItem = new JMenuItem( "Exit", 'x' );
        exitItem.addActionListener( fileAction );

        // JMenu exportMenu = new JMenu( "Export to..." );
        // exportMenu.setMnemonic( 'p' );
        // exportMenu.add( expClipItem );

        fileMenu.add( openItem );
        fileMenu.add( exploreItem );
        fileMenu.add( refreshItem );
        // fileMenu.add( songFolderItem );
        // fileMenu.add( exportMenu );
        fileMenu.addSeparator();
        fileMenu.add( expClipItem );
        fileMenu.add( expHitsoundItem );
        fileMenu.addSeparator();
        fileMenu.add( exitItem );

        // BOOKMARK MENU TODO make seperate?
        BookmarkAction bookmarkAction = new BookmarkAction();
        bookmarkMenu = new JMenu( "Bookmarks" );
        bookmarkMenu.setMnemonic( 'B' );

        addBmItem = new JMenuItem( "Add Bookmark", 'A' );
        addBmItem.addActionListener( bookmarkAction );

        removeBmItem = new JMenuItem( "Remove Bookmark", 'R' );
        removeBmItem.addActionListener( bookmarkAction );
        bmThisItem = new JMenuItem( "Bookmark This Folder", 'T' );
        bmThisItem.addActionListener( bookmarkAction );
        bmThisItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_B,
            ActionEvent.CTRL_MASK ) );

        bookmarkMenu.add( refreshAllBm( bookmarkAction ) );
        bookmarkMenu.addSeparator();
        bookmarkMenu.add( addBmItem );
        bookmarkMenu.add( removeBmItem );
        bookmarkMenu.add( bmThisItem );

        // SEARCH MENU
        SearchAction searchAction = new SearchAction();
        JMenu searchMenu = new JMenu( "Search" );
        searchMenu.setMnemonic( 'S' );
        searchItem = new JMenuItem( "Search osu! Songs Folder", 'S' );
        searchItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F,
            ActionEvent.CTRL_MASK ) );
        searchItem.addActionListener( searchAction );
        songFolderItem = new JMenuItem( "Select osu! Songs Folder Location" );
        songFolderItem.addActionListener( searchAction );
        searchMenu.add( searchItem );
        searchMenu.add( songFolderItem );

        // SETTINGS MENU
        SettingsAction settingsAction = new SettingsAction();
        JMenu settingsMenu = new JMenu( "Settings" );
        settingsMenu.setMnemonic( 't' );
        settingsTextItem = new JMenuItem( "Show settings.txt", 's' );
        settingsTextItem.addActionListener( settingsAction );
        settingsMenu.add( settingsTextItem );

        // HELP MENU
        HelpAction helpAction = new HelpAction();
        JMenu helpMenu = new JMenu( "Help" );
        helpMenu.setMnemonic( 'H' );

        helpItem = new JMenuItem( "AIBat Help", 'H' );
        helpItem.addActionListener( helpAction );
        helpItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H,
            ActionEvent.CTRL_MASK ) );
        changelogItem = new JMenuItem( "Show README.txt", 'c' );
        changelogItem.addActionListener( helpAction );
        changelogItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_L,
            ActionEvent.CTRL_MASK ) );
        aboutItem = new JMenuItem( "About AIBat", 'A' );
        aboutItem.addActionListener( helpAction );
        helpMenu.add( helpItem );
        helpMenu.add( changelogItem );
        helpMenu.addSeparator();
        helpMenu.add( aboutItem );

        // Adds the menus to the tab
        add( fileMenu );
        add( bookmarkMenu );
        add( searchMenu );
        add( settingsMenu );
        add( helpMenu );
    }


    private class FileAction implements ActionListener
    {
        // private String pathName = System.getProperty( "user.dir" ) + "/";

        @Override
        public void actionPerformed( ActionEvent e )
        {
            JMenuItem m = (JMenuItem)e.getSource();
            if ( m == openItem )
            {
                window.openFolder();
            }
            else if ( m == expClipItem )
            {
                window.copyAllWarningsToClipboard();
            }
            else if ( m == expHitsoundItem )
            {
                window.exportHitsounds();
            }
            else if ( m == exitItem )
            {
                System.exit( 0 );
            }
            else if ( m == refreshItem )
            {
                window.refreshCurrentFolder();
            }
            else if ( m == exploreItem )
            {
                window.explore();
            }
        }

    }


    private class HelpAction implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            String cmd = ( (AbstractButton)e.getSource() ).getActionCommand();
            if ( cmd.equals( "AIBat Help" ) )
            {
                JOptionPane.showMessageDialog( window,
                    HELP,
                    "Instructions",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else if ( cmd.equals( "About AIBat" ) )
            {
                JOptionPane.showMessageDialog( window,
                    ABOUT,
                    "About",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            // TODO change to something == changeItem
            else if ( cmd.equals( "Show README.txt" ) )
            {
                try
                {
                    Util.openFileEditor( "README.txt" );
                }
                catch ( IOException e1 )
                {
                    Util.errorMessage( "The file README.txt could not be found\n"
                        + "(Make sure it is in the same folder as AIBat.jar)",
                        window );
                }
                // JOptionPane.showMessageDialog( window,
                // "Not yet implemented. See changelog for details.",
                // "Error",
                // JOptionPane.ERROR_MESSAGE );
            }

        }

    }


    private class SearchAction implements ActionListener
    {
        private void selectFolder()
        {
            String folder = window.chooseDirectory( "Select osu! Songs Folder",
                "Select" );
            if ( folder != null )
                sm.setSongFolderLoc( folder );
        }


        public void actionPerformed( ActionEvent e )
        {
            if ( sm == null )
            {
                Util.errorSettings();
                return;
            }
            JMenuItem m = (JMenuItem)e.getSource();
            if ( m == searchItem )
            {
                // New dialogue TODO
                // new Searcher2( window );
                window.search();
            }
            else if ( m == songFolderItem )
            {
                selectFolder();
            }
        }
    }


    private class BookmarkAction implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            if ( sm == null )
            {
                Util.errorSettings();
                return;
            }
            String cmd = ( (AbstractButton)e.getSource() ).getActionCommand();
            JMenuItem m = (JMenuItem)e.getSource();
            if ( m == addBmItem )
            {
                String folder = window.chooseDirectory( "Select Folder to Bookmark",
                    "Bookmark This Folder" );
                // null if canceled
                if ( folder != null )
                {
                    sm.addBookmark( folder );
                    bookmarkMenu.remove( 0 );
                    bookmarkMenu.add( refreshAllBm( this ), 0 );
                }
            }
            else if ( m == removeBmItem )
            {
                Util.errorMessage( "Not yet implemented.\n"
                    + "(You can manually delete entries in settings.txt for now)",
                    window );

            }
            else if ( m == bmThisItem )
            {
                if ( window.isFileOpened() )
                {
                    // TODO remove duplicate code
                    sm.addBookmark( window.getDirectory() );
                    bookmarkMenu.remove( 0 );
                    bookmarkMenu.add( refreshAllBm( this ), 0 );
                }
                else
                    Util.errorMessage( "No folder is currently opened.", window );
            }
            else
            {
                window.switchTo( cmd );
            }
        }
    }


    private class SettingsAction implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            JMenuItem m = (JMenuItem)e.getSource();
            if ( m == settingsTextItem )
                try
                {
                    Util.openFileEditor( "settings.txt" );
                }
                catch ( IOException e1 )
                {
                    Util.errorSettings();
                }

        }

    }


    private JMenu refreshAllBm( BookmarkAction bookmarkAction )
    {
        JMenu allBm = new JMenu( "Open Bookmark..." );
        allBm.setMnemonic( 'O' );

        try
        {
            sm = new SettingsManager();
            for ( String bookmark : sm.getBookmarks() )
            {
                JMenuItem bm = new JMenuItem( bookmark );
                allBm.add( bm );
                bm.addActionListener( bookmarkAction );
            }
        }
        catch ( FileNotFoundException e )
        {
            // e.printStackTrace();
            // System.out.println( "HeI" );
        }
        return allBm;
    }
}
