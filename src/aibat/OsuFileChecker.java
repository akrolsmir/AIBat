package aibat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import obj.Circle;
import obj.HitObject;
import obj.Slider;
import obj.Spinner;
import obj.TimingPoint;


public class OsuFileChecker
{
    private static final int MIN_SPIN_LENGTH = 750, MAX_SPIN_LENGTH = 7500,
                    MIN_BREAK_LENGTH = 750, MAX_BREAK_LENGTH = 15000,
                    BREAK_FREQ_SUG = 90000, BREAK_FREQ_ABS = 135000,
                    MIN_CIRCLE_SIZE = 3, MAX_CIRCLE_SIZE = 7;

    private static final double MIN_STACK_LEN = 0.3;

    private String snapCheck = "", catmullCheck = "", spinLengthCheck = "",
                    stackLenCheck = "", breaksCheck = "", spinNCCheck = "";

    private StringBuilder whistle, clap, finish;

    // private String kiaiTimes = "";

    private OsuFileParser ofp;

    private Timing t;


    // These are quick (less than 10 ms each)
    public OsuFileChecker( OsuFileParser o )
    {
        ofp = o;
        t = ofp.getTimer();
        checkHitObj();
        checkStackLen();
        checkGreenPointsSnap();
        checkBreaks();
    }


    // Checks for snapping, catmull sliders, spinner length
    private void checkHitObj()
    {
        HashMap<HitObject, String> notations = ofp.getNotation();
        ListIterator<HitObject> iter = ofp.getHitObjects().listIterator();
        while ( iter.hasNext() )
        {
            HitObject h = iter.next();
            if ( h instanceof Circle )
            {
                // Snapping
                if ( !t.isAlmostSnapped( h.getTime(), 1 ) )
                    snapCheck += notations.get( h ) + " - Unsnapped circle.\n";
            }
            else if ( h instanceof Spinner )
            {
                // Snapping:
                if ( !t.isAlmostSnapped( h.getTime(), 1 ) )
                    snapCheck += notations.get( h )
                        + " - Unsnapped spinner (start).\n";
                if ( !t.isAlmostSnapped( h.getEndTime(), 1 ) )
                    snapCheck += notations.get( h )
                        + " - Unsnapped spinner (end).\n";

                // Spinner Length:
                int length = h.getEndTime() - h.getTime();
                if ( length < MIN_SPIN_LENGTH )
                    spinLengthCheck += notations.get( h )
                        + " - This spinner is " + length
                        + " ms long, which is shorter than " + MIN_SPIN_LENGTH
                        + " ms; check that it gets at least 2000 on auto.\n";
                if ( length > MAX_SPIN_LENGTH )
                    spinLengthCheck += notations.get( h )
                        + " - This spinner is " + length
                        + " ms long, which is longer than " + MAX_SPIN_LENGTH
                        + " ms; it may get tiring to play.\n";

                // Check if properly NC'd:
                if ( !OsuFileParser.isNewCombo( h.getCombo() ) )
                {
                    spinNCCheck += notations.get( h )
                        + " - This spinner lacks a new combo.\n";
                }

                // Check if next note properly NC'd:
                // TODO use better iteration/thing
                if ( iter.hasNext() )
                {
                    HitObject nextObj = iter.next();
                    if ( !( nextObj instanceof Spinner )
                        && !OsuFileParser.isNewCombo( nextObj.getCombo() ) )
                    {
                        spinNCCheck += notations.get( nextObj )
                            + " - Put a new combo on this note so the previous spinner ends properly.\n";
                    }
                    iter.previous();
                }
            }
            else if ( h instanceof Slider )
            {
                // Snapping:
                if ( !t.isAlmostSnapped( h.getTime(), 1 ) )
                    snapCheck += notations.get( h )
                        + " - Unsnapped slider (start).\n";
                // TODO test this for first, middle, last.
                // for(int i = 0; i < ((Slider)h).getRepeats(); i++)
                // if(!t.isAlmostSnapped( ( (Slider)h ).getTimeAt(i), 1 ))
                // snapCheck += notations.get( h )
                // + " - Unsnapped slider (middle).\n";
                if ( !t.isAlmostSnapped( h.getEndTime(), 2 ) )
                    snapCheck += notations.get( h )
                        + " - Unsnapped slider (end).\n";

                // Catmull:
                if ( ( (Slider)h ).getType().equals( "C" ) )
                    catmullCheck += notations.get( h )
                        + " - This slider is a Catmull slider, which is discouraged.\n";
            }
        }
    }


    private void checkStackLen()
    {
        double stackLen = Double.parseDouble( ofp.getGenMeta()[ofp.STACK_LOC] );
        if ( stackLen < MIN_STACK_LEN )
            stackLenCheck += "The stack leniency is set to " + ( stackLen )
                + ", which is below the recommended leniency of "
                + MIN_STACK_LEN + "\n";
    }


