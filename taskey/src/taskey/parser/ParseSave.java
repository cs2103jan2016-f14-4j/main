package taskey.parser;

import taskey.messenger.ProcessedObject;

/**
 * @@author A0107345L 
 * Job of this class is to parse "save" commands. 
 * @author Xue Hui
 *
 */
public class ParseSave extends ParseCommand {

	public ParseSave() {
		super(); 
	}
	
	protected ProcessedObject processSave(String command) {
		assert(command != null); 
		
		return new ProcessedObject(command.toUpperCase()); 
	}
	
}
