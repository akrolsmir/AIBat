package aibat;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import skin.*;

public class SkinSBChecker {
    private final static int NUM_EVENT_SECTIONS = 8, BG_WIDTH = 1024,
	    BG_HEIGHT = 768;

    // Because some thing in Events under osu aren't in the osb
    private final static int[] osbEvents = { 0, 2, 3, 4, 5, 6 };

    private File dir;

    private ArrayList<OsuFileParser> osuFileParsers;

    private ArrayList<File> osbFiles;

    private Map<String, BufferedImage> imageFiles;

    private String missingSkin, skinProblems;

    private Set<String> unusedImages, imageFileNames;

    // These are identified by presence in code, not in folder
    private Set<String> bgNames, codedElements, codelessElements;

    private String path;

    // The 8 sections go like this:
    // 0 - Background and Video events
    // 1 - Break Periods
    // 2 - Storyboard Layer 0 (Background)
    // 3 - Storyboard Layer 1 (Fail)
    // 4 - Storyboard Layer 2 (Pass)
    // 5 - Storyboard Layer 3 (Foreground)
    // 6 - Storyboard Sound Samples
    // 7 - Background Colour Transformations
    private ArrayList<String>[] allEvents;

    private boolean hasEpilepsyWarning;

    public SkinSBChecker(File directory,
	    ArrayList<OsuFileParser> osuFileParsers,
	    ArrayList<File> inputOsbFiles, boolean epilepsyWarningFound) {
	dir = directory;
	this.osuFileParsers = osuFileParsers;
	osbFiles = inputOsbFiles;
	hasEpilepsyWarning = epilepsyWarningFound;

	path = dir.getAbsolutePath().toLowerCase() + "\\";
	imageFiles = new HashMap<String, BufferedImage>();
	codedElements = new HashSet<String>();
	codelessElements = new HashSet<String>();

	long start = System.currentTimeMillis();
	ArrayList<File> allFiles = Util.getAllFiles(dir);
	for (File file : allFiles) {
	    String ext = Util.getExtension(file);
	    if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg")) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(file);
		} catch (IOException e) {
		    Util.errorException(e);
		}
		imageFiles.put(file.getAbsolutePath().toLowerCase(), img);
	    }
	}
	Util.logTime(start);

	imageFileNames = new HashSet<String>(imageFiles.keySet());
	unusedImages = new HashSet<String>(imageFiles.keySet());
	initializeAllEvents();
	initializeBGNames();
	initializeSBElements();
	checkSkin();
	animationKiller();
	// for ( int i = 0; i < NUM_EVENT_SECTIONS; i++ )
	// {
	// for(String s : allEvents[i])
	// System.out.println(s);
	// }
    }

    public String checkBG() {
	if (bgNames.size() == 0)
	    return "Please include a background (no backgrounds were found).\n";
	String result = "";
	for (String name : bgNames) {
	    if (imageFiles.containsKey(name)) {
		BufferedImage bg = imageFiles.get(name);
		int w = bg.getWidth(), h = bg.getHeight();
		if (w != BG_WIDTH || h != BG_HEIGHT) {
		    result += cutPath(name) + " is " + w + "x" + h
			    + ", which differs from the recommended size of "
			    + BG_WIDTH + "x" + BG_HEIGHT + ".\n";
		}
	    } else {
		result += "The background file \"" + cutPath(name)
			+ "\" is missing.\n";
	    }
	}
	return result;
    }

    private void initializeAllEvents() {
	allEvents = new ArrayList[NUM_EVENT_SECTIONS];
	for (int i = 0; i < NUM_EVENT_SECTIONS; i++)
	    allEvents[i] = new ArrayList<String>();

	for (File osb : osbFiles) {
	    for (int i = 0; i < osbEvents.length; i++)
		allEvents[osbEvents[i]]
			.addAll(eventsParser(Util.readFile(osb))[i]);
	}

	for (OsuFileParser ofp : osuFileParsers) {
	    // System.out.println( eventsParser( ofp.getEventsString() ) );
	    for (int i = 0; i < NUM_EVENT_SECTIONS; i++)
		allEvents[i].addAll(eventsParser(ofp.getEventsString())[i]);
	}
    }

    private void initializeBGNames() {
	bgNames = new HashSet<String>();
	for (String line : allEvents[0]) {
	    // If String before first comma is an int, then add the thing
	    // between quotes.
	    try {
		Integer.parseInt(line.split(",")[0]);
		String imageName = path + line.split("\"")[1];
		imageName = imageName.toLowerCase();
		bgNames.add(imageName);
		unusedImages.remove(imageName);
	    } catch (NumberFormatException nfe) {
		// 1st number not an int = not a reference to BG;
	    }
	}
    }

    private void initializeSBElements() {
	// for the lines in allEvents that correspond to storyboarding
	// hangs at i = 5;
	for (int i = 2; i < 6; i++) {
	    ArrayList<String> lines = allEvents[i];
	    for (int j = 0; j < lines.size(); j++) {
		String line = lines.get(j);
		if (isElement(line)) {
		    String imageName = path + line.split("\"")[1];
		    imageName = imageName.toLowerCase();
		    // triggers if j is the last, or the next line is also an
		    // element
		    if (j + 1 >= lines.size() || isElement(lines.get(j + 1))) {
			codelessElements.add(imageName);
		    } else {
			codedElements.add(imageName);
			unusedImages.remove(imageName);
		    }
		}
	    }
	}
    }

    private void animationKiller() {
	// For each element in codedElements
	Pattern p = Pattern.compile("\\d*");
	for (Iterator<String> ce = codedElements.iterator(); ce.hasNext();) {
	    String c1 = ce.next();
	    String c = Util.cutExt(c1);
	    // Check each item in unusedImages
	    for (Iterator<String> ui = unusedImages.iterator(); ui.hasNext();) {
		String u = Util.cutExt(ui.next());
		// If it u follows the format c0, c1 etc., remove.
		if (u.indexOf(c) == 0
			&& p.matcher(u.substring(c.length())).matches()) {
		    ui.remove();
		    // So that it won't be counted missing:
		    imageFileNames.add(c1);
		}
	    }
	}
    }

    // Checks for missing skin files as well as improperly sized skin files.
    // TODO doublecheck, shorten.
    private void checkSkin() {
	StringBuilder allAbsent = new StringBuilder();
	StringBuilder problems = new StringBuilder();
	Spec[][] sk = SkinSpecs.ALL_SPECS;

	// iterates through for everything in ALL_SPECS, and OTHER when r = -1.
	for (int r = -1; r < sk.length; r++) {
	    StringBuilder absent = new StringBuilder();
	    int numMissing = 0;
	    Spec[] set;

	    // sk[r] is the set, sk[r][c] is the element in the set
	    if (r == -1)
		set = SkinSpecs.Other;
	    else
		set = sk[r];
	    for (int c = 0; c < set.length; c++) {
		// Spec spec = sk[r][c]; TODO optimize?
		String name = set[c].getName();

		// Checks if it was animated (had a -#.png)
		Pattern p;
		if (name.equals("sliderb.png")) {
		    p = Pattern.compile("\\d*");
		} else {
		    p = Pattern.compile("\\-\\d*");
		}
		boolean animated = false;

		if (set[c].isAnimatable()) {
		    for (Iterator<String> ui = unusedImages.iterator(); ui
			    .hasNext();) {
			String unusedImage = ui.next();
			String s = Util.cutPath(Util.cutExt(unusedImage));
			String n = Util.cutExt(name);
			// System.out.println(s + " " + s.indexOf( n ) + n);
			if (s.indexOf(n) == 0
				&& p.matcher(s.substring(n.length())).matches()) {
			    animated = true;
			    ui.remove();
			    // TODO simplify later, this is a duplicate
			    BufferedImage img = imageFiles.get(unusedImage);
			    // see isFine and whyNotFine in Spec.
			    if (!set[c].isFine(name, img.getWidth(),
				    img.getHeight())) {
				problems.append(set[c].whyNotFine(name,
					img.getWidth(), img.getHeight()));
			    }
			}
		    }
		}

		if (r > -1 && !imageFileNames.contains(path + name)
			&& !animated) {
		    absent.append(name + "\n");
		    numMissing++;
		}
		if (imageFileNames.contains(path + name)) {
		    unusedImages.remove(path + name);
		    BufferedImage img = imageFiles.get(path + name);
		    // see isFine and whyNotFine in Spec.
		    if (!set[c].isFine(name, img.getWidth(), img.getHeight())) {
			problems.append(set[c].whyNotFine(name, img.getWidth(),
				img.getHeight()));
		    }
		}
	    }
	    // triggers if some of the files in the set were found
	    if (r > -1 && numMissing != set.length) {
		allAbsent.append(absent);
	    }
	}
	missingSkin = allAbsent.toString();
	skinProblems = problems.toString();
    }

    public String checkMultipleOsb() {
	if (osbFiles.size() > 1)
	    return "There should only be one .osb file in a folder (this folder has "
		    + osbFiles.size() + ").\n";
	return "";
    }

    public String checkCodelessElements() {
	return listElements(codelessElements);
    }

    public String checkUnusedImages() {
	return listElements(unusedImages);
    }

    public String checkMissingElements() {
	Set<String> missingElementsSet = new HashSet<String>();
	missingElementsSet.addAll(codedElements);
	missingElementsSet.removeAll(imageFileNames);
	return listElements(missingElementsSet);
    }

    // Takes a set, and prints out the elements to a string, alphabetized
    private String listElements(Set<String> set) {
	ArrayList<String> list = new ArrayList<String>();
	list.addAll(set);
	Collections.sort(list);

	StringBuilder result = new StringBuilder();
	for (String s : list) {
	    result.append(cutPath(s) + "\n");
	}
	return result.toString();
    }

    public String checkPNG() {
	StringBuilder result = new StringBuilder();
	for (String s : codedElements) {
	    if (imageFileNames.contains(s)
		    && !Util.getExtension(s).equals("png")) {
		// System.out.println( Util.getExtension( s ) );
		result.append(cutPath(s) + "\n");
	    }
	}
	return result.toString();
    }

    public String suggestEpilepsy() {
	if (codedElements.size() > 0 && !hasEpilepsyWarning)
	    return "A storyboard was detected, you may want to include an epilepsy warning.\n";
	return "";
    }

    // Checks if a certain line references an SB element- if it doesn't have a
    // space in front of it.
    private boolean isElement(String line) {
	return line != null && line.length() > 0 && line.charAt(0) != ' ';
    }

    // Takes a block of events, and parses it into an array each containing a
    // list of lines.
    private List<String>[] eventsParser(String events) {
	// initialize parsed
	ArrayList<String>[] parsed = new ArrayList[NUM_EVENT_SECTIONS];
	for (int i = 0; i < NUM_EVENT_SECTIONS; i++)
	    parsed[i] = new ArrayList<String>();

	Scanner sc = new Scanner(events);
	int i = -1;
	while (sc.hasNextLine()) {
	    String line = sc.nextLine();
	    if (line.indexOf("//") == 0)
		i++;
	    else if (i >= 0)
		parsed[i].add(line);
	}
	return parsed;
    }

    private String cutPath(String nameWithPath) {
	if (nameWithPath.indexOf(path) == 0) {
	    return nameWithPath.substring(path.length());
	}
	return nameWithPath;
    }

    public String getMissingSkin() {
	return missingSkin;
    }

    public String getSkinProblems() {
	return skinProblems;
    }
}
