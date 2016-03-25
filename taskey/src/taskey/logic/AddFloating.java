package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * adding of floating tasks. 
 */
final class AddFloating extends Add {

	AddFloating(Task taskToAdd) {
		super(taskToAdd);
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.addFloating(taskToAdd);
		addTagsToMemory(logicMemory, taskToAdd.getTaskTags());
	}
}
