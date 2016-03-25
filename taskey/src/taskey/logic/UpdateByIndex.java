package taskey.logic;

import taskey.constants.UiConstants.ContentBox;

/** 
 * @@author A0134177E
 * This class provides the basic resources necessary for subclasses to encapsulate the instructions that the receiver, 
 * LogicMemory, must perform in order to facilitate the updating of indexed tasks. 
 */
abstract class UpdateByIndex extends Command {
	
	protected ContentBox contentBox;
	protected int updateIndex;
	
	protected UpdateByIndex(ContentBox contentBox, int updateIndex) {
		this.contentBox = contentBox;
		this.updateIndex = updateIndex;
	}
}