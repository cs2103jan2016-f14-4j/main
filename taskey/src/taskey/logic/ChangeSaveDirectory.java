package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * changing of the directory where saved task and tag category data is stored.
 */
final class ChangeSaveDirectory extends Command {
	
	String pathName;

	ChangeSaveDirectory(String pathName) {
		this.pathName = pathName;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.changeSaveDirectory(pathName);
	}
}
