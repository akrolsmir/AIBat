package aibat;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import modtrace.OsuFilesCopier;

import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;

// Puts things together for the General and All tabs.
public class Consolidator {
    private static final double MAX_FILE_SIZE_VID = 24.0,
	    MAX_FILE_SIZE_NOVID = 10.0, BYTES_PER_MB = 1024.0 * 1024.0;

    private static final int MAX_BITRATE = 192, MIN_BITRATE = 128;

    private static final long MIN_SAFE_WAV = 44;

    private ArrayList<File> osuFiles = new ArrayList<File>(),
	    soundFiles = new ArrayList<File>(),
	    osbFiles = new ArrayList<File>();

    private ArrayList<OsuFileParser> osuFileParsers = new ArrayList<OsuFileParser>();;

    private HashSet<File> musicFiles = new HashSet<File>();; // TODO figure out
							     // what the heck
							     // this
    // should be

    private boolean hasVid, epilepsyWarningFound = false;

    private File dir;

    private String redPointsCheck = "", coloursCheck = "", kiaiCheck = "";

    private SkinSBChecker skinSBChecker;

    public Consolidator(File directory) {
	dir = directory;
	// imageFiles = new HashSet<File>();
	for (File file : Util.getFiles(dir)) {
	    String ext = Util.getExtension(file);
	    // This takes the most time...
	    if (ext.equals("osu")) {
		osuFiles.add(file);
		osuFileParsers.add(new OsuFileParser(file));
	    }
	    else if (ext.equals("osb")) {
		osbFiles.add(file);
	    }
	    // else if ( ext.equals( "png" ) || ext.equals( "jpg" )
	    // || ext.equals( "jpeg" ) )
	    // {
	    // imageFiles.add( file );
	    // }
	    else if (ext.equals("mp3") || ext.equals("ogg")) {
		musicFiles.add(file);
	    }
	    else if (ext.equals("wav")) {
		soundFiles.add(file);
	    }
	    else if (ext.equals("flv") || ext.equals("avi")) {
		hasVid = true;
	    }
	}
	checkRedColourKiaiEpilepsy();
	skinSBChecker = new SkinSBChecker(dir, osuFileParsers, osbFiles,
		epilepsyWarningFound);
	// TODO remove
	new OsuFilesCopier(osuFiles);
    }

    public String checkGenMeta() {
	String result = "";
	String[] titles = OsuFileParser.TITLES;
	for (int i = 0; i < titles.length; i++) {
	    // Skip for sample set, mode, difficulty name (aka version), stack
	    // leniency
	    // if ( i == OsuFileParser.SAMPLESET_LOC
	    // || i == OsuFileParser.MODE_LOC || i == OsuFileParser.DIFF_LOC
	    // || i == OsuFileParser.STACK_LOC )
	    // continue;
	    switch (i) {
	    case OsuFileParser.SAMPLESET_LOC:
	    case OsuFileParser.MODE_LOC:
	    case OsuFileParser.DIFF_LOC:
	    case OsuFileParser.STACK_LOC:
	    case OsuFileParser.BEATDIV_LOC:
		continue;
	    default:
		if (!checkElement(i)) {
		    result += "\nInconsistency in " + titles[i] + "\n";
		    for (OsuFileParser o : osuFileParsers) {
			result += "  - " + o.getDiffBoxed() + " : "
				+ o.getGenMeta()[i] + "\n";
		    }
		}
	    }
	}
	return result;
    }

    // Suggests tags if there are none, or a name is missing
    public String checkTags() {
	String result = "";
	ArrayList<String> guests = new ArrayList<String>();
	HashSet<String> tags = new HashSet<String>();
	for (OsuFileParser o : osuFileParsers) {
	    int a = o.getDiff().indexOf("'");
	    // if there's an apostrophe in the diff name
	    if (a > 0)
		guests.add(o.getDiff().substring(0, a));
	    // Add all tags, in lowercase.
	    for (String t : o.getTags().split(" "))
		if (t.length() > 0)
		    tags.add(t.toLowerCase());
	}

	int size = tags.size();
	if (size == 0)
	    result += "There are no tags, consider adding some.\n";
	for (String g : guests) {
	    tags.add(g.toLowerCase());
	    int newSize = tags.size();
	    // if g is not in tags
	    if (newSize > size)
		result += "  - Consider adding \"" + g
			+ "\" to all of your tags\n";
	    size = newSize;
	}
	return result;
    }

    // Checks for consistency in red timing sections
    private void checkRedColourKiaiEpilepsy() {
	HashSet<String> redPoints = new HashSet<String>();
	HashSet<String> colours = new HashSet<String>();
	HashSet<String> kiai = new HashSet<String>();
	for (OsuFileParser o : osuFileParsers) {
	    redPoints.add(o.getTimer().redPointsString());
	    colours.add(o.getColoursString());
	    kiai.add(o.getTimer().getKiaiTimes());
	    if (o.epilepsyFound())
		epilepsyWarningFound = true;
	}
	if (redPoints.size() > 1)
	    redPointsCheck += "Inconsistency in uninherited (red) timing sections:\n";
	if (colours.size() > 1)
	    coloursCheck += "Inconsistency in combo colours:\n";
	if (kiai.size() > 1)
	    kiaiCheck += "Inconsistency in Kiai Times:\n";
	for (OsuFileParser o : osuFileParsers) {
	    if (redPointsCheck.length() > 0)
		redPointsCheck += "\n  - " + o.getDiffBoxed()
			+ "\nRed timing sections at:\n"
			+ o.getTimer().redPointsString();
	    if (coloursCheck.length() > 0) {
		coloursCheck += "\n  - " + o.getDiffBoxed() + " :\n";
		if (o.getColoursString() == null)
		    coloursCheck += "No custom colours.\n";
		else
		    coloursCheck += o.getColoursString();
	    }
	    String k = o.getTimer().getKiaiTimes();
	    if (kiaiCheck.length() > 0) {
		kiaiCheck += "\n  - " + o.getDiffBoxed() + " :\n" + k;
	    }
	}
    }

