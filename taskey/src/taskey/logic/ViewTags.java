package taskey.logic;

import java.util.ArrayList;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * viewing of tasks with the specified tags. 
 */
final class ViewTags extends Command {
	
	private ArrayList<String> tagNames;
	
	ViewTags(ArrayList<String> tagNames) {
		this.tagNames = tagNames;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.viewTags(tagNames);
	}
}