package obj;

public class Slider extends HitObject {
    // 1 for slider w/ no repeats, 2 for 1 repeat...
    private int repeats;

    private double sliderLength;

    // p is a numPoints by 2 array, where 0 = x, 1 = y
    private int[][] p;

    private int[] allHitsounds, allTimes;

    private String sliderType;

    // input format:
    // X,Y,Time,Combo,Hitsound,Type|X1:Y1|X2:Y2...,Repeats,Length,Hit1|Hit2...
    public Slider(String input, int numPoints, double sliderX, double beatSpace) {
	super(input);
	sliderType = k[on++];

	p = new int[numPoints][2];
	p[0][0] = x;
	p[0][1] = y;

	for (int i = 1; i < numPoints; i++) {
	    p[i][0] = Integer.parseInt(k[on++]);
	    p[i][1] = Integer.parseInt(k[on++]);
	}

	repeats = Integer.parseInt(k[on++]);
	sliderLength = Double.parseDouble(k[on++]);

	// Parses the hitsound for the slider.
	allHitsounds = new int[repeats + 1];
	allTimes = new int[repeats + 1];
	// For now, takes a rounded time, instead of proper calibration
	double timeInterval = (sliderLength * beatSpace) / (100 * sliderX);
	for (int i = 0; i < repeats + 1; i++) {
	    if (on < k.length)
		allHitsounds[i] = Integer.parseInt(k[on++]);
	    else
		allHitsounds[i] = hitsound;
	    // May or may not be 1 ms off, same w/ Math.floor
	    allTimes[i] = (int) ((time + i * timeInterval));
	}
	endTime = allTimes[allTimes.length - 1];

	// For now, uses the last node for endX and endY
	if (repeats % 2 == 0) {
	    endX = x;
	    endY = y;
	}
	else {
	    endX = p[numPoints - 1][0];
	    endY = p[numPoints - 1][1];
	}
    }

    // TODO fix.
    public String toString() {
	StringBuilder result = new StringBuilder();
	result.append(super.toString() + "," + sliderType);
	for (int[] a : p) {
	    result.append("|" + a[0] + ":" + a[1]);
	}
	result.append("," + repeats + "," + sliderLength);
	// TODO add check to see if it has custom hitsounds
	return result.toString();
    }

    public String getSliderType() {
	return sliderType;
    }

    // Use getHitsound/TimeAt instead
    // public int[] getAllHitsounds() {
    // return allHitsounds;
    // }
    //
    // public int[] getAllTimes() {
    // return allTimes;
    // }

    public int getHitsoundAt(int num) {
	return allHitsounds[num];
    }

    public int getTimeAt(int num) {
	return allTimes[num];
    }

    public int getRepeats() {
	return repeats;
    }

    public String[] getNodes() {
	int numNodes = p.length;
	String[] nodes = new String[numNodes];
	for (int i = 0; i < numNodes; i++)
	    nodes[i] = formatPos(p[i][0], p[i][1]);
	return nodes;
    }

    // public static void main(String args[]) {
    // Slider s = new Slider(
    // "80,143,153056,2,0,B|64:176|72:216,2,61.8749981559814,0|2|0,0:0|0:0|0:0,0:0",
    // 3, 1.7, 642.054574638844);
    // System.out.println(s.repeats);
    // for (int i = 0; i < s.allHitsounds.length; i++) {
    // System.out.println(s.allHitsounds[i] + ":" + s.allTimes[i]);
    // }
    // }

}