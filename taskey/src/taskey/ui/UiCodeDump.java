package taskey.ui;

/**
 * For code that may be reused or provide ideas
 * @author junwei
 *
 */
public class UiCodeDump {

	/*
	// In UiContentFormatter
	public ArrayList<Text> convertStringToTextNodes(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		String modifier = "#000000";
		for ( int i = 0; i < line.length(); i ++ ) {
			String myChar = String.valueOf(line.charAt(i));
			if ( myChar.equals("<")) {
				modifier = "";
				i++;
				myChar = String.valueOf(line.charAt(i));
				while ( myChar.equals(">") == false ) {
					modifier += myChar;	
					i++;
					myChar = String.valueOf(line.charAt(i));
				}
				i++;
				myChar = String.valueOf(line.charAt(i));
			}
			Text newText = new Text(myChar);
			newText.setFill(Color.web(modifier));
			myTexts.add(newText);
		}
		return myTexts;
	}
	
	
	
	
	
	
	
	
	
	 */
}