    public String getRedPointsCheck() {
	return redPointsCheck;
    }

    public String getColoursCheck() {
	return coloursCheck;
    }

    public String getKiaiCheck() {
	return kiaiCheck;
    }

    // Recursive method that gets the size of d
    private long addFolderSize(File d) {
	long total = d.length();
	if (d.isDirectory())
	    if (d.listFiles() != null)
		for (File f : d.listFiles())
		    total += addFolderSize(f);
	return total;
    }

    public String checkFolderSize() {
	String result = "";
	double sizeInMB = addFolderSize(dir) / (BYTES_PER_MB);
	DecimalFormat toTenths = new DecimalFormat("0.0");
	if (hasVid && sizeInMB > MAX_FILE_SIZE_VID) {
	    result += "This folder's size is "
		    + toTenths.format(sizeInMB)
		    + " MB, which is greater than the allowed size of 20.0 MB with video.\n";
	}
	else if (!hasVid && sizeInMB > MAX_FILE_SIZE_NOVID) {
	    result += "This folder's size is "
		    + toTenths.format(sizeInMB)
		    + " MB, which is greater than the allowed size of 10.0 MB without video.\n";
	}
	return result;
    }

    public String checkBitrate() {
	// TODO look at skinSBChecker.checkBG()
	StringBuilder result = new StringBuilder();
	for (File song : musicFiles) {
	    try {
		int bitrate = getBitrate(song);
		if (bitrate < MIN_BITRATE)
		    result.append("The bitrate for "
			    + Util.cutPath(song.toString()) + " is " + bitrate
			    + " kb/s, which is below the miniumum of "
			    + MIN_BITRATE + " kb/s.\n");
		else if (bitrate > MAX_BITRATE)
		    result.append("The bitrate for "
			    + Util.cutPath(song.toString()) + " is " + bitrate
			    + " kb/s, which is above the maximum of "
			    + MAX_BITRATE + " kb/s.\n");
	    }
	    catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return result.toString();
    }

    public String checkCustomHitsounds() {
	StringBuilder result = new StringBuilder();
	for (File song : soundFiles) {
	    if (song.length() < MIN_SAFE_WAV)
		result.append(Util.cutPath(song.toString()) + " is only "
			+ song.length()
			+ " bytes large. Please use the blank.wav which is "
			+ MIN_SAFE_WAV + " bytes large.\n");
	}
	return result.toString();
    }

    // Getters + Util **********************************************************
    // true if all elements are the same
    private boolean checkElement(int i) {
	HashSet<String> elementSet = new HashSet<String>();
	for (OsuFileParser o : osuFileParsers) {
	    elementSet.add(o.getGenMeta()[i]);
	    if (elementSet.size() > 1)
		return false;
	}
	return true;
    }

    // public String toString()
    // {
    // String result = dir + "\n";
    // for ( OsuFileParser o : osuFiles )
    // result += o.getDiff() + "\n";
    // return result;
    // }

    public ArrayList<OsuFileParser> getOsuFiles() {
	return osuFileParsers;
    }

    public SkinSBChecker getSkinSBChecker() {
	return skinSBChecker;
    }

    public static void main(String[] args) {
	Consolidator c2 = new Consolidator(
		new File(
			"C:\\Users\\Akrolsmir\\Desktop\\Gaming Programs\\osu!\\Songs\\27625 La Luna - Take Me (Nightcore Mix)"));
	System.out.println("done");

    }

    private static int getBitrate(File file)
	    throws UnsupportedAudioFileException, IOException {
	AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	AudioFormat format = fileFormat.getFormat();
	if (fileFormat instanceof TAudioFileFormat) {
	    Map<?, ?> properties = ((TAudioFormat) format).properties();
	    return (Integer) properties.get("bitrate") / 1000;
	}
	else {
	    throw new UnsupportedAudioFileException();
	}
    }

    // Check
    // http://stackoverflow.com/questions/3046669/how-do-i-get-a-mp3-files-total-time-in-java
    // private static void dosth(File file) throws
    // UnsupportedAudioFileException, IOException {
    //
    // AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
    // if (fileFormat instanceof TAudioFileFormat) {
    // Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
    // String key = "duration";
    // Long microseconds = (Long) properties.get(key);
    // int mili = (int) (microseconds / 1000);
    // int sec = (mili / 1000) % 60;
    // int min = (mili / 1000) / 60;
    // System.out.println("time = " + min + ":" + sec);
    // } else {
    // throw new UnsupportedAudioFileException();
    // }
    //
    // }
}