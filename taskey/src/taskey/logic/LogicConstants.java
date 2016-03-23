package taskey.logic;

//@@author A0134177E
public class LogicConstants {
	public static final String MSG_EXCEPTION_COMMAND_EXECUTION = "Failed to execute command.";
	public static final String MSG_EXCEPTION_DUPLICATE_TASKS = "The task \"%1$s\" already exists!";
	public static final String MSG_EXCEPTION_SAVING_TAGS = "Error saving tags!";
	public static final String MSG_EXCEPTION_DATE_EXPIRED = "The date \"%1$s\" is already past!";
	public static final String MSG_EXCEPTION_DELETE_INVALID_TAB = "Cannot delete from this tab!";
	public static final String MSG_EXCEPTION_INVALID_INDEX = "\"%1$d\" is not a valid index!";
	public static final String MSG_EXCEPTION_NAME_NOT_FOUND = "\"%1$s\" not found in this tab!";
	public static final String MSG_EXCEPTION_SEARCH_PHRASE_EMPTY = "Search phrase cannot be empty!";
	public static final String MSG_EXCEPTION_DONE_INVALID_TAB = "Cannot use \"done\" command from this tab!";
	public static final String MSG_EXCEPTION_UPDATE_INVALID_TAB = "Cannot use \"set\" command from this tab!";
	public static final String MSG_EXCEPTION_UNDO = "Nothing to undo!";
	public static final String MSG_EXCEPTION_TAG_NOT_FOUND = "There are no pending tasks tagged with \"%1$s\".";
	
	public enum ListID {
		THIS_WEEK(0), PENDING(1), EXPIRED(2), GENERAL(3), DEADLINE(4), EVENT(5), COMPLETED(6), 
		SEARCH(0), VIEW(0);
		
		private int index;
		
		private ListID(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	public enum CategoryID {
		GENERAL(0), DEADLINE(1), EVENT(2), COMPLETED(3);
		
		private int index;
		
		private CategoryID(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
}
