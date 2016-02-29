package taskey.parser;

import taskey.logic.ProcessedObject;

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
