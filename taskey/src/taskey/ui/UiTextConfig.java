package taskey.ui;

import java.util.ArrayList;

import javafx.scene.text.Text;
import javafx.util.Pair;

public class UiTextConfig {

	ArrayList<Pair<Integer,String>> styleMarkers = new ArrayList<Pair<Integer,String>>(); // where to start certain styles

	public ArrayList<Text> format(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		int currentStart = styleMarkers.get(0).getKey();
		int currentEnd = 0;
		String currentStyle = "";
		String segment = "";
		
		// start segment
		if ( currentStart != 0 ) {
			segment = line.substring(0,currentStart); // note substring excludes the end index
			Text newText = new Text(segment);
			myTexts.add(newText);
		}
		
		for ( int i = 0; i < styleMarkers.size() && currentEnd < line.length(); i++ ) { // not end of line yet
			currentStyle = styleMarkers.get(i).getValue();
			
			if ( i == styleMarkers.size() - 1) { // just set the style for the rest of the string
				currentEnd = line.length();
			}
			else {
				currentEnd = Math.min(styleMarkers.get(i+1).getKey(), line.length());
			}
			segment = line.substring(currentStart,currentEnd);
			Text newText = new Text(segment);
			newText.getStyleClass().add(currentStyle);
			myTexts.add(newText);
			currentStart = currentEnd; 
		}

		return myTexts;
	}
	
	public void addMarker(Integer startIndex, String style ) {
		styleMarkers.add(new Pair<Integer,String>(startIndex,style));
	}
}
