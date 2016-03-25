package taskey.logic;

/** 
 * @@author A0134177E
 * This class encapsulates the instructions that the receiver, LogicMemory, must perform in order to facilitate the 
 * searching of tasks containing a specific keyword. 
 */
final class Search extends Command {
	
	private String searchPhrase;
	
	Search(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}
	
	@Override
	void execute(LogicMemory logicMemory) throws LogicException {
		logicMemory.search(searchPhrase);
	}
}