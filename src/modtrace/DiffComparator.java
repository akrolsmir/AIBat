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
	    else // if (iTime > cTime)
	    {
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
	List<String> addMessages = new ArrayList<String>();

	if (h instanceof Circle) {
	    addMessages.add("add a circle at " + h.getPosString());

	    if (h.isNewCombo())
		addMessages.add("with a NC");
	}
	else if (h instanceof Spinner) {
	    addMessages.add("add a spinner from here until "
		    + Util.formatTime(h.getEndTime()));

	    if (!h.isNewCombo())
		addMessages.add("without a NC");
	}
	else if (h instanceof Slider) {
	    Slider s = ((Slider) h);
	    StringBuilder temp = new StringBuilder(
		    "add a slider from here until "
			    + Util.formatTime(s.getTimeAt(1)));

	    switch (s.getRepeats()) {
	    case 0:
		Util.errorMessage(h.toString());
		break;
	    case 1:
		break;
	    case 2:
		temp.append(" and then repeat it once");
		break;
	    default:
		temp.append(" and then repeat it " + (s.getRepeats() - 1)
			+ " times");
	    }
	    addMessages.add(temp.toString());

	    if (h.isNewCombo())
		addMessages.add("with a NC");
	    // TODO add hitsounds for individual repeats
	}

	// Always notify about hitsounds
	if (h.getHitsound() > 1)
	    addMessages.add("with" + hitsoundString(h.getHitsound(), " a ")
		    + ((h instanceof Slider) ? " on the entire slider" : ""));

	changes.put(Util.formatTime(h.getTime()),
		Util.colToStr(addMessages, ", "));

    }

    private void remove(HitObject h) {
	changes.put(origNotations.get(h), "remove this " + className(h));
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

    private void compare(HitObject i, HitObject c) {
	List<String> compMessages = new ArrayList<String>();

	if (i instanceof Circle) {
	    if (i.getX() != c.getX() && i.getY() != c.getY())
		compMessages.add("move to " + c.getPosString());
	}
	else if (i instanceof Spinner) {
	    if (i.getEndTime() != c.getEndTime())
		compMessages.add("end at " + Util.formatTime(c.getEndTime()));
	}
	else if (i instanceof Slider) {
	    // TODO finish
	}
	
	int iH = i.getHitsound(), cH = c.getHitsound();
	if (iH != cH) {
	    int addH = ~iH & cH;
	    if (addH > 1)
		compMessages.add("add" + hitsoundString(addH, " a "));
	    
	    int removeH = iH & ~cH;
	    if (removeH > 1)
		compMessages.add("remove" + hitsoundString(removeH, " the "));
	}

	if (!i.isNewCombo() && c.isNewCombo())
	    compMessages.add("add NC");
	if (i.isNewCombo() && !c.isNewCombo())
	    compMessages.add("remove NC");

	changes.put(origNotations.get(i), Util.colToStr(compMessages, ", "));
    }

    private static String className(HitObject h) {
	String classString = h.getClass().toString();
	return (classString.substring(classString.indexOf('.') + 1)
		.toLowerCase());
    }

    // Parses changes into a nicely formatted string
    private String getChangesString() {
	StringBuilder result = new StringBuilder();
	for (Entry<String, String> change : changes.entrySet()) {
	    // Capitalize + append newline
	    String toShow = change.getValue();
	    if(toShow != null && toShow.length() > 0)
	    toShow = Character.toUpperCase(toShow.charAt(0))
		    + toShow.substring(1) + ".\n";

	    result.append(change.getKey() + " - " + toShow);
	}
	return result.toString();
    }

    public static void main(String[] args) {
	// System.out.println(className(new Circle("113,86,10576,1,0")));
	// long start = System.currentTimeMillis();
	// OsuFileParser ofp1 = new OsuFileParser(
	// new File(
	// "C:\\Users\\Akrolsmir\\Desktop\\fripSide - Prominence-Version 2007\\fripSide - prominence -version 2007- (akrolsmir) [Normal].osu"));
	// OsuFileParser ofp2 = new OsuFileParser(
	// new File(
	// "C:\\Users\\Akrolsmir\\Desktop\\fripSide - Prominence-Version 2007\\fripSide - prominence -version 2007- (akrolsmir) [Normal_2].osu"));
	// System.out.println(new DiffComparator(ofp1, ofp2).compare());
	// Util.logTime(start);
	// int hitsound = 10;
	// System.out.println((hitsound & 8) == 8);
	// System.out.println((hitsound & 4) == 4);
	// System.out.println((hitsound & 2) == 2);
	for (int i = 0; i < 16; i++) {
	    System.out.println(i + ": " + hitsoundString(i, " a "));
	    // System.out.println(i + ": " + hitsoundString(i, false));
	    // System.out.println(i + ": " + ((i & 4) > 0));
	}
    }
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

    // FIXME some way to have natural lang (bit flags?)
    // int hitsound = h.getHitsound();
    // if (hitsound != 0) {
    // if (hitsound >= 8) {
    // hitsound -= 8;
    // addMessage.append(", with a clap");
    // }
    // if (hitsound >= 4) {
    // hitsound -= 4;
    // addMessage.append(", with a finish");
    // }
    // if (hitsound >= 2) {
    // addMessage.append(", with a whistle");
    // }
    // if (h instanceof Slider) {
    // addMessage.append(" on the entire slider");
    // }
    // }

    // private enum Hitsound {
    // CLAP(8, "clap"), FINISH(4, "finish"), WHISTLE(2, "whistle");
    // private final int BIT;
    // private final String NAME;
    //
    // Hitsound(int bit, String name) {
    // BIT = bit;
    // NAME = name;
    // }
    // }
}
