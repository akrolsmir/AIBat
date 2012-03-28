import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import obj.Circle;
import obj.HitObject;
import obj.Slider;
import obj.Spinner;


public class OsuFileParser
{
    private String contents;

    // Array of info from General and Metadata
    private String[] osuInfo;

    // ArrayList of all hitobjects in the map
    private ArrayList<HitObject> hitObjects;

    private ArrayList<Integer> breakStarts, breakEnds;

    private HashMap<HitObject, String> notations;

    private int startTime, endTime;

    private Timing timer;

    private OsuFileChecker ofc;

    //@formatter:off
    public final static String[] TITLES = 
    {
        "AudioFilename: ", //index = 0
        "AudioLeadIn: ", //1
        "PreviewTime: ", //2
        "Countdown: ", //3
        "SampleSet: ", //4
        "StackLeniency: ", //5
        "Mode: ", //6
        "LetterboxInBreaks: ", //7
        "SkinPreference: ", //8
        "EpilepsyWarning: ", //9
        
        "Title:", //10
        "Artist:", //11
        "Creator:", //12
        "Version:", //13
        "Source:", //14
        "Tags:", //15
        
        "BeatDivisor: ", //16
    };
    //@formatter:on

    public final static int PREVIEW_LOC = 2, SAMPLESET_LOC = 4, STACK_LOC = 5,
                    MODE_LOC = 6, EP_LOC = 9, DIFF_LOC = 13, TAGS_LOC = 15,
                    BEATDIV_LOC = 16;


    public OsuFileParser( String fileName )
    {
        contents = Util.readFile( fileName );
        int t = 0;
        osuInfo = new String[TITLES.length];
        for ( int i = 0; i < TITLES.length; i++, t++ )
        {
            osuInfo[i] = Util.extract( TITLES[t], contents );
        }
        timer = new Timing( this );
        try
        {
            processHitObjects();
            processNotation();
            processBreaks();
            ofc = new OsuFileChecker( this );
        }
        catch ( IndexOutOfBoundsException e )
        {
            Util.errorMessage( "Found no hitobjects in:\n" + fileName
                + "\nPlace at least 1 hitobject to analyze the file." );
        }
    }


    // Creates hitObjects.
    private void processHitObjects()
    {
        ArrayList<HitObject> result = new ArrayList<HitObject>();
        Scanner sc = new Scanner( getHitObjectsString() );
        while ( sc.hasNextLine() )
        {
            String line = sc.nextLine();
            if ( line.length() > 0 )
            {
                int combo = findType( Integer.parseInt( line.split( "," )[3] ) );
                if ( combo == 1 )
                {
                    result.add( new Circle( line ) );
                }
                else if ( combo == 2 )
                {
                    int time = Integer.parseInt( line.split( "," )[2] );
                    // Counts the number of points in slider
                    int p = 1;
                    for ( int i = 0; i < line.length(); i++ )
                        if ( line.charAt( i ) == ':' )
                            p++;
                    // TODO ?
                    try
                    {
                        result.add( new Slider( line,
                            p,
                            getSliderX(),
                            timer.getEffectiveBeatSpace( time ) ) );
                    }
                    catch ( Exception e )
                    {
                        Util.errorException( e, "Slider: " + line );
                    }
                }
                else
                    result.add( new Spinner( line ) );
            }
        }
        hitObjects = result;
        startTime = hitObjects.get( 0 ).getTime();
        endTime = hitObjects.get( hitObjects.size() - 1 ).getEndTime();
    }


    // Finds the type of hitObj given the combo. 1 = circle, 2 = slider, 8 =
    // spinner
    public static int findType( int combo )
    {
        String bin = Integer.toBinaryString( combo );
        if ( bin.charAt( bin.length() - 1 ) == '1' )
            return 1;
        else if ( bin.charAt( bin.length() - 2 ) == '1' )
            return 2;
        else
            return 8;
    }
    
    // returns true if combo indicates NC
    public static boolean isNewCombo( int combo )
    {
        return combo - findType( combo ) >= 4;
    }


