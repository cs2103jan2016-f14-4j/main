package taskey.logic;

// @@author A0134177E
public class LogicConstants {
	public static final String MSG_EXCEPTION_COMMAND_EXECUTION = "Failed to execute command.";
	public static final String MSG_EXCEPTION_DUPLICATE_TASKS = "The task you are trying to add already exists!";
	public static final String MSG_EXCEPTION_DUPLICATE_TASK_NAMES = "There are multiple tasks with that name, choose " 
			                                                        + "which one you want to delete via del <index>.";
			                                             
	public static final String MSG_EXCEPTION_SAVING_TAGS = "Error saving tags!";
	public static final String MSG_EXCEPTION_DATE_EXPIRED = "The deadline/end date is already past!";
	public static final String MSG_EXCEPTION_DELETE_INVALID_TAB = "Cannot delete from this tab!";
	public static final String MSG_EXCEPTION_INVALID_INDEX = "Invalid index specified!";
	public static final String MSG_EXCEPTION_NAME_NOT_FOUND = "\"%1$s\" not found in this tab!";
	public static final String MSG_EXCEPTION_DONE_INVALID = "This task is already archived!";
	public static final String MSG_EXCEPTION_UPDATE_INVALID = "Cannot update archived tasks!";
	public static final String MSG_EXCEPTION_UPDATE_INVALID_TAB = "Cannot use \"set\" command from this tab!";
	public static final String MSG_EXCEPTION_UNDO = "Nothing to undo!";
	public static final String MSG_EXCEPTION_TAG_NOT_FOUND = "No matches found.";
	public static final String MSG_EXCEPTION_SEARCH_NOT_FOUND = "No matches found!";
	public static final String MSG_ADD_SUCCESSFUL = "Successfully added task.";
	public static final String MSG_DELETE_SUCCESSFUL = "Successfully deleted task(s).";
	public static final String MSG_DONE_SUCCESSFUL = "Task moved to archive.";
	public static final String MSG_UPDATE_SUCCESSFUL = "Successfully updated task.";
	public static final String MSG_CLEAR_SUCCESSFUL = "Successfully cleared memory.";
	
	public enum ListID {
		THIS_WEEK(0), PENDING(1), EXPIRED(2), GENERAL(3), DEADLINE(4), EVENT(5), COMPLETED(6), ACTION(7);
		
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
