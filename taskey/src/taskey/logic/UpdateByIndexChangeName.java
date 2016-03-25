package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing their name. 
 */
final class UpdateByIndexChangeName extends UpdateByIndex {
	
	private String newName;

	UpdateByIndexChangeName(ContentBox contentBox, int updateIndex, String newName) {
		super(contentBox, updateIndex);
		this.newName = newName;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.updateByIndexChangeName(contentBox, updateIndex, newName);
	}
}
