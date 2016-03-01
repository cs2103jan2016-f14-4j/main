package taskey.ui.utility;

import java.util.ArrayList;

import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * This class provides a way to configure different styles given a string
 * 
 * @author Junwei
 *
 */
public class UiTextConfig {

	private ArrayList<Pair<Integer, String>> styleMarkers; 
	private char symbol;

	public UiTextConfig() {
		initVariables();
	}
	public UiTextConfig(String ...styles) { // for quick styling (primarily for formatting by symbol
		initVariables();
		addMarkers(styles);
	}
	public void initVariables() {
		styleMarkers = new ArrayList<Pair<Integer, String>>(); // where to start certain styles 
		addMarker(0, "textBlack"); // default marker, will get overridden if there exists another marker at 0
		symbol = '$'; // default symbol
	}
	public ArrayList<Text> format(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		int currentStart = styleMarkers.get(0).getKey();
		int currentEnd = 0;
		String currentStyle = "";
		String segment = "";

		// start segment
		if (currentStart != 0) {
			segment = line.substring(0, currentStart); // note substring excludes the end index
			Text newText = new Text(segment);
			myTexts.add(newText);
		}

		for (int i = 0; i < styleMarkers.size() && currentEnd < line.length(); i++) { // not end of line yet 
			currentStyle = styleMarkers.get(i).getValue();

			if (i == styleMarkers.size() - 1) { // just set the style for the rest of the string
				currentEnd = line.length();
			} else {
				currentEnd = Math.min(styleMarkers.get(i + 1).getKey(), line.length());
			}
			segment = line.substring(currentStart, currentEnd);
			Text newText = new Text(segment);
			newText.getStyleClass().add(currentStyle);
			myTexts.add(newText);
			currentStart = currentEnd;
		}
		return myTexts;
	}
	
	public void setSymbol(char _symbol) {
		symbol = _symbol;
	}
	/**
	 * Using a symbol like #, etc to format, reuse markers but not using indexes
	 * Markers are ignored in a queue-like fashion for each symbol encountered
	 * @param symbol - #, $ etc
	 * @param line - line to format
	 * @return
	 */
	public ArrayList<Text> formatBySymbol(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		String segment = "";
		int styleIndex = 0;
		String currentStyle = "";
		for (int i = 0; i < line.length(); i++) { // not end of line yet 
			char c = line.charAt(i);
			if ( c != symbol ) {
				segment += c;
			} 
			if ( c == symbol || i == line.length() - 1) { // hit symbol or hit end of line
				if ( styleIndex < styleMarkers.size()) {
					currentStyle = styleMarkers.get(styleIndex).getValue();
					styleIndex++; // ignore previous style
				}
				Text newText = new Text(segment);
				newText.getStyleClass().add(currentStyle);
				myTexts.add(newText);
				segment = "";
			}
		}
		return myTexts;
	}

	public void addMarker(Integer startIndex, String style) {
		styleMarkers.add(new Pair<Integer, String>(startIndex, style));
	}
	public void addMarkers(String ...styles) {
		for ( String style : styles) {
			styleMarkers.add(new Pair<Integer, String>(0, style));
		}
	}

	public void removeMarkers() {
		styleMarkers.clear();
	}
}