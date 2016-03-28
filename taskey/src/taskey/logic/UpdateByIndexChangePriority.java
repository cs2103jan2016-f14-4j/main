package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing their priority. 
 */
final class UpdateByIndexChangePriority extends UpdateByIndex {
	
	private int newPriority;

	UpdateByIndexChangePriority(ContentBox contentBox, int updateIndex, int newPriority) {
		super(contentBox, updateIndex);
		this.newPriority = newPriority;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.updateByIndexChangePriority(contentBox, updateIndex, newPriority);
	}
}
