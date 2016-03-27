package taskey.parser;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedObject;

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "search" command 
 * @author Xue Hui
 *
 */
public class ParseSearch extends ParseCommand {
	
	public ParseSearch() {
		super(); 
	}
	
	/**
	 * Return ProcessedObject for Search 
	 * @param command
	 * @param stringInput
	 * @return
	 */
	public ProcessedObject processSearch(String command, String stringInput) {
		ProcessedObject processed = new ProcessedObject(command.toUpperCase()); 
		String searchPhrase = getTaskName(command, stringInput);
		
		if (searchPhrase.compareTo("") != 0) { 
			processed.setSearchPhrase(searchPhrase);
		} else {
			processed = super.processError(ParserConstants.ERROR_EMPTY_SEARCH); 
		}
		return processed; 
	}
	
	/**
	 * FOR FLOATING TASK: 
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	public String getTaskName(String command, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}

}
