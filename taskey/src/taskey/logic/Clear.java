package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * clearing of all Task and TagCategory data <b>in memory. Data present in disk is not affected.</b> Note that the 
 * "clear" command is only used for developer testing and is not available to the user.
 */
final class Clear extends Command {
	
	@Override
	void execute(LogicMemory logicMemory) {
		logicMemory.clearAllTaskLists();
		logicMemory.clearTagCategoryList();
	}
}
