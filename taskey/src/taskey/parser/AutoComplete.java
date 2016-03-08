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
	private ArrayList<String> viewList = new ArrayList<String>();
	
	public AutoComplete() {
		commands.add("add");
		commands.add("view");
		commands.add("del");
		commands.add("set");
		commands.add("search");
		commands.add("done");
		commands.add("undo");
		commands.add("file_loc");
		
		viewList.add("all");
		viewList.add("general");
		viewList.add("deadlines");
		viewList.add("events");
		viewList.add("archive");
		viewList.add("help");
		
	}
	
	/**
	 * Given a partial input of the command, return a list of commands 
	 * that contain that input as a sub-string. 
	 * @param phrase
	 * @return If no such command exists, return null
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
		} 
		//no such command containing that sub-string
		return null; 
	}
	
	/**
	 * Given a partial input that contains "view xxxx",
	 * return a list of available view types that the user can access.
	 * @param phrase
	 * @return If no such list of views is available, return null 
	 */
	public ArrayList<String> completeView(String phrase) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("view", ""); 
		
		for(int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).contains(phrase)) {
				availViews.add(viewList.get(i)); 
			}
		}
		//TODO: Handle case for tagged categories
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
}
