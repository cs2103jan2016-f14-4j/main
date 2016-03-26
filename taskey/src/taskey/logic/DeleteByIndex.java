package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * deleting of indexed tasks. 
 */
final class DeleteByIndex extends Command {
	
	private ContentBox contentBox;
	private int deleteIndex;
	
	DeleteByIndex(ContentBox contentBox, int deleteIndex) {
		this.contentBox = contentBox;
		this.deleteIndex = deleteIndex;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.deleteByIndex(contentBox, deleteIndex);
	}
}