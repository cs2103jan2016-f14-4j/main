package taskey.logic;

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
	void execute(LogicMemory logicMemory) {
		// TODO
	}
}