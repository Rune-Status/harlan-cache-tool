package net.openrs.cache.cs2;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author Harlan
 *
 */
public class CodeFormatter {
	public CodeFormatter() {
		lines.add("//Dumped By Harlan - CS2 for 498");
	}

	private final List<String> lines = new LinkedList<String>();

	private int tabIndex = 0;

	public void parse(String line) {
		if (line.contains("}")) {
			tabIndex--;
		}
		StringBuilder newString = new StringBuilder();
		for (int i = 0; i < tabIndex; i++) {
			newString.append("	");//tab
		}
		newString.append(line);
		lines.add(newString.toString());
		if (line.contains("{")) {
			tabIndex++;
		}
	}
	/**
	 * Publishes the code into a print stream.
	 * @param stream
	 */
	public void publish(PrintStream stream) {
		for (String s : lines) {
			stream.println(s);
		}
	}

}
