package taskey.logic;

import java.util.ArrayList;

/** @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * adding of floating tasks. 
 */
final class AddFloating extends Add {

	AddFloating(Task taskToAdd) {
		super(taskToAdd);
	}
	
	@Override
	ArrayList<ArrayList<Task>> execute(LogicMemory logicMemory) {
		return null; // TODO
	}
}
