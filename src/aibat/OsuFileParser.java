package aibat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import obj.Circle;
import obj.HitObject;
import obj.Slider;
import obj.Spinner;

public class OsuFileParser implements Comparable<OsuFileParser> {
    private String contents;

    // Map of info in the .osu file (mostly from General and Metadata)
    private Map<String, String> osuInfo = new TreeMap<String, String>();

    // ArrayList of all hitobjects in the diff
    private ArrayList<HitObject> hitObjects = new ArrayList<HitObject>();

    private ArrayList<Integer> breakStarts, breakEnds;

    private HashMap<HitObject, String> notations;

    private int startTime, endTime;

    private Timing timer;

    private OsuFileChecker ofc;

    // @formatter:off
    public final static String[] TITLES = { 
	    "AudioFilename: ", // index = 0
	    "AudioLeadIn: ", // 1
	    "PreviewTime: ", // 2
	    "Countdown: ", // 3
	    "SampleSet: ", // 4
	    "StackLeniency: ", // 5
	    "Mode: ", // 6
	    "LetterboxInBreaks: ", // 7
	    "SkinPreference: ", // 8
	    "EpilepsyWarning: ", // 9

	    "Title:", // 10
	    "Artist:", // 11
	    "Creator:", // 12
	    "Version:", // 13
	    "Source:", // 14
	    "Tags:", // 15

	    "BeatDivisor: ", // 16
	    "Bookmarks: ", //17
	    
	    "SliderMultiplier:", //18
    };
    // @formatter:on

    // public final static int PREVIEW_LOC = 2, SAMPLESET_LOC = 4, STACK_LOC =
    // 5,
    // MODE_LOC = 6, EP_LOC = 9, DIFF_LOC = 13, TAGS_LOC = 15,
    // BEATDIV_LOC = 16;

    public OsuFileParser(File file) {
	contents = Util.readFile(file);
	for (String title : TITLES) {
	    osuInfo.put(title, Util.extract(title, contents));
	}
	timer = new Timing(this);
	try {
	    processHitObjects();
	    processNotation();
	    processBreaks();
	    ofc = new OsuFileChecker(this);
	}
	catch (IndexOutOfBoundsException e) {
	    Util.errorMessage("Found no hitobjects in:\n"
		    + file.getAbsolutePath()
		    + "\nPlace at least 1 hitobject to analyze the file.");
	}
    }

    // Creates hitObjects.
    private void processHitObjects() {
	Scanner sc = new Scanner(getHitObjectsString());
	while (sc.hasNextLine()) {
	    String line = sc.nextLine();
	    if (line.length() > 0) {
		// Switches based on combo
		int combo = Integer.parseInt(line.split(",")[3]);
		switch (Integer.lowestOneBit(combo)) {
		case 1:
		    hitObjects.add(new Circle(line));
		    break;
		case 2:
		    int time = Integer.parseInt(line.split(",")[2]);
		    // Counts the number of points in slider
		    int p = 1;
		    for (int i = 0; i < line.length(); i++)
			if (line.charAt(i) == ':')
			    p++;
		    try {
			hitObjects.add(new Slider(line, p,
				getSliderMultiplier(), timer
					.getEffectiveBeatSpace(time)));
		    }
		    catch (Exception e) {
			Util.errorException(e, getDiffBoxed() + " Slider: "
				+ line);
		    }
		    break;
		default:
		    hitObjects.add(new Spinner(line));
		    break;
		}
	    }
	}
	startTime = hitObjects.get(0).getTime();
	endTime = hitObjects.get(hitObjects.size() - 1).getEndTime();
    }

    // Maps each hitobject to its notation in the format of MM:SS:mmm (#)
    private void processNotation() {
	notations = new HashMap<HitObject, String>();
	int current = 0;
	for (HitObject h : hitObjects) {
	    if (h.isNewCombo())
		current = 1;
	    else
		current++;
	    notations.put(h, Util.formatTime(h.getTime()) + " (" + current
		    + ")");
	}
    }

    private void processBreaks() {
	breakStarts = new ArrayList<Integer>();
	breakEnds = new ArrayList<Integer>();
	Scanner sc = new Scanner(getBreaksString());
	sc.useDelimiter(",");
	while (sc.hasNextLine()) {
	    String line = sc.nextLine();
	    if (line.length() > 0) {
		breakStarts.add(Integer.parseInt(line.split(",")[1]));
		breakEnds.add(Integer.parseInt(line.split(",")[2]));
	    }
	}
    }

