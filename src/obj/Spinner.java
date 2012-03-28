package obj;

public class Spinner extends HitObject
{
    // input format:
    // X,Y,Time,Combo,Hitsound,EndTime
    // next note must have new combo
    public Spinner( String input )
    {
        super( input );
        endX = x;
        endY = y;
        endTime = Integer.parseInt( k[on++] );
    }


    public String toString()
    {
        return super.toString() + "," + endTime;
    }

}