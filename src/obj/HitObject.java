package obj;

import java.util.Vector;

public abstract class HitObject {
    // Starting values
    protected int x, y, time;

    // Ending values
    protected int endX, endY, endTime;

    // 1 = circle; 2 = slider; 4 = spinner
    // 4, 8, 16, 32, 64 = additional New Combo
    // combo is the sum of all these.
    protected int combo;

    // 0 = default, 2 = whistle, 4 = finish, 8 = clap
    // hitsound is the sum of all these
    protected int hitsound;

    protected int on = 0;

    public String[] k;// holds input split by "[,|:]"

    // Format for first 5 items:
    // X,Y,Time,Combo,Hitsound
    public HitObject(String input) {
	k = input.split("[,|:]");
	x = Integer.parseInt(k[on++]);
	y = Integer.parseInt(k[on++]);
	time = Integer.parseInt(k[on++]);
	combo = Integer.parseInt(k[on++]);
	hitsound = Integer.parseInt(k[on++]);
    }

    public double distanceTo(HitObject other) {
	return distBtwn(endX, endY, other.x, other.y);
    }

    public int timeTo(HitObject other) {
	return (other.time - endTime);
    }

    private double distBtwn(int x1, int y1, int x2, int y2) {
	int sum = (x1 - x2) * (x1 - x2);
	sum += (y1 - y2) * (y1 - y2);
	return Math.sqrt(sum);
    }

    public String toString() {
	return x + "," + y + "," + time + "," + combo + "," + hitsound;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTime() {
	return time;
    }

    public int getEndTime() {
	return endTime;
    }

    public int getCombo() {
	return combo;
    }

    public int getHitsound() {
	return hitsound;
    }

    public boolean isNewCombo() {
	return combo - findType() >= 4;
    }
    
    // Helper method for isNewCombo
    // Finds the type of hitObj given the combo.
    // 1 = circle, 2 = slider, 8 = spinner
    private int findType() {
	switch (Integer.lowestOneBit(combo)) {
	case 1:
	    return 1;
	case 2:
	    return 2;
	default:
	    return 8;
	}
    }

}
