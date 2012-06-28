package modtrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import obj.Circle;
import obj.HitObject;
import obj.Slider;
import obj.Spinner;
import aibat.OsuFileParser;
import aibat.Util;

public class DiffComparator {
    // TODO implement TreeMultiMap instead?
    private Map<String, String> changes = new TreeMap<String, String>();
    private Map<HitObject, String> origNotations;

    private List<HitObject> init, chng;

    public DiffComparator(OsuFileParser orig, OsuFileParser curr) {
	init = orig.getHitObjects();
	chng = curr.getHitObjects();
	origNotations = orig.getNotation();
    }

    public String compare() {
	Iterator<HitObject> initIter = init.iterator();
	Iterator<HitObject> chngIter = chng.iterator();
	HitObject i = initIter.next();
	HitObject c = chngIter.next();
	while (i != null && c != null) {
	    // If they're the same, continue
	    if (i.getInput().equals(c.getInput())) {
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
	    else {// if (iTime > cTime)
		add(c);
		c = chngIter.hasNext() ? chngIter.next() : null;
	    }
	}
	while (i != null) {
	    remove(i);
	    i = initIter.hasNext() ? initIter.next() : null;
	}
	while (c != null) {
	    add(c);
	    c = chngIter.hasNext() ? chngIter.next() : null;
	}
	return getChangesString();
    }

    private void add(HitObject h) {
	List<String> addMsgs = new ArrayList<String>();

	if (h instanceof Circle) {
	    addMsgs.add("add a circle at " + h.getPosString());

	    if (h.isNewCombo())
		addMsgs.add("with a NC");
	}
	else if (h instanceof Spinner) {
	    addMsgs.add("add a spinner from here until "
		    + Util.formatTime(h.getEndTime()));

	    if (!h.isNewCombo())
		addMsgs.add("without a NC");
	}
	else if (h instanceof Slider) {
	    Slider s = ((Slider) h);
	    StringBuilder initMsg = new StringBuilder(
		    "add a slider from here until "
			    + Util.formatTime(s.getTimeAt(1)));

	    int ends = s.getRepeats() + 1;
	    switch (ends) {
	    case 2:
		break;
	    case 3:
		initMsg.append(" and then repeat it once");
		break;
	    default:
		initMsg.append(" and then repeat it " + (ends - 2) + " times");
	    }
	    addMsgs.add(initMsg.toString());

	    for (int j = 0; j < ends; j++) {
		int sH = s.getHitsoundAt(j);
		if (sH == 0)
		    continue;

		String repeatPos;
		if (j == 0)
		    repeatPos = " on the start";
		else if (j == ends - 1)
		    repeatPos = " on the end";
		else
		    repeatPos = " on repeat #" + j + " (at "
			    + Util.formatTime(s.getTimeAt(j)) + ")";

		addMsgs.add("with" + hitsoundString(sH, " a ") + repeatPos);
	    }

	    if (h.isNewCombo())
		addMsgs.add("with a NC");
	}

	// Always notify about hitsounds
	if (h.getHitsound() > 1)
	    addMsgs.add("with" + hitsoundString(h.getHitsound(), " a ")
		    + ((h instanceof Slider) ? " on the entire slider" : ""));

	changes.put(Util.formatTime(h.getTime()), Util.colToStr(addMsgs, ", "));

    }

    private void remove(HitObject h) {
	changes.put(origNotations.get(h), "remove this " + className(h));
    }

    private static String className(HitObject h) {
	String classString = h.getClass().toString();
	return (classString.substring(classString.indexOf('.') + 1)
		.toLowerCase());
    }

    private void compare(HitObject i, HitObject c) {
	List<String> compareMsgs = new ArrayList<String>();

	if (i instanceof Circle) {
	    if (i.getX() != c.getX() && i.getY() != c.getY())
		compareMsgs.add("move to " + c.getPosString());
	}
	else if (i instanceof Spinner) {
	    if (i.getEndTime() != c.getEndTime())
		compareMsgs.add("end at " + Util.formatTime(c.getEndTime()));
	}
	else if (i instanceof Slider) {
	    Slider iS = ((Slider) i), cS = ((Slider) c);
	    // TODO finish with moved nodes, changed repeats.

	    // only for the matching times
	    int ends = iS.getRepeats() + 1;
	    for (int j = 0; j < ends; j++) {
		if (iS.getTimeAt(j) == cS.getTimeAt(j))
		    continue;
		int iH = iS.getHitsoundAt(j), cH = cS.getHitsoundAt(j);
		if (iH == cH)
		    continue;

		String repeatPos;
		if (j == 0)
		    repeatPos = " on the start";
		else if (j == ends - 1)
		    repeatPos = " on the end";
		else
		    repeatPos = " on repeat #" + j + " (at "
			    + Util.formatTime(iS.getTimeAt(j)) + ")";

		compareMsgs.add(compareHitsounds(iH, cH) + repeatPos);
	    }
	}
	int iH = i.getHitsound(), cH = c.getHitsound();
	if (iH != cH)
	    compareMsgs.add(compareHitsounds(iH, cH)
		    + ((i instanceof Slider) ? " on the entire slider" : ""));

	if (!i.isNewCombo() && c.isNewCombo())
	    compareMsgs.add("add NC");
	if (i.isNewCombo() && !c.isNewCombo())
	    compareMsgs.add("remove NC");

	changes.put(origNotations.get(i), Util.colToStr(compareMsgs, ", "));
    }

    private static List<String> hitsoundIntToList(int hitsound) {
	if (hitsound <= 1)
	    return null;

	// Too lazy make into constants, but bit values are:
	// clap = 8, finish = 4, whistle = 2
	List<String> hitsoundList = new ArrayList<String>();
	if ((hitsound & 8) > 0)
	    hitsoundList.add("clap");
	if ((hitsound & 4) > 0)
	    hitsoundList.add("finish");
	if ((hitsound & 2) > 0)
	    hitsoundList.add("whistle");
	return hitsoundList;
    }

    // returns pre + "clap" [ + " and" + pre + "finish"]..."
    // pre should be " a " or " the "
    private static String hitsoundString(int hitsound, String pre) {
	return pre + Util.colToStr(hitsoundIntToList(hitsound), " and" + pre);
    }

    private static String compareHitsounds(int iH, int cH) {
	if (iH == cH)
	    return null;

	StringBuilder result = new StringBuilder();
	int addH = ~iH & cH;
	if (addH > 1)
	    result.append("add" + hitsoundString(addH, " a "));

	int removeH = iH & ~cH;
	if (removeH > 1) {
	    if (result.length() > 0)
		result.append(" and ");
	    result.append("remove" + hitsoundString(removeH, " the "));
	}
	return result.toString();
    }

    // Parses changes into a nicely formatted string
    private String getChangesString() {
	StringBuilder result = new StringBuilder();
	for (Entry<String, String> change : changes.entrySet()) {

	    String toShow = change.getValue();
	    if (toShow == null || toShow.length() == 0)
		continue;

	    // Capitalize + append newline
	    toShow = Character.toUpperCase(toShow.charAt(0))
		    + toShow.substring(1) + ".\n";
	    result.append(change.getKey() + " - " + toShow);
	}
	return result.toString();
    }

    // public static void main(String[] args) {
    // }

    // TODO finish, use if you want to combine add+remove or remove+add into
    // timeshift
    //
    // private enum Type {
    // ADD, REMOVE, CHANGE, NOTE
    // };
    //
    // private class ModEntry {
    // private Type type;
    // private String edit;
    //
    // private ModEntry() {
    // type = Type.NOTE;
    // edit = "";
    // }
    // }

}
