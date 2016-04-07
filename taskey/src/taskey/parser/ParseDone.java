package taskey.parser;

import taskey.messenger.ProcessedObject;
import taskey.messenger.Task;
import taskey.constants.ParserConstants; 

/**
 * @@author A0107345L
 * Purpose of this class is to parse the "done" command 
 * @author Xue Hui
 *
 */
public class ParseDone extends ParseCommand {
		
	public ParseDone() {
		super(); 
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
	protected ProcessedObject processDone(String stringInput) {
		ProcessedObject processed; 
		String taskName = getTaskName(stringInput);
		
		if (taskName.compareTo("") == 0) {
			return super.processError(ParserConstants.ERROR_DONE_EMPTY);
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
