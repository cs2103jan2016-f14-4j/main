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
	//private HashMap<String,String> viewList = new HashMap<String,String>();

	public ParseView() {
		super(); 
		/*
		viewList.put("all", "all"); 
		viewList.put("general", "general");
		viewList.put("deadlines", "deadlines");
		viewList.put("events", "events"); 
		viewList.put("archive", "archive"); 
		viewList.put("help", "help"); 
		*/ 
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
		
		//empty view
		if (rawView.compareTo("") == 0) {
			return super.processError(ParserConstants.ERROR_VIEW_EMPTY);
		}
		
		//let logic handle if the view exists or not.  
		return new ProcessedObject("VIEW", rawView); 
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
		viewType = viewType.toUpperCase();
		viewType = viewType.trim(); 

		return viewType; 
	}

}
