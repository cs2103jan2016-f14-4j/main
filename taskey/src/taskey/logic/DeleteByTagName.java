package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * deleting of indexed tasks. 
 */
final class DeleteByTagName extends Command {
	
	private String tagName;
	
	DeleteByTagName(String tagName) {
		this.tagName = tagName;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws Exception {
		logicMemory.deleteByTagName(tagName);
	}
}
