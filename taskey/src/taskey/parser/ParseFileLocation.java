package taskey.parser;

import taskey.logic.ProcessedObject;
import taskey.constants.ParserConstants; 

/**
 * @@author A0107345L
 * Purpose of this class is to Parse a new directory location
 * for storing files in other locations. 
 * @author Xue Hui
 *
 */
public class ParseFileLocation extends ParseCommand {
	
	public ParseFileLocation() {
		super(); 
	}
	
	public ProcessedObject processLoc(String command, String rawInput) {
		String pathname = getFileName(command, rawInput); 
		ProcessedObject po = new ProcessedObject(ParserConstants.NEW_FILE_LOC);
		po.setNewFileLoc(pathname); 
		
		return po; 
	}
	
	/**
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	public String getFileName(String command, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}

}
