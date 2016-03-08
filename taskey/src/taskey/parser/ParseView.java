package taskey.parser;

import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject; 

/**
 * Purpose of this class is to parse the "view" command 
 * @author Xue Hui
 *
 */
public class ParseView {
	private HashMap<String,String> viewList = new HashMap<String,String>();
	private UserTagDatabase tagDB = null; 
	private ParseError parseError = new ParseError(); 
	
	public ParseView(UserTagDatabase tagDB) {
		this.tagDB = tagDB; 
		
		viewList.put("all", "all"); 
		viewList.put("general", "general");
		viewList.put("deadlines", "deadlines");
		viewList.put("events", "events"); 
		viewList.put("archive", "archive"); 
	}
	
	/**
	 * If the command is view, process what kind of view it is:
	 * 1. ALL
	 * 2. GENERAL
	 * 3. DEADLINES
	 * 4. EVENTS 
	 * 5. ARCHIVE
	 * @param command
	 * @param stringInput
	 * @return processedStuff
	 */
	public ProcessedObject processView(String command, String stringInput) {
		String viewType = getViewType(command, stringInput);
		
		if (viewType.compareTo("error") != 0) {
			return new ProcessedObject("VIEW",viewType.toUpperCase());
		}
		return parseError.processError(ParserConstants.ERROR_VIEW_TYPE); 
	}
	
	/**
	 * Get viewType all, general, events or deadlines, or
	 * gets a user defined viewtype (based on their current list of available tags)
	 * or returns error 
	 * @param command
	 * @param stringInput
	 * @return string view type 
	 */
	public String getViewType(String command, String stringInput) {
		stringInput = stringInput.toLowerCase(); 
		String viewType = stringInput.replaceFirst(command, "");
		viewType = viewType.toLowerCase();
		viewType = viewType.trim(); 
		
		if (viewList.containsKey(viewType)) {
			return viewType; 
		} else if (tagDB.hasTag(viewType)) {
			return viewType; 
		}
		return "error"; 
	}

}
