package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedObject; 

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
		viewList.put("high", "high");
		viewList.put("medium", "medium"); 
		viewList.put("low", "low"); 
		viewList.put("today", "today");
		viewList.put("tomorrow", "tomorrow"); 
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
	 * 8. Priority: high, medium, low
	 * @param command
	 * @param stringInput
	 * @return processedStuff
	 */
	protected ProcessedObject processView(String stringInput) {
		assert(stringInput != null); 
		
		String stringWithoutCommand = getStringWithoutCommand(stringInput);
		
		//empty view
		if (stringWithoutCommand.compareTo("") == 0) {
			return super.processError(ParserConstants.ERROR_VIEW_EMPTY);
		}
		
		//note: let logic handle if the view exists or not.  
		return getView(stringWithoutCommand); 
	}
	
	/**
	 * @param command
	 * @param stringInput
	 * @return string without the "view" command attached to it. 
	 */
	private String getStringWithoutCommand(String stringInput) {
		String command = stringInput.split(" ")[0]; 
		return stringInput.replace(command, "").trim().toLowerCase();
	}
	
	/**
	 * Get all the categories that the user wants to view 
	 * @param command
	 * @param stringInput
	 * @return ProcessedObject of the processed VIEW
	 */
	private ProcessedObject getView(String stringInput) {
		ArrayList<String> views = new ArrayList<String>(); 
		String[] split = stringInput.split(" ");
		String category; 
		
		//if only one view 
		if (split.length == 1) {
			category = split[0].trim().toLowerCase();
			
			//check if it is a tagged view: Logic to handle if tag doesnt exist
			if (category.startsWith("#")) {
				views.add(category.replace("#", "").trim());
				return new ProcessedObject("VIEW_TAGS", views); 
			} else {
				if (viewList.containsKey(category)) {
					views.add(category);
					return new ProcessedObject("VIEW_BASIC", views);
				} else {
					//no such category 
					return super.processError(String.format(
							ParserConstants.ERROR_VIEW_TYPE, category));
				}
			}
		}
		
		//multiple views: only for hashtags 
		for(int i = 0; i < split.length; i++) {
			category = split[i].trim().toLowerCase(); 
			//tags view type
			if (category.startsWith("#")) {
				views.add(category.replace("#", "").trim());
			} else {
				return super.processError(String.format(
						ParserConstants.ERROR_VIEW_TYPE_TAG, category));
			}
		}
		return new ProcessedObject("VIEW_TAGS", views); 
	}
	
	/* @@author A0134177E
	 * For quick testing if parsing of tags is done correctly.
	public static void main(String[] args) {
		ParseView pv = new ParseView();
		ProcessedObject po = pv.processView("view", "view tag1#tag2 #tag3");
		System.out.println(po.getCommand());
		po = pv.processView("view", "view #tag1 #tag 2 #tag3");
		System.out.println(po.getCommand());
	}*/
}
