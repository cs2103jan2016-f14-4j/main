package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * completion of indexed tasks. 
 */
final class DoneByIndex extends Command {
	
	private ContentBox contentBox;
	private int doneIndex;
	
	DoneByIndex(ContentBox contentBox, int doneIndex) {
		this.contentBox = contentBox;
		this.doneIndex = doneIndex;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.doneByIndex(contentBox, doneIndex);
	}
}