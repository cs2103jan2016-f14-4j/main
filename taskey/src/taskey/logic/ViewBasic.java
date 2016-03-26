package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * viewing of basic task categories (such as deadline or event). 
 */
final class ViewBasic extends Command {
	
	private String viewType;
	
	ViewBasic(String viewType) {
		this.viewType = viewType;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.viewBasic(viewType);
	}
}