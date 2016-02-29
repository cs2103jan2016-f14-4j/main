package taskey.parser;

import taskey.logic.ProcessedObject;

/**
 * Purpose is to parse the "undo" command 
 * @author Nat
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
