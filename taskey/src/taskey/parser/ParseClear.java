package taskey.parser;

import taskey.messenger.ProcessedObject;

/**
 * @@author A0107345L
 * Job of this class is to parse "Clear" commands. 
 * @author Xue Hui
 *
 */
public class ParseClear extends ParseCommand {
	
	public ParseClear() {
		super(); 
	}
	
	public ProcessedObject processClear(String command) {
		return new ProcessedObject(command.toUpperCase()); 
	}

}
