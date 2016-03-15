package taskey.parser;

import taskey.logic.ProcessedObject;
import taskey.logic.Task;
import taskey.constants.ParserConstants;

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "delete" command
 * @author Xue Hui
 *
 */
public class ParseDelete {
	private ParseError parseError = new ParseError();
	
	public ParseDelete() {
		
	}
	
	/**
	 * If command is delete, check if the deletion is by 
	 * 1. NAME, or
	 * 2. INDEX 
	 * and return the appropriate ProcessedObject
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject 
	 */
	public ProcessedObject processDelete(String command, String stringInput) {
		ProcessedObject processed; 
		String taskName = getTaskName(command, stringInput);
		
		if (taskName.compareTo("") == 0) {
			return parseError.processError("no task has been selected for delete"); 
		}
		
		try {
			int index = Integer.parseInt(taskName);
			processed = new ProcessedObject(ParserConstants.DELETE_BY_INDEX, index-1); 
			
		} catch (Exception e) {
			//if the delete is not by index, then it's by task name. 
			processed = new ProcessedObject(ParserConstants.DELETE_BY_NAME, new Task(taskName));
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
