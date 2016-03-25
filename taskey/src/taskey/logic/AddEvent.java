package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * adding of event tasks. 
 */
final class AddEvent extends Add {

	AddEvent(Task taskToAdd) {
		super(taskToAdd);
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.addEvent(taskToAdd);
		addTagsToMemory(logicMemory, taskToAdd.getTaskTags());
	}
}