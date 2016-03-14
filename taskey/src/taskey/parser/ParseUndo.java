package taskey.parser;

import taskey.logic.ProcessedObject;

/**
 * @@author A0107345L
 * Purpose is to parse the "undo" command 
 * @author Xue Hui
 *
 */
public class ParseUndo {
	
	public ParseUndo() {
		
	}

	/**
	 * Return ProcessedObject for Undo 
	 * @param command
	 * @return
	 */
	public ProcessedObject processUndo(String command) {
		return new ProcessedObject(command.toUpperCase()); 
	}
}
