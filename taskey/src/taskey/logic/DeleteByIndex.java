package taskey.logic;

import java.util.ArrayList;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * deleting of indexed tasks. 
 */
final class DeleteByIndex extends Command {
	
	private int deleteIndex;
	
	DeleteByIndex(int deleteIndex) {
		this.deleteIndex = deleteIndex;
	}
	
	@Override
	ArrayList<ArrayList<Task>> execute(LogicMemory logicMemory) {
		return null; // TODO
	}
}