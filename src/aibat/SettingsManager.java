package aibat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class SettingsManager
{
    private final String SETTINGS_TXT_LOC = System.getProperty( "user.dir" )
        + "\\settings.txt", INDICATOR1 = "osu! Songs folder:",
                    INDICATOR2 = "Bookmarks:";

    // "" is unassigned.
    private String songFolderLoc = "";

    private ArrayList<String> bookmarks;

    private final static String NL = System.getProperty( "line.separator" );


    public SettingsManager() throws FileNotFoundException
    {
        bookmarks = new ArrayList<String>();
        try
        {
            BufferedReader f = new BufferedReader( new FileReader( SETTINGS_TXT_LOC ) );
            try
            {
                // Skips line 1 and 3, reads 2 into songFolderLoc and everything
                // else into bookmarks
                f.readLine();
                songFolderLoc = f.readLine();
                f.readLine();
                while ( f.ready() )
                {
                    String line = f.readLine();
                    if ( line.length() > 0 )
                        bookmarks.add( line );
                }
            }
            finally
            {
                f.close();
            }
        }
        catch ( FileNotFoundException e )
        {
            // e.printStackTrace();
            throw e;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
//Testing Properties.
//    public SettingsManager() throws FileNotFoundException
//    {
//        bookmarks = new ArrayList<String>();
//        try
//        {
//            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("hi.txt")));
//            Properties p = new Properties();
//            p.load( new FileReader( "Settings.txt" ) );
//            //p.setProperty( "C", "C:\\Users\\Akrolsmir\\Desktop\\Gaming Programs\\osu!\\Songs" );
//            String bm = p.getProperty( "C" );
//            p.list( out );
//            out.close();
//            //p.save( , "arg1" )
//            System.out.println( "*" + bm );
//        }
//        catch ( Exception ex )
//        {
//            ex.printStackTrace();
//        }
//    }


    private void saveToTxt()
    {
        try
        {
            PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( SETTINGS_TXT_LOC ) ) );
            out.print( toString() );
            out.close();

        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    public void addBookmark( String newMark )
    {
        if ( !bookmarks.contains( newMark ) )
        {
            bookmarks.add( newMark );
            saveToTxt();
            JOptionPane.showMessageDialog( null, "Successfully bookmarked "
                + newMark + ".", "Bookmarked", JOptionPane.INFORMATION_MESSAGE );
        }
    }


    // True if successfully removed
    public boolean removeBookmark( String removeMark )
    {
        int loc = bookmarks.indexOf( removeMark );
        if ( loc < 0 )
            return false;
        bookmarks.remove( loc );
        saveToTxt();
        return true;
    }


    // True if successfully removed
    public boolean removeBookmark( int markLoc )
    {
        if ( markLoc < 0 || markLoc >= bookmarks.size() )
            return false;
        bookmarks.remove( markLoc );
        saveToTxt();
        return true;
    }


    public ArrayList<String> getBookmarks()
    {
        return bookmarks;
    }


    public String getSongFolderLoc()
    {
        if ( songFolderLoc == null )
            return "";
        return songFolderLoc;
    }


    public void setSongFolderLoc( String set )
    {
        songFolderLoc = set;
        saveToTxt();
    }


    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append( INDICATOR1 + NL );
        result.append( getSongFolderLoc() + NL );
        result.append( INDICATOR2 + NL );
        if ( bookmarks.size() == 0 )
            result.append( NL );
        else
            for ( String bookmark : bookmarks )
                result.append( bookmark + NL );
        return result.toString();
    }


    public static void main( String[] args )
    {
        SettingsManager k = null;
        try
        {
            k = new SettingsManager();
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
    }
    // // System.out.println( k.songFolderLoc + "\n--" );
    // // for ( String s : k.bookmarks )
    // // {
    // // System.out.println( s );
    // // }
    // System.out.print( k );
    // // k.setSongFolderLoc( "it's Here now" );
    // k.addBookmark( "hi" );
    // k.addBookmark( "hi2" );
    // k.removeBookmark( "hi2" );
    // k.removeBookmark( "nope" );
    // System.out.print( k );
    // }
}