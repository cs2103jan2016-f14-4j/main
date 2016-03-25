package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing their date. 
 */
final class UpdateByIndexChangeDate extends UpdateByIndex {
	
	private Task newTask; // Contains the new date

	UpdateByIndexChangeDate(ContentBox contentBox, int updateIndex, Task newTask) {
		super(contentBox, updateIndex);
		this.newTask = newTask;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.updateByIndexChangeDate(contentBox, updateIndex, newTask);
	}
}
