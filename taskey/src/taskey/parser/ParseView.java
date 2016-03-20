package taskey.parser;

import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject; 

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "view" command 
 * @author Xue Hui
 *
 */
public class ParseView extends ParseCommand {
	private HashMap<String,String> viewList = new HashMap<String,String>();
	private UserTagDatabase tagDB = null; 

	public ParseView(UserTagDatabase tagDB) {
		super();
		this.tagDB = tagDB; 
		
		viewList.put("all", "all"); 
		viewList.put("general", "general");
		viewList.put("deadlines", "deadlines");
		viewList.put("events", "events"); 
		viewList.put("archive", "archive"); 
		viewList.put("help", "help"); 
	}
	
	/**
	 * If the command is view, process what kind of view it is:
	 * 1. ALL
	 * 2. GENERAL
	 * 3. DEADLINES
	 * 4. EVENTS 
	 * 5. ARCHIVE
	 * 6. HELP 
	 * 7. #tags 
	 * @param command
	 * @param stringInput
	 * @return processedStuff
	 */
	public ProcessedObject processView(String command, String stringInput) {
		String rawView = getViewRaw(command, stringInput); 
		String viewType = getViewType(command, stringInput);
		
		//empty view
		if (rawView.compareTo("") == 0) {
			return super.processError(ParserConstants.ERROR_VIEW_EMPTY);
		}
		
		if (viewType.compareTo("error") != 0) {
			return new ProcessedObject("VIEW",viewType.toUpperCase());
		}
		//no such category 
		return super.processError(String.format(
				ParserConstants.ERROR_VIEW_TYPE, rawView)); 
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
		String temp = stringInput.toLowerCase(); 
		String viewType = temp.replaceFirst(command, "");
		viewType = viewType.toLowerCase();
		viewType = viewType.trim(); 
		
		if (viewList.containsKey(viewType)) {
			return viewType; 
		} else if (tagDB.hasTag(viewType)) {
			return viewType; 
		}
		return "error"; 
	}
	
	/**
	 * Get raw View for a user input
	 * @param command
	 * @param stringInput
	 * @return
	 */
	public String getViewRaw(String command, String stringInput) {
		String temp = stringInput.toLowerCase(); 
		String viewType = temp.replaceFirst(command, "");
		viewType = viewType.toLowerCase();
		viewType = viewType.trim(); 

		return viewType; 
	}

}
