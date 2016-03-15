package taskey.parser;

import taskey.logic.ProcessedObject;


/**
 * @@author A0107345L
 * Parent class for all kinds of Parse (eg. ParseAdd, etc..)
 * This is to ensure that all Parse classes has a means to 
 * process error. 
 * @author Xue Hui
 *
 */
public class ParseCommand {
	
	public ParseCommand() {
		
	}
	
	/**
	 * Process Errors for string formatting/commands/etc... 
	 * @param errorType
	 * @return
	 */
	public ProcessedObject processError(String errorType) {
		ProcessedObject processed = new ProcessedObject("ERROR");
		processed.setErrorType(errorType); 
		
		return processed;
	}

}
