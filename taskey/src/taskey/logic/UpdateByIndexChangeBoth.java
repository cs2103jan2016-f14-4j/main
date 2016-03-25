package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing both their name and date. 
 */
final class UpdateByIndexChangeBoth extends UpdateByIndex {
	
	private String newName;
	private Task newTask; // Contains the new date

	UpdateByIndexChangeBoth(ContentBox contentBox, int updateIndex, String newName, Task newTask) {
		super(contentBox, updateIndex);
		this.newName = newName;
		this.newTask = newTask;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.updateByIndexChangeBoth(contentBox, updateIndex, newName, newTask);
	}
}