import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;


public final class Util
{
    //For optimization testing purposes. Time should have been System.currentTimeMilliseconds()
    public static void logTime(long time)
    {
        String methodName = Thread.currentThread().getStackTrace()[2].toString();//.getMethodName();
        System.out.println( System.currentTimeMillis() - time + " ms: "
            + methodName );
    }


    // Converts a file to a String with StringBuilder
    public static String readFile( File file )
    {
        StringBuilder result = new StringBuilder();
        try
        {
            BufferedReader f = new BufferedReader( new FileReader( file ) );
            try
            {
                char c;
                while ( f.ready() )
                {
                    c = (char)f.read();

                    result.append( c );
                }
            }
            finally
            {
                f.close();
            }
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return result.toString();
    }


    public static String readFile( String filename )
    {
        return readFile( new File( filename ) );
    }


    // Test for speed later
    // public static String readFileAsString( String filePath )
    // throws java.io.IOException
    // {
    // StringBuilder fileData = new StringBuilder( 1000 );
    // BufferedReader reader = new BufferedReader( new FileReader(filePath));
    // char[] buf = new char[1024];
    // int numRead = 0;
    // while ( ( numRead = reader.read( buf ) ) != -1 )
    // {
    // String readData = String.valueOf( buf, 0, numRead );
    // fileData.append( readData );
    // buf = new char[1024];
    // }
    // reader.close();
    // return fileData.toString();
    // }

    // resource reader?
    // private String readFileAsString(String filePath) throws
    // java.io.IOException {
    // StringBuffer fileData = new StringBuffer(1000);
    // BufferedReader reader = new BufferedReader(new InputStreamReader(this
    // .getClass().getClassLoader().getResourceAsStream(filePath)));
    // char[] buf = new char[1024];
    // int numRead = 0;
    // while ((numRead = reader.read(buf)) != -1) {
    // String readData = String.valueOf(buf, 0, numRead);
    // fileData.append(readData);
    // buf = new char[1024];
    // }
    // reader.close();
    // return fileData.toString();
    // }

    // Converts a file to a String, sped up?
    // public static String readFile( String filename )
    // {
    // String result = "";
    // try
    // {
    // BufferedReader f = new BufferedReader( new FileReader( filename ) );
    // try
    // {
    // while ( f.ready() )
    // {
    // result += f.readLine() + "\n";
    // }
    // }
    // finally
    // {
    // f.close();
    // }
    // }
    // catch ( FileNotFoundException e )
    // {
    // e.printStackTrace();
    // }
    // catch ( IOException e )
    // {
    // e.printStackTrace();
    // }
    // return result;
    // }

    // Formats int time to MM:SS:mmm
    public static String formatTime( int time )
    {
        boolean isNegative = false;
        if ( time < 0 )
        {
            time = -time;
            isNegative = true;
        }

        int min = 0, sec, ms;
        while ( time > 60000 )
        {
            time -= 60000;
            min++;
        }
        sec = time / 1000;
        ms = time % 1000;
        DecimalFormat two = new DecimalFormat( "00" );
        DecimalFormat three = new DecimalFormat( "000" );
        String result = two.format( min ) + ":" + two.format( sec ) + ":"
            + three.format( ms );
        if ( isNegative )
            result = "-" + result;
        return result;
    }


    // extracts the String following title, until \n, in String searchThrough
    public static String extract( String title, String searchThrough )
    {
        int start = searchThrough.indexOf( title );
        if ( start < 0 )
            return "";
        int end = searchThrough.indexOf( '\n', start + 1 );
        if ( start + title.length() == end ) // If the string is "" TODO
            return "";
        return searchThrough.substring( start + title.length(), end - 1 );
    }


    public static String extract( String start, String end, String searchThrough )
    {
        int s = searchThrough.indexOf( start );
        int e = searchThrough.indexOf( end );
        if ( s < 0 || e < 0 )
            return "";
        return searchThrough.substring( s + start.length(), e );
    }


    public static void copyStringToClipboard( String s )
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( s ), null );
    }


    public static ArrayList<File> getAllFiles( File dir )
    {
        ArrayList<File> allFiles = new ArrayList<File>();
        for ( File f : dir.listFiles() )
        {
            if ( f.isFile() )
                allFiles.add( f );
            else if ( f.isDirectory() )
                allFiles.addAll( getAllFiles( f ) );
        }
        return allFiles;
    }


    public static ArrayList<File> getFiles( File dir )
    {
        ArrayList<File> allFiles = new ArrayList<File>();
        for ( File f : dir.listFiles() )
        {
            if ( f.isFile() )
                allFiles.add( f );
            // recursive loop that finds all stuff- left out
            // else if ( f.isDirectory() )
            // allFiles.addAll( getAllFiles( f ) );
        }
        return allFiles;
    }


    public static String getExtension( File f )
    {
        return getExtension( f.getName() );
    }


    public static String getExtension( String s )
    {
        String ext = null;
        int i = s.lastIndexOf( '.' );

        if ( i > 0 && i < s.length() - 1 )
            ext = s.substring( i + 1 ).toLowerCase();

        if ( ext == null )
            return "";
        return ext;
    }


    public static String cutPath( String toCut )
    {
        // TODO replace AIBAT.cutpath
        return toCut.substring( toCut.lastIndexOf( "\\" ) + 1 );
    }


    // public static String cutPathAndExt( String toCut )
    // {
    // return cutPath( cutExt( toCut ) );
    // }

    public static String cutExt( String toCut )
    {
        if ( toCut.indexOf( '.' ) > 0 )
            return toCut.substring( 0, toCut.lastIndexOf( '.' ) );
        return toCut;
    }


    public static void openFileEditor( String fileName ) throws IOException
    {
        if ( Desktop.isDesktopSupported() )
        {
            Desktop.getDesktop().edit( new File( fileName ) );
        }

    }


    public static void openFolder( String fileName ) throws IOException
    {
        File file = new File( fileName );
        if ( Desktop.isDesktopSupported() && file.isDirectory() )
        {
            Desktop.getDesktop().open( file );
        }
    }


    // Opens a JFileChooser, which returns the selected directory or null.
    public static String chooseDirectory( String initDirectory, String type )
    {
        return chooseDirectory( type, type );
    }


    // Opens a JFileChooser, which returns the selected directory or null if
    // canceled or missed
    public static String chooseDirectory(
        String initDirectory,
        String title,
        String buttonText )
    {
        JFileChooser fileChooser = new JFileChooser( initDirectory );
        fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fileChooser.setDialogTitle( title );
        int result = fileChooser.showDialog( null, buttonText );
        if ( result == JFileChooser.CANCEL_OPTION )
            return null;

        File file = fileChooser.getSelectedFile();
        if ( file != null )
            return file.getAbsolutePath();
        return null;
    }


    // public static void getDurationWithMp3Spi( File file )
    // throws UnsupportedAudioFileException,
    // IOException
    // {
    // long start = System.currentTimeMillis();
    //
    // AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat( file );
    // AudioFormat format = fileFormat.getFormat();
    // if ( fileFormat instanceof TAudioFileFormat )
    // {
    // Map<?, ?> properties = ( (TAudioFileFormat)fileFormat ).properties();
    // String key = "duration";
    // Long microseconds = (Long)properties.get( key );
    // int mili = (int)( microseconds / 1000 );
    // System.out.println( formatTime( mili ) );
    //
    // System.out.println( format instanceof TAudioFormat );
    //
    // properties = ( (TAudioFormat)format ).properties();
    // System.out.println( (Integer)properties.get( "bitrate" ) / 1000
    // + " " + properties.get( "vbr" ) );
    // }
    // else
    // {
    // throw new UnsupportedAudioFileException();
    // }
    //
    // System.out.println( System.currentTimeMillis() - start );
    //
    // }

    public static int getBitrate( File file )
        throws UnsupportedAudioFileException,
        IOException
    {
        AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat( file );
        AudioFormat format = fileFormat.getFormat();
        if ( fileFormat instanceof TAudioFileFormat )
        {
            Map<?, ?> properties = ( (TAudioFormat)format ).properties();
            return (Integer)properties.get( "bitrate" ) / 1000;
        }
        else
        {
            throw new UnsupportedAudioFileException();
        }
    }


    public static void errorMessage( String text, AIBatWindow window )
    {
        JOptionPane.showMessageDialog( window,
            text,
            "Error",
            JOptionPane.ERROR_MESSAGE );
    }


    public static void errorMessage( String text )
    {
        errorMessage( text, null );
    }


    public static void errorSettings()
    {
        errorMessage( "The file settings.txt could not be found\n"
            + "(Make sure it is in the same folder as AIBat.jar)", null );
    }


    public static void errorException( Exception e )
    {
        errorException( e, "" );
    }


    public static void errorException( Exception e, String addText )
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        // Util.errorMessage(
        // "Please report this error along with the map that caused it:\n\n"
        // + sw.toString());

        JFrame frame = new JFrame( "Error" );
        JTextArea textArea = new JTextArea();
        // textArea.setEditable(false);
        textArea.setText( "Please report this error along with the map that caused it:\n\n"
            + addText + "\n" + sw.toString() );
        frame.getContentPane().add( textArea );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }


    // Test
    public static void main( String args[] )
        throws UnsupportedAudioFileException,
        IOException
    {
        String testFile = "C:\\Users\\Akrolsmir\\Desktop\\Gaming Programs\\osu!\\Songs\\32318 DEEN - Eien no Ashita\\101 Eternal Tomorrow (Tales of Hearts Version).mp3";
        System.out.println( Util.getBitrate( new File( testFile ) ) );
    }

}
