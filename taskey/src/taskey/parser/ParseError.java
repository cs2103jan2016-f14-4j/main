package taskey.parser;

import taskey.logic.ProcessedObject;

/**
 * @@author A0107345L
 * Purpose of this class is to parse "errors" for any 
 * wrong command formats/etc...
 * @author Xue Hui
 *
 */
public class ParseError {
	
	public ParseError() {
		
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
