package obj;

import java.text.DecimalFormat;


public class TimingPoint
{
    private int measure, hitsounds, customHit, volume, type, kiai;

    private int time;

    private double beatSpace;


    public TimingPoint( String input )
    {
        int on = 0;
        String[] k = input.split( "," );
        time = (int)Double.parseDouble( k[on++] );
        beatSpace = Double.parseDouble( k[on++] );
        measure = Integer.parseInt( k[on++] );
        hitsounds = Integer.parseInt( k[on++] );
        customHit = Integer.parseInt( k[on++] );
        volume = Integer.parseInt( k[on++] );
        type = (int)Double.parseDouble( k[on++] );
        kiai = (int)Double.parseDouble( k[on++] );
    }


    public String toString()
    {
        String result = time + "," + beatSpace + "," + measure + ","
            + hitsounds + "," + customHit + "," + volume + "," + type + ","
            + kiai;
        return result;
    }


    public int getTime()
    {
        return time;
    }


    public double getBeatSpace()
    {
        return beatSpace;
    }


    public boolean isRed()
    {
        return type > 0;
    }


    public boolean isKiai()
    {
        return kiai > 0;
    }


    public String getBPM()
    {
        return new DecimalFormat( "0.000" ).format( 60000 / beatSpace );
    }
}