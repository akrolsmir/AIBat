package modtrace;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import obj.Circle;
import obj.HitObject;
import obj.Slider;
import obj.Spinner;
import aibat.OsuFileParser;
import aibat.Util;

public class DiffComparator {
    // private List<Change> changes; TODO remove Change?
    private String changes = "";

    public String compare(List<HitObject> init, List<HitObject> chng) {
	Iterator<HitObject> initIter = init.iterator();
	Iterator<HitObject> chngIter = chng.iterator();
	HitObject i = initIter.next();
	HitObject c = chngIter.next();
	// HitObject i, c;
	while (i != null && c != null) {
	    // If they're the same, continue
	    if (i.toString().equals(c.toString())) {
		i = initIter.hasNext() ? initIter.next() : null;
		c = chngIter.hasNext() ? chngIter.next() : null;
		continue;
	    }
	    int iTime = i.getTime(), cTime = c.getTime();
	    if (i.getClass().equals(c.getClass()) && iTime == cTime) {
		// only compares if times AND classes are same
		compare(i, c);
		i = initIter.hasNext() ? initIter.next() : null;
		c = chngIter.hasNext() ? chngIter.next() : null;
	    }
	    else if (iTime <= cTime) {
		remove(i);
		i = initIter.hasNext() ? initIter.next() : null;
	    }
	    else // if (iTime > cTime)
	    {
		add(c);
		c = chngIter.hasNext() ? chngIter.next() : null;
	    }

	    // FIXME last bug.
	}
	while (initIter.hasNext()) {
	    i = initIter.next();
	    remove(i);

	}
	while (chngIter.hasNext()) {
	    c = chngIter.next();
	    add(c);
	}
	return changes;
    }

    private void remove(HitObject h) {
	// changes += ("Remove: " + h.toString());
	//TODO switch based on type
	changes += (Util.formatTime(h.getTime()) + " - " + "Remove this "
		+ className(h) + ".\n");
    }

    private void add(HitObject h) {
	// changes += ("Add: " + h.toString());
	//TODO switch based on type
	changes += (Util.formatTime(h.getTime()) + " - " + "Add a "
		+ className(h) + ".\n");
    }

    private void compare(HitObject a, HitObject b) {
	changes += ("Compare: " + a.toString() + " / " + b.toString() + ".\n");
    }

    private String className(HitObject h) {
	if (h instanceof Circle)
	    return "circle";
	else if (h instanceof Slider)
	    return "slider";
	else if (h instanceof Spinner)
	    return "spinner";
	else
	    return "";

    }

    public static void main(String[] args) {
	long start = System.currentTimeMillis();
	OsuFileParser ofp1 = new OsuFileParser(
		new File(
			"C:\\Users\\Akrolsmir\\Desktop\\fripSide - Prominence-Version 2007\\fripSide - prominence -version 2007- (akrolsmir) [Normal].osu"));
	OsuFileParser ofp2 = new OsuFileParser(
		new File(
			"C:\\Users\\Akrolsmir\\Desktop\\fripSide - Prominence-Version 2007\\fripSide - prominence -version 2007- (akrolsmir) [Normal_2].osu"));
	System.out.println(new DiffComparator().compare(ofp1.getHitObjects(),
		ofp2.getHitObjects()));
	Util.logTime(start);
    }
}
