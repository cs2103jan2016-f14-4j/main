package taskey.messenger;

import java.util.ArrayList;

/**
 * @@author A0107345L
 * This class is a short form for ProcessedAutoComplete.
 * It holds all the information that Logic needs to know about 
 * what list to display for the Auto Complete feature in the UI. 
 * 
 * Command Types: 
 * 1. DISPLAY_COMMAND : user is halfway through his command, show the list still
 * 2. FINISHED_COMMAND : user has completed typing his command, 
 * 						no need to display anything
 * 3. NO_SUCH_COMMAND : user keyed in a command that doesn't exist 
 * 3. to be added when we add more auto complete features 
 * @author Xue Hui
 */
public class ProcessedAC {
	private String command = null; 
	private ArrayList<String> availCommands = null; 
	
	/**
	 * Constructs a ProcessedAC Object
	 * @param command
	 */
	public ProcessedAC(String command) {
		this.command = command; 
	}
	
	/**
	 * Overloaded constructor 
	 * @param command
	 * @param availCommands
	 */
	public ProcessedAC(String command, ArrayList<String> availCommands) {
		this.command = command; 
		this.availCommands = availCommands; 
	}
	
	/**
	 * @return command type
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Set the command type 
	 * @param command
	 */
	public void setCommand(String command) {
		this.command = command; 
	}
	
	/**
	 * @return availCommands to display to the user 
	 */
	public ArrayList<String> getAvailCommands() {
		return availCommands;
	}
	
	/**
	 * set a lit of commands to display to the user 
	 * @param availCommands
	 */
	public void setAvailCommands(ArrayList<String> availCommands) {
		this.availCommands = availCommands; 
	}
	
}
