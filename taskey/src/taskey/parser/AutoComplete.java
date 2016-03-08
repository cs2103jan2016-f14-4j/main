package taskey.parser;

import java.util.ArrayList;

/**
 * This class processes what words should be shown in the dropdown
 * menu if the user types into the CLI - it allows user to auto-complete
 * his commands. 
 * @author Xue Hui
 *
 */
public class AutoComplete {
	private ArrayList<String> commands = new ArrayList<String>();
	
	public AutoComplete() {
		commands.add("add");
		commands.add("view");
		commands.add("del");
		commands.add("set");
		commands.add("search");
		commands.add("done");
		commands.add("undo");
		commands.add("file_loc");
	}
	
	/**
	 * Given a partial input of the command, return a list of commands 
	 * that contain that input as a sub-string. 
	 * @param phrase
	 * @return
	 */
	public ArrayList<String> completeCommand(String phrase) {
		phrase = phrase.toLowerCase(); 
		ArrayList<String> availCommands = new ArrayList<String>();
		
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i).contains(phrase)) {
				availCommands.add(commands.get(i)); 
			}
		}
		
		if (!availCommands.isEmpty()) {
			return availCommands;
		} else {
			//no such command containing that sub-string
			return null; 
		}
	}

}
