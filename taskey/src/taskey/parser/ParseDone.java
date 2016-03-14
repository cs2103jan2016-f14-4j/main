package taskey.parser;

import taskey.logic.ProcessedObject;
import taskey.logic.Task;
import taskey.constants.ParserConstants; 

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "done" command 
 * @author Xue Hui
 *
 */
public class ParseDone {
	private ParseError parseError = new ParseError();
		
	public ParseDone() {
		
	}

	/**
	 * If command is done, check if the done is by 
	 * 1. NAME, or
	 * 2. INDEX 
	 * and return the appropriate ProcessedObject
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject 
	 */
	public ProcessedObject processDone(String command, String stringInput) {
		ProcessedObject processed; 
		String taskName = getTaskName(command, stringInput);
		
		if (taskName.compareTo("") == 0) {
			return parseError.processError("no task has been selected as done");
		}
		
		try {
			int index = Integer.parseInt(taskName);
			processed = new ProcessedObject(ParserConstants.DONE_BY_INDEX, index-1); 
			
		} catch (Exception e) {
			//if the done is not by index, then it's by task name. 
			processed = new ProcessedObject(ParserConstants.DONE_BY_NAME, new Task(taskName));
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