    private void checkGreenPointsSnap()
    {

        String result = "";
        for ( TimingPoint green : t.getGreenPoints() )
        {
            int time = green.getTime();
            if ( !t.isAlmostSnapped( time, 1 ) )
                result += Util.formatTime( time ) + " (snap to "
                    + Util.formatTime( t.snap( time, ofp.getBeatDivisor() ) )
                    + ")\n";
        }
        if ( result.length() > 0 )
            snapCheck += "\nUnsnapped inherited (green) timing point(s) at:\n"
                + result;
    }


    private void checkBreaks()
    {
        ArrayList<Integer> bStarts = ofp.getBreakStarts();
        ArrayList<Integer> bEnds = ofp.getBreakEnds();
        // Checks break length
        for ( int i = 0; i < bStarts.size(); i++ )
        {
            int bLength = bEnds.get( i ) - bStarts.get( i );
            if ( bLength > MAX_BREAK_LENGTH )
            {
                breaksCheck += Util.formatTime( bStarts.get( i ) )
                    + " - The break here is " + bLength
                    + " ms long, which is longer than the recommended "
                    + MAX_BREAK_LENGTH + " ms.\n";
            }
            else if ( bLength < MIN_BREAK_LENGTH )
            {
                breaksCheck += Util.formatTime( bStarts.get( i ) )
                    + " - The break here is " + bLength
                    + " ms long, which is shorter than the recommended "
                    + MIN_BREAK_LENGTH + " ms.\n";
            }
        }
        // Checks break frequency
        bEnds.add( 0, ofp.getStartTime() );
        bStarts.add( ofp.getEndTime() );
        for ( int i = 0; i < bEnds.size(); i++ )
        {
            int freq = bStarts.get( i ) - bEnds.get( i );
            if ( freq > BREAK_FREQ_ABS )
            {
                breaksCheck += "You need a break before "
                    + Util.formatTime( bEnds.get( i ) + BREAK_FREQ_ABS )
                    + ".\n";
            }
            else if ( freq > BREAK_FREQ_SUG )
            {
                breaksCheck += "Consider adding a break before "
                    + Util.formatTime( bEnds.get( i ) + BREAK_FREQ_SUG )
                    + ".\n";
            }
        }
    }


    public void processHitsoundBookmark()
    {
        whistle = new StringBuilder();
        finish = new StringBuilder();
        clap = new StringBuilder();
        for ( HitObject h : ofp.getHitObjects() )
        {
            if ( h instanceof Circle )
            {
                addHitsoundTime( h.getHitsound(), h.getTime() );
            }
            else if ( h instanceof Slider )
            {
                Slider s = (Slider)h;
                int iter = s.getRepeats() + 1;
                for ( int i = 0; i < iter; i++ )
                {
                    addHitsoundTime( s.getHitsoundAt( i ), s.getTimeAt( i ) );
                }
            }
            else if ( h instanceof Spinner )
            {
                addHitsoundTime( h.getHitsound(), h.getEndTime() );
            }
        }
        if ( whistle.length() > 0 )
            whistle.deleteCharAt( whistle.length() - 1 );
        if ( finish.length() > 0 )
            finish.deleteCharAt( finish.length() - 1 );
        if ( clap.length() > 0 )
            clap.deleteCharAt( clap.length() - 1 );
    }


    private void addHitsoundTime( int hitsound, int time )
    {
        if ( hitsound == 0 )
            return;
        if ( hitsound >= 8 )
        {
            hitsound -= 8;
            clap.append( time + "," );
        }
        if ( hitsound >= 4 )
        {
            hitsound -= 4;
            finish.append( time + "," );
        }
        if ( hitsound == 2 )
        {
            whistle.append( time + "," );
        }
    }


    public String getSnapCheck()
    {
        return snapCheck;
    }


    public String getCatmullCheck()
    {
        return catmullCheck;
    }


    public String getSpinLengthCheck()
    {
        return spinLengthCheck;
    }


    public String getStackLenCheck()
    {
        return stackLenCheck;
    }


    public String getBreaksCheck()
    {
        return breaksCheck;
    }


    public String getWhistle()
    {
        return whistle.toString();
    }


    public String getFinish()
    {
        return finish.toString();
    }


    public String getClap()
    {
        return clap.toString();
    }


    public String getWrongKiais()
    {
        return t.getWrongKiais();
    }


    public String getPreviewCheck()
    {
        if ( Double.parseDouble( ofp.getPreview() ) < 0 )
        {
            return "A preview point has not yet been set for this difficulty.";
        }
        return "";
    }


    public String getSpinNCCheck()
    {
        return spinNCCheck;
    }
}