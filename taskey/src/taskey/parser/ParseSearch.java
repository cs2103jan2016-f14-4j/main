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
	protected ProcessedObject processSearch(String command, String stringInput) {
		assert(stringInput != null);
		
		ProcessedObject processed = new ProcessedObject(command.toUpperCase()); 
		String searchPhrase = getTaskName(stringInput);
		
		if (searchPhrase.compareTo("") != 0) { 
			processed.setSearchPhrase(searchPhrase);
		} else {
			processed = super.processError(ParserConstants.ERROR_EMPTY_SEARCH); 
		}
		return processed; 
	}
	
	/**
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	private String getTaskName(String stringInput) {
		String command = stringInput.split(" ")[0]; 
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}

}
