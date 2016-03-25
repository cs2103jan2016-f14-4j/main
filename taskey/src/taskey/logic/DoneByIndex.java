package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * completion of indexed tasks. 
 */
final class DoneByIndex extends Command {
	
	private int listIndex;
	private int taskIndex;
	
	DoneByIndex(int listIndex, int taskIndex) {
		this.listIndex = listIndex;
		this.taskIndex = taskIndex;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.doneByIndex(listIndex, taskIndex);
	}
}