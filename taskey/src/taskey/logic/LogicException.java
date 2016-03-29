package taskey.logic;

/**
 * @@author A0134177E
 * Whenever the Logic component encounters an error in command execution, or wishes to pass a message to UI, it
 * encapsulates the message in an instance of this class. This class is made public to facilitate testing, but components 
 * other than Logic should not have any reason to create objects of this class.
 */
@SuppressWarnings("serial")
public class LogicException extends Exception {
	
    //================================================================================
    // Constants
    //================================================================================
	
	public static final String MSG_SUCCESS_ADD = "Successfully added task.";
	public static final String MSG_SUCCESS_DELETE = "Successfully deleted task(s).";
	public static final String MSG_SUCCESS_DONE = "Task moved to archive.";
	public static final String MSG_SUCCESS_UPDATE = "Successfully updated task.";
	public static final String MSG_SUCCESS_CLEAR = "Successfully cleared memory.";
	public static final String MSG_SUCCESS_SAVE = "Save successful.";
	public static final String MSG_SUCCESS_CHANGE_DIR = "Successfully changed save directory.";
	public static final String MSG_ERROR_INVALID_COMMAND = "Invalid command!";
	public static final String MSG_ERROR_DUPLICATE_TASKS = "Duplicate tasks are not allowed.";	                                             
	public static final String MSG_ERROR_DATE_EXPIRED = "The deadline/end date is already past!";
	public static final String MSG_ERROR_INVALID_INDEX = "Invalid index specified!";
	public static final String MSG_ERROR_DONE_INVALID = "This task is already archived!";
	public static final String MSG_ERROR_UPDATE_INVALID = "Cannot update archived tasks!";
	public static final String MSG_ERROR_UNDO = "Nothing to undo!";
	public static final String MSG_ERROR_TAG_NOT_FOUND = "The tag(s) you specified were not found.";
	public static final String MSG_ERROR_SEARCH_NOT_FOUND = "No search results found.";
	public static final String MSG_ERROR_PRIORITY_NOT_FOUND = "There are no pending/expired tasks with that priority level.";
	public static final String MSG_ERROR_SAVE = "Error occurred during save.";
	public static final String MSG_ERROR_CHANGE_DIR = "Error changing save directory!";
	
    //================================================================================
    // Constructor
    //================================================================================

	public LogicException(String message) {
		super(message);
	}
	
    //================================================================================
    // Overriding Methods
    //================================================================================
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getMessage() == null) ? 0 : getMessage().hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		LogicException other = (LogicException) obj;
		
		if (getMessage() == null) {
			if (other.getMessage() != null) {
				return false;
			}
		} else if (!getMessage().equals(other.getMessage())) {
			return false;
		}
		
		return true;
	}
}
