package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import taskey.constants.UiConstants;

/**
 * @@author A0125419H
 * This class provides a way to configure different styles given a string as input.
 * It builds an array of Text objects to be returned 
 *
 * @author Junwei
 * 
 */

public class UiTextBuilder {
	private ArrayList<Pair<Integer, String>> styleMarkers; 
	private char symbol; 
	
	public UiTextBuilder() {
		initVariables();
	}
	
	public UiTextBuilder(String ...styles) { // for quick styling (primarily for building by symbol
		initVariables();
		addMarkers(styles);
	}
	
	public void initVariables() {
		styleMarkers = new ArrayList<Pair<Integer, String>>(); // where to start certain styles 
		addMarker(0, UiConstants.STYLE_TEXT_BLACK); // default marker, will get overridden if there exists another marker at 0
		symbol = '$'; // default symbol
	}

	public void setSymbol(char _symbol) {
		symbol = _symbol;
	}
	
	public void addMarker(Integer startIndex, String style) {
		assert(startIndex >= 0);
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
	
	/**
	 * This method converts an arraylist of Text objects into a Textflow
	 * for displaying
	 * @param theText
	 * @return
	 */
	public TextFlow convertTextArray(ArrayList<Text> theText) {
		TextFlow element = new TextFlow();
		element.getChildren().addAll(theText);
		return element;
	}
	/**
	 * Builds the textFlow object based on the markers, which mark out certain segments of a text
	 * so that styles can be switched then
	 *
	 * @param line - the line to process
	 * @return - TextFlow object
	 */
	public TextFlow build(String line) {
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
			newText.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
			newText.getStyleClass().add(currentStyle);
			myTexts.add(newText);
			currentStart = currentEnd;
		}
		return convertTextArray(myTexts);
	}
	
	/**
	 * Using a symbol like #, etc to build, reuse markers but not using indexes
	 * Markers are ignored in a queue-like fashion for each symbol encountered.
	 * This is very slow because it goes through each character, but is more convenient
	 *
	 * @param line - line to build text objects from
	 * @return - the text flow object
	 */
	public TextFlow buildBySymbol(String line) {
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
				newText.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
				newText.getStyleClass().add(currentStyle);
				myTexts.add(newText);
				segment = "";
			}
		}
		return convertTextArray(myTexts);
	}
}