    // Maps each hitobject to its notation in the format of MM:SS:mmm (#)
    private void processNotation()
    {
        notations = new HashMap<HitObject, String>();
        int current = 0;
        for ( HitObject h : hitObjects )
        {
            if ( isNewCombo( h.getCombo() ) )
                current = 1;
            else
                current++;
            notations.put( h, Util.formatTime( h.getTime() ) + " (" + current
                + ")" );
        }
    }


    private void processBreaks()
    {
        breakStarts = new ArrayList<Integer>();
        breakEnds = new ArrayList<Integer>();
        Scanner sc = new Scanner( getBreaksString() );
        sc.useDelimiter( "," );
        while ( sc.hasNextLine() )
        {
            String line = sc.nextLine();
            if ( line.length() > 0 )
            {
                breakStarts.add( Integer.parseInt( line.split( "," )[1] ) );
                breakEnds.add( Integer.parseInt( line.split( "," )[2] ) );
            }
        }
    }


    // Getters
    // ***************************************************************************

    public String toString()
    {
        String result = "";
        for ( String s : osuInfo )
            System.out.println( s );
        return result;
    }


    public String[] getGenMeta()
    {
        return osuInfo;
    }


    public String getPreview()
    {
        return osuInfo[PREVIEW_LOC];
    }


    // returns difficulty name aka version
    public String getDiff()
    {
        return osuInfo[DIFF_LOC];
    }


    public String getDiffBoxed()
    {
        return "[" + osuInfo[DIFF_LOC] + "]";
    }


    public String getTags()
    {
        return osuInfo[TAGS_LOC];
    }


    public int getBeatDivisor()
    {
        return Integer.parseInt( osuInfo[BEATDIV_LOC] );
    }


    public String getHitObjectsString()
    {
        return contents.substring( contents.indexOf( "[HitObjects]" ) + 14,
            contents.length() );
    }


    public String getColoursString()
    {
        return extractBlock( "[Colours]", "[HitObjects]" );
    }


    public String getTimingPointsString()
    {
        // if no custom colors
        if ( extractBlock( "[TimingPoints]", "[Colours]" ) == null )
            return extractBlock( "[TimingPoints]", "[HitObjects]" );
        return extractBlock( "[TimingPoints]", "[Colours]" );
    }


    public String getEventsString()
    {
        return extractBlock( "[Events]", "[TimingPoints]" );
    }


    public String getBreaksString()
    {
        return extractBlock( "//Break Periods",
            "//Storyboard Layer 0 (Background)" );
    }


    public ArrayList<HitObject> getHitObjects()
    {
        return hitObjects;
    }


    public HashMap<HitObject, String> getNotation()
    {
        return notations;
    }


    // Gets the SliderMultiplier
    public double getSliderX()
    {
        return Double.parseDouble( Util.extract( "SliderMultiplier:", contents ) );
    }


    public ArrayList<Integer> getBreakStarts()
    {
        return breakStarts;
    }


    public ArrayList<Integer> getBreakEnds()
    {
        return breakEnds;
    }


    public int getStartTime()
    {
        return startTime;
    }


    public int getEndTime()
    {
        return endTime;
    }


    public Timing getTimer()
    {
        return timer;
    }


    public OsuFileChecker getOsuFileChecker()
    {
        return ofc;
    }


    public boolean epilepsyFound()
    {
        if ( osuInfo[EP_LOC].equals( "1" ) )
            return true;
        return false;

    }


    // Utility
    // ***************************************************************************

    private String extractBlock( String start, String end )
    {
        int beginIndex = contents.indexOf( start );
        int endIndex = contents.indexOf( end );
        if ( beginIndex < 0 || endIndex < 0 )
            return null;
        // Adjust so substring cuts properly
        beginIndex += start.length() + 2;
        endIndex -= 2;
        if ( beginIndex >= endIndex )
            return "";
        return contents.substring( beginIndex, endIndex );
    }

    // public static void main( String[] args )
    // {
    // Test runtime
    // processhitobj takes a lot of time, ~ 5 seconds
    // - Using split instead of scanner = .6 sec
    // - Sliders are very slow = 2.3 sec
    // timer takes about .7 secs
    // ofc takes almost no time
    // }
}
