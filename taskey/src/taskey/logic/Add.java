package taskey.logic;

import java.util.ArrayList;

/** @@author A0134177E
 * This class provides the basic resources necessary for subclasses to encapsulate the instructions that the receiver, 
 * LogicMemory, must perform in order to facilitate the adding of tasks. 
 */
abstract class Add extends Command {
	
	protected Task taskToAdd; 
	
	protected Add(Task taskToAdd) {
		this.taskToAdd = taskToAdd;
	}
	
	protected void addTagsToDatabase(ArrayList<TagCategory> tagList) {
		// TODO
		for (TagCategory tc : tagList) {
		}
	}
}
