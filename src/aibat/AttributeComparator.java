package aibat;
// Passing a method to another method? - command

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//How to use: Instantiate, then call addAttribute(), then count();
public class AttributeComparator {

    private String attributeType;

    // Maps diff names to the (list of?) attributes of that diff
    private Map sources;

    // Keeps a count of the instances of each attribute.
    private Map<String, Integer> attributeCount = new TreeMap<String, Integer>();

    // Set to true if each diff should only have one attribute
    private boolean isAttributeUnique;

//    public AttributeComparator(String attributeType) {
//	this(attributeType, false);
//    }

    public AttributeComparator(String attributeType, boolean isAttributeUnique) {
	this.attributeType = attributeType;
	this.isAttributeUnique = isAttributeUnique;
	if (isAttributeUnique) {
	    sources = new TreeMap<String, String>();
	}
	else {
	    sources = new TreeMap<String, List<String>>();
	}
    }

    public String getAttributeType() {
	return attributeType;
    }

    // For multi-attribute use
    public void addAttributes(String source, List<String> attributes) {
	sources.put(source, attributes);
    }

    // For single-attribute use
    public void addAttribute(String source, String attribute) {
	sources.put(source, attribute);
    }

    // Cycles through all the sources and passes their name and listing to a
    // command.
    private void process(Command command) {
	Set<String> sourcesKeySet = sources.keySet();
	for (String source : sourcesKeySet) {
	    if (isAttributeUnique) {
		command.executeUnique(source, (String) sources.get(source));
	    }
	    else {
		command.execute(source, (List<String>) sources.get(source));
	    }
	}
    }

    // Accessable method to run, after all attributes added.
    public void count() {
	process(new CountAttributes());
    }

    // Returns the names of the diffs missing certain attributes
    // Sample result:
    // "Happy" not found in Easy and Hard and Insane
    // "Hardcore" only found in Hard
    public String compareAttributes() {
	String result = "";
	int totalSources = sources.size();
	
	Set<String> attributeCountKeySet = attributeCount.keySet();
	for (String attribute : attributeCountKeySet) {
	    int count = attributeCount.get(attribute);
	    if (count < totalSources) {
		System.out.println(totalSources + "*" + count);//TODO remove
		if (count >= totalSources / 2)
		    result += sourcesMissingAttribute(attribute);
		else
		    result += sourcesHavingAttribute(attribute);
	    }
	}
	return result;
    }

    private String sourcesHavingAttribute(String attribute) {
	CheckAttribute ha = new CheckAttribute(attribute, true);
	process(ha);
	return ha.printResult();
    }

    // sample result: Easy and Hard and Insane
    private String sourcesMissingAttribute(String attribute) {
	CheckAttribute ha = new CheckAttribute(attribute, false);
	process(ha);
	return ha.printResult();
    }

    private interface Command {
	public void execute(String source, List<String> attributes);

	public void executeUnique(String source, String attribute);
    }

    private class CountAttributes implements Command {

	@Override
	public void executeUnique(String source, String attribute) {
	    countAttribute(attribute);
	}

	@Override
	public void execute(String source, List<String> attributes) {
	    for (String attribute : attributes) {
		countAttribute(attribute);
	    }
	}

	private void countAttribute(String attribute) {
	    if (attributeCount.containsKey(attribute)) {
		int count = attributeCount.get(attribute);
		// TODO check if put will override
		attributeCount.put(attribute, ++count);
	    }
	    else {
		attributeCount.put(attribute, 1);
	    }
	}
    }

    private class CheckAttribute implements Command {

	private boolean checkForHas;//If false, checks for missing

	private String attribute;

	private List<String> validSources = new ArrayList<String>();

	public CheckAttribute(String attribute, boolean checkForHas) {
	    this.attribute = attribute;
	    this.checkForHas = checkForHas;
	}

	@Override
	public void execute(String source, List<String> attributes) {
	    if (attributes.contains(attribute) == checkForHas) {
		validSources.add(source);
	    }
	}

	@Override
	public void executeUnique(String source, String attribute) {
	    if ((this.attribute == attribute) == checkForHas)
		validSources.add(source);
	}

	public String printResult() {
	    StringBuilder result = new StringBuilder();
	    result.append("\"" + attribute + "\" ");
	    result.append(checkForHas? "only" : "not");
	    result.append(" found in ");
	    //result.append( sourcesMissingAttribute(attribute));
	    switch (validSources.size()) {
	    case 0:
		break;
	    case 1:
		result.append( validSources.get(0));
		break;
	    default:
		StringBuilder temp = new StringBuilder();
		for (String source : validSources) {
		    temp.append(" and " + source);
		}
		result.append( temp.toString().substring(5));
		break;
	    }
	    result.append("\n");
	    return result.toString();
	}
    }

    public static void main(String[] args) {
	long start = System.currentTimeMillis();
	AttributeComparator a = new AttributeComparator("Tag", true);
	System.out.println(a.getAttributeType());
	a.addAttribute("Easy", "Tag1");
	a.addAttribute("Med", "Tag3");
	a.addAttribute("Hard", "Tag3");
	a.addAttribute("Hard", "Tag4");
	a.addAttribute("Harder", "Tag1");
	a.count();
	System.out.println(a.compareAttributes());
	Util.logTime(start);
    }
}