    // Getters
    // ***************************************************************************

    public String toString() {
	String result = "";
	for (String s : osuInfo.keySet())
	    System.out.println(s);
	return result;
    }

    public Map<String, String> getOsuInfo() {
	return osuInfo;
    }

    public String getPreview() {
	return osuInfo.get("PreviewTime: ");
    }

    // returns difficulty name aka version
    public String getDiff() {
	return osuInfo.get("Version:");
    }

    public String getDiffBoxed() {
	return "[" + getDiff() + "]";
    }

    public String getTags() {
	return osuInfo.get("Tags:");
    }

    public int getBeatDivisor() {
	return Integer.parseInt(osuInfo.get("BeatDivisor: "));
    }

    public int getAudioLeadIn() {
	return Integer.parseInt(osuInfo.get("AudioLeadIn: "));
    }

    // returns null if no bookmarks, else an int[] containing all
    public int[] getBookmarks() {
	String bookmarksString = osuInfo.get("Bookmarks: ");
	if (bookmarksString == null || bookmarksString.length() == 0)
	    return null;
	String[] bookmarksStringsArray = bookmarksString.split(",");
	int numBookmarks = bookmarksStringsArray.length;
	int[] bookmarks = new int[numBookmarks];
	for (int i = 0; i < numBookmarks; i++) {
	    bookmarks[i] = Integer.parseInt(bookmarksStringsArray[i]);
	}
	return bookmarks;
    }

    public double getSliderMultiplier() {
	return Double.parseDouble(osuInfo.get("SliderMultiplier:"));
    }

    public String getStackLen() {
	return osuInfo.get("StackLeniency: ");
    }

    public String getHitObjectsString() {
	return contents.substring(contents.indexOf("[HitObjects]") + 14,
		contents.length());
    }

    public String getColoursString() {
	return extractBlock("[Colours]", "[HitObjects]");
    }

    public String getTimingPointsString() {
	String result = extractBlock("[TimingPoints]", "[Colours]");
	// if no custom colors, change the paramaters
	return result.length() == 0 ? extractBlock("[TimingPoints]",
		"[HitObjects]") : result;
    }

    public String getEventsString() {
	return extractBlock("[Events]", "[TimingPoints]");
    }

    public String getBreaksString() {
	return extractBlock("//Break Periods",
		"//Storyboard Layer 0 (Background)");
    }

    public ArrayList<HitObject> getHitObjects() {
	return hitObjects;
    }

    public HashMap<HitObject, String> getNotation() {
	return notations;
    }

    public ArrayList<Integer> getBreakStarts() {
	return breakStarts;
    }

    public ArrayList<Integer> getBreakEnds() {
	return breakEnds;
    }

    public int getStartTime() {
	return startTime;
    }

    public int getEndTime() {
	return endTime;
    }

    public Timing getTimer() {
	return timer;
    }

    public OsuFileChecker getOsuFileChecker() {
	return ofc;
    }

    public boolean epilepsyFound() {
	return (osuInfo.get("EpilepsyWarning: ").equals("1"));
    }

    public boolean countdownEnabled() {
	return (!osuInfo.get("Countdown: ").equals("0"));
    }

    // Utility
    // ***************************************************************************

    // Extracts a block out of an .osu file
    private String extractBlock(String start, String end) {
	int beginIndex = contents.indexOf(start);
	int endIndex = contents.indexOf(end);
	if (beginIndex < 0 || endIndex < 0)
	    return "";
	// Adjust so substring cuts properly
	beginIndex += start.length() + 2;
	endIndex -= 2;
	if (beginIndex >= endIndex)
	    return "";
	return contents.substring(beginIndex, endIndex);
    }

    public static void main(String[] args) {
	// Test runtime
	// processhitobj takes a lot of time, ~ 5 seconds
	// - Using split instead of scanner = .6 sec
	// - Sliders are very slow = 2.3 sec
	// timer takes about .7 secs
	// ofc takes almost no time
	// long start = System.currentTimeMillis();
	// Util.logTime(start);
    }

    @Override
    public int compareTo(OsuFileParser o) {
	return getDiff().compareTo(o.getDiff());
    }

}