package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject; 

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "view" command 
 * Parse as: VIEW_BASIC and VIEW_TAGS
 * @author Xue Hui
 *
 */
public class ParseView extends ParseCommand {
	private HashMap<String,String> viewList = new HashMap<String,String>();

	public ParseView() {
		super(); 
		
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
		String stringWithoutCommand = getStringWithoutCommand(command, stringInput);
		
		//empty view
		if (stringWithoutCommand.compareTo("") == 0) {
			return super.processError(ParserConstants.ERROR_VIEW_EMPTY);
		}
		
		//let logic handle if the view exists or not.  
		return getView(stringWithoutCommand); 
	}
	
	/**
	 * @param command
	 * @param stringInput
	 * @return string without the "view" command attached to it. 
	 */
	public String getStringWithoutCommand(String command, String stringInput) {
		return stringInput.replace(command, "").trim().toLowerCase();
	}
	
	/**
	 * Get all the categories that the user wants to view 
	 * @param command
	 * @param stringInput
	 * @return ProcessedObject of the processed VIEW
	 */
	public ProcessedObject getView(String stringInput) {
		ArrayList<String> views = new ArrayList<String>(); 
		
		if (stringInput.contains("#")) {
			String[] split = stringInput.split("#"); 
			for(int i = 0; i < split.length; i++) {
				views.add(split[i].trim()); 
			}
			//LOGIC to check if the tags are valid/exists 
			return new ProcessedObject("VIEW_TAGS", views); 
		} else {
			String[] split = stringInput.split(" "); 
			for(int i = 0; i < split.length; i++) {
				String category = split[i].trim(); 
				if (viewList.containsKey(category)) {
					views.add(category); 
				} else {
					return super.processError(String.format(
							ParserConstants.ERROR_VIEW_TYPE, category));
				}
			}
			return new ProcessedObject("VIEW_BASIC", views); 
		}

	}

}
