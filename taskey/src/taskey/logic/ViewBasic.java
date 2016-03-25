package taskey.logic;

import java.util.ArrayList;

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
	ArrayList<ArrayList<Task>> execute(LogicMemory logicMemory) {
		return null; // TODO
	}
}