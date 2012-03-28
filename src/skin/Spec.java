package skin;

public class Spec
{
    // Helps determine if an element is within specifications

    private final String name;

    // What compare means- 0: exact, 1: size <= specified width and height, 2:
    // size >= spec'd..., -1 or anything else: no specs
    private int compare, width, height;

    private boolean animatable;


    public Spec(
        String name,
        int compare,
        int width,
        int height,
        boolean animatable )
    {
        this.name = name;
        this.compare = compare;
        this.width = width;
        this.height = height;
        this.animatable = animatable;
    }


    public Spec( String name, int compare, int width, int height )
    {
        this( name, compare, width, height, false );
    }


    public Spec( String name, boolean animatable )
    {
        this( name, -1, 0, 0, animatable );
    }


    // Creates a spec with only a name, signifying that any dimensions are fine.
    public Spec( String name )
    {
        this( name, false );
    }


    // Checks if the name, width and height match this Spec
    public boolean isFine( String n, int w, int h )
    {
        if ( !n.equals( name ) )
            return false;
        switch ( compare )
        {
            case 0:
                return ( w == width && h == height );
            case 1:
                return ( w <= width && h <= height );
            case 2:
                return ( w >= width && h >= height );
            default: // -1 = no specs
                return true;
        }
    }


    // Tells why the name, width, and height do not fit.
    public String whyNotFine( String n, int w, int h )
    {
        if ( isFine( n, w, h ) )
            return "Wait, what?\n";
        if ( !n.equals( name ) )
            return "Wrong name.\n";
        StringBuilder result = new StringBuilder( name + "'s dimensions must " );
        switch ( compare )
        {
            case 0:
                result.append( "be exactly " );
                break;
            case 1:
                result.append( "not be larger than " );
                break;
            case 2:
                result.append( "not be smaller than " );
                break;
        }
        result.append( width + "x" + height );
        result.append( " (currently " + w + "x" + h + ").\n" );
        return result.toString();

    }


    public String getName()
    {
        return name;
    }


    public boolean isAnimatable()
    {
        return animatable;
    }

    // public static void main (String[] args)
    // {
    // Spec s = new Spec("hi", 1, 2, 3);
    // System.out.println(s.isFine( "hik", 2, 3 ));
    // System.out.println(s.whyNotFine( "hi", 0, 1 ));
    // System.out.println(s.whyNotFine( "hi", 3, 3 ));
    // System.out.println(s.isFine( "hi", 2, 4 ));
    // }
}
