import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import obj.TimingPoint;


// This class takes care of timing issues, such as checking for snapping.
public class Timing
{
    private ArrayList<Kiai> Kiais;

    private ArrayList<TimingPoint> redPoints, greenPoints, allPoints;

    public OsuFileParser ofp;


    public Timing( OsuFileParser initial )
    {
        ofp = initial;
        redPoints = new ArrayList<TimingPoint>();
        greenPoints = new ArrayList<TimingPoint>();
        allPoints = new ArrayList<TimingPoint>();
        Scanner sc = new Scanner( ofp.getTimingPointsString() );
        while ( sc.hasNextLine() )
        {
            String s = sc.nextLine();
            if ( s.length() == 0 )
                break;
            try
            {
                TimingPoint tp = new TimingPoint( s );
                if ( tp.isRed() )
                    redPoints.add( tp );
                else
                    greenPoints.add( tp );
                allPoints.add( tp );
            }
            catch ( Exception e )
            {
                Util.errorException( e, "Failed TimingPoint: " + s );
            }
        }
        processKiais();
    }


    public void processKiais()
    {
        Kiais = new ArrayList<Kiai>();
        boolean onKiai = false;
        int numKiai = 0, start = -1;
        for ( TimingPoint tp : allPoints )
        {
            if ( tp.isKiai() && !onKiai )
            {
                numKiai++;
                onKiai = true;
                start = tp.getTime();
            }
            else if ( !tp.isKiai() && onKiai )
            {
                onKiai = false;
                Kiais.add( new Kiai( numKiai, start, tp.getTime() ) );
            }
        }

        if ( onKiai && start > 0 )
        {
            Kiais.add( new Kiai( numKiai, start, -1 ) );
        }
    }


    //TODO use better method
    private boolean isSnapped( int time )
    {
        TimingPoint tp = getPreviousRed( time );
        // System.out.println(tp);
        double eighth = tp.getBeatSpace() / 8;
        double sixth = tp.getBeatSpace() / 6;

        int space = ( time - tp.getTime() );
        long away = Math.round( space / eighth );
        if ( Math.floor( away * eighth ) == space )
            return true;

        away = Math.round( space / sixth );
        if ( Math.floor( away * sixth ) == space )
            return true;

        return false;
    }


    //Checks if it is snapped to the given divisor.
    //TODO remove duplicate code.
    private boolean isSnapped( int time, int divisor )
    {
        TimingPoint tp = getPreviousRed( time );

        double nth = tp.getBeatSpace() / divisor;
        int space = ( time - tp.getTime() );
        long away = Math.round( space / nth );
        if ( Math.floor( away * nth ) == space )
            return true;
        return false;
    }


    // check for snapped +/- error ms
    public boolean isAlmostSnapped( int time, int error )
    {
        for ( int i = -error; i <= error; i++ )
        {
            if ( isSnapped( time + i ) )
                return true;
        }
        return false;
    }


    private boolean isDownbeat( int time )
    {
        TimingPoint tp = getPreviousRed( time );
        double beatspace = tp.getBeatSpace();
        int space = ( time - tp.getTime() );
        long away = Math.round( space / beatspace );
        return ( Math.floor( away * beatspace ) == space );
    }


    public boolean isAlmostDownbeat( int time, int error )
    {
        for ( int i = -error; i <= error; i++ )
        {
            if ( isDownbeat( time + i ) )
                return true;
        }
        return false;
    }


    // public Integer[] snapTimes( int upTo )
    // {
    // ArrayList<Integer> result = new ArrayList<Integer>();
    // for ( int i = 0; i < upTo; i++ )
    // {
    // if ( isSnapped( i ) )
    // result.add( i );
    // }
    // return result.toArray( new Integer[0] );
    // }

    public String redPointsString()
    {
        String result = "";
        for ( TimingPoint tp : redPoints )
        {
            result += Util.formatTime( tp.getTime() ) + ", BPM of "
                + tp.getBPM() + "\n";
        }
        return result;
    }


    public String getKiaiTimes()
    {
        StringBuilder result = new StringBuilder();
        for ( Kiai k : Kiais )
        {
            result.append( k.toString() + "\n" );
        }
        return result.toString();
    }


    public String getWrongKiais()
    {
        StringBuilder result = new StringBuilder();
        for ( Kiai k : Kiais )
        {
            if ( !isAlmostDownbeat( k.getStart(), 1 ) )
            {
                result.append( k.toString() + "\n" );
            }
        }
        return result.toString();
    }


    public ArrayList<Kiai> getKiais()
    {
        return Kiais;
    }


    public class Kiai
    {
        private int start, end, number;


        public Kiai( int n, int s, int e )
        {
            number = n;
            start = s;
            end = e;

        }


        @Override
        public String toString()
        {
            if ( end > 0 )
                return "Kiai #" + number + ": Starts on "
                    + Util.formatTime( start ) + ", ends on "
                    + Util.formatTime( end );
            else
                return "Kiai #" + number + ": Starts on "
                    + Util.formatTime( start ) + ", until the end";
        }


        @Override
        public boolean equals( Object other )
        {
            if ( other instanceof Kiai )
                return start == ( (Kiai)other ).getStart()
                    && end == ( (Kiai)other ).getEnd();
            return false;
        }


        public int getEnd()
        {
            return end;
        }


        public int getStart()
        {
            return start;
        }

    }


    private TimingPoint getPreviousRed( int time )
    {
        int i = 0;
        while ( i < redPoints.size() && time >= redPoints.get( i ).getTime() )
            i++;
        if ( i > 0 )
            i--;
        return redPoints.get( i );
    }


    private TimingPoint getPreviousTP( int time )
    {
        int i = 0;
        while ( i < allPoints.size() && time >= allPoints.get( i ).getTime() )
            i++;
        if ( i > 0 )
            i--;
        return allPoints.get( i );
    }


//    public int snap( int time )
//    {
//        // Returns the nearest snapped time
//        // i.e. if time is t, checks t, t-1, t-1+2, etc.
//        int polarity = 1;
//        for ( int i = 0;; i++ )
//        {
//            time += polarity * i;
//            if ( isSnapped( time ) )
//                return time;
//            polarity *= -1;
//        }
//    }


    public int snap( int time, int divisor )
    {
        // Returns the nearest snapped time
        // i.e. if time is t, checks t, t-1, t-1+2, etc.
        // Will only check through every divisor
        //TODO remove duplicate code.
        int polarity = 1;
        for ( int i = 0;; i++ )
        {
            time += polarity * i;
            if ( isSnapped( time, divisor ) )
                return time;
            polarity *= -1;
        }
    }


    // public int snap(int near, int error)
    // {
    // int pol = 1;
    // int check = near;
    // for(int i = 0; i < error * 2 + 1; i++)
    // {
    // check += pol * i;
    // if(isSnapped(check))
    // return check;
    // pol *= -1;
    // }
    // return -1;
    // }

    // Getters ***************************************************************8
    public double getEffectiveBeatSpace( int time )
    {
        TimingPoint tp = getPreviousTP( time );
        if ( tp.isRed() )
            return tp.getBeatSpace();
        else
            return -tp.getBeatSpace()
                * getPreviousRed( tp.getTime() ).getBeatSpace() / 100;
    }


    public ArrayList<TimingPoint> getGreenPoints()
    {
        return greenPoints;
    }


    // Test
    public static void main( String[] args )
    {
        TimingPoint tp = new TimingPoint( "7995.87234042553,428.571428571429,4,1,1,60,1,0" );
        System.out.println( tp.getTime() + " " + tp.getBeatSpace() );
    }

}