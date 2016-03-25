package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * updating of indexed tasks by changing their date. 
 */
final class UpdateByIndexChangeDate extends UpdateByIndex {
	
	private Task newTask; // Contains the new date

	UpdateByIndexChangeDate(int updateIndex, Task newTask) {
		super(updateIndex);
		this.newTask = newTask;
	}
	
	@Override
	void execute(LogicMemory logicMemory) {
		// TODO
	}
}
