package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedAC;

/**
 * @@author A0107345L
 * This class processes what words should be shown in the dropdown
 * menu if the user types into the CLI - it allows user to auto-complete
 * his commands. 
 * @author Xue Hui
 *
 */
public class AutoComplete {
	private HashMap<String,String> commandList = new HashMap<String,String>();
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
		
		commandList.put("add","add");
		commandList.put("view","view");
		commandList.put("del", "del");
		commandList.put("set","set");
		commandList.put("search","search");
		commandList.put("done","done");
		commandList.put("undo","undo");
		commandList.put("file_loc","file_loc");
		
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
	public ProcessedAC completeCommand(String phrase) {
		phrase = phrase.toLowerCase(); 
		
		//if the command is completed, don't need to process
		if (commandList.containsKey("phrase")) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
		}
		
		ArrayList<String> availCommands = new ArrayList<String>();
		
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i).contains(phrase)) {
				availCommands.add(commands.get(i)); 
			}
		}
		
		if (!availCommands.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
		} 
		//no such command containing that sub-string
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
	}
	
	/**
	 * Given a partial input that contains "view xxxx",
	 * return a list of available view types that the user can access.
	 * @param phrase
	 * @return If no such list of views is available, return null 
	 */
	public ArrayList<String> completeView(String phrase, 
			ArrayList<String> tagDB) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("view", ""); 
		
		for(int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).contains(phrase)) {
				availViews.add(viewList.get(i)); 
			}
		}
		
		for(int i = 0; i < tagDB.size(); i++) {
			if (tagDB.get(i).contains(phrase)) {
				availViews.add(tagDB.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
	
	/**
	 * Given a partial input that contains "del xxxx",
	 * display a list of tasks that the user can delete 
	 * (he can delete by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	public ArrayList<String> completeDelete(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("del", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
	
	/**
	 * Given a partial input that contains "done xxxx",
	 * display a list of tasks that the user can set as done 
	 * (he can set done by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	public ArrayList<String> completeDone(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("done", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
}
