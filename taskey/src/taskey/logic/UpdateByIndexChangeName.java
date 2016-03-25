package taskey.logic;

import java.util.ArrayList;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing their name. 
 */
final class UpdateByIndexChangeName extends UpdateByIndex {
	
	private String newName;

	UpdateByIndexChangeName(int updateIndex, String newName) {
		super(updateIndex);
		this.newName = newName;
	}
	
	@Override
	ArrayList<ArrayList<Task>> execute(LogicMemory logicMemory) {
		return null; // TODO
	}
}
