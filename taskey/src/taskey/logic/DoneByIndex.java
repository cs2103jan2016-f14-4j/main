package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * completion of indexed tasks. 
 */
final class DoneByIndex extends Command {
	
	private int doneIndex;
	
	DoneByIndex(int doneIndex) {
		this.doneIndex = doneIndex;;
	}
	
	@Override
	void execute(LogicMemory logicMemory) {
		// TODO
	}
}