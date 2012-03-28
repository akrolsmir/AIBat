package obj;
public class Circle extends HitObject
{
    // input format:
    // X,Y,Time,Combo,Hitsound
    public Circle( String input )
    {
        super( input );
        endX = x;
        endY = y;
        endTime = time;
    }

}