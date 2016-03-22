package taskey.constants;

/**
 * @@author A0107345L
 * Constants for usage by the Parser package
 * @author Xue Hui
 *
 */
public class ParserConstants {
	public static final String FINISHED_COMMAND = "FINISHED_COMMAND";
	public static final String DISPLAY_COMMAND = "DISPLAY_COMMAND"; 
	public static final String NO_SUCH_COMMAND = "NO_SUCH_COMMAND"; 
	public static final String DELETE_BY_INDEX = "DELETE_BY_INDEX"; 
	public static final String DELETE_BY_NAME = "DELETE_BY_NAME"; 
	public static final String DELETE_BY_CATEGORY = "DELETE_BY_CATEGORY"; 
	public static final String DONE_BY_INDEX = "DONE_BY_INDEX"; 
	public static final String DONE_BY_NAME = "DONE_BY_NAME"; 
	public static final String UPDATE_BY_INDEX_CHANGE_NAME = "UPDATE_BY_INDEX_CHANGE_NAME"; 
	public static final String UPDATE_BY_INDEX_CHANGE_DATE = "UPDATE_BY_INDEX_CHANGE_DATE"; 
	public static final String UPDATE_BY_INDEX_CHANGE_BOTH = "UPDATE_BY_INDEX_CHANGE_BOTH";
	public static final String UPDATE_BY_NAME_CHANGE_NAME = "UPDATE_BY_NAME_CHANGE_NAME";
	public static final String UPDATE_BY_NAME_CHANGE_DATE = "UPDATE_BY_NAME_CHANGE_DATE";
	public static final String UPDATE_BY_NAME_CHANGE_BOTH = "UPDATE_BY_NAME_CHANGE_BOTH";
	public static final String NEW_FILE_LOC = "CHANGE_FILE_LOC"; 
	
	/* Errors */ 
	public static final String ERROR = "ERROR"; 
	public static final String ERROR_DATE_FORMAT = "Error: \"%s\" is not an accepted date format"; 
	public static final String ERROR_DATE_GRAMMAR = "Error: \"%s\" is a grammatically incorrect date"; 
	public static final String ERROR_VIEW_TYPE = "Error: \"%s\" is not a valid category"; 
	public static final String ERROR_VIEW_TYPE_TAG = "Error: \"%s\" is not a valid tag";
	public static final String ERROR_VIEW_EMPTY = "Error: No view type selected";
	public static final String ERROR_COMMAND = "Error: \"%s\" is not a valid command"; 
	public static final String ERROR_ADD_EMPTY = "Error: Cannot be an empty add";
	public static final String ERROR_ONLY_NUMS = "Error: Task name cannot consist entirely of numbers";
	public static final String ERROR_INPUT_EMPTY = "Error: Cannot be an empty change"; 
	public static final String ERROR_STRING_FORMAT = "Error: Wrong format for new task name/date";
	public static final String ERROR_DEL_EMPTY_CAT = "Error: Cannot delete an empty category!";
	public static final String ERROR_DEL_EMPTY = "Error: No task has been selected for deletion";
	public static final String ERROR_DONE_EMPTY = "Error: No task has been selected as done";
	public static final String ERROR_EMPTY_SEARCH = "Error: No search phrase entered"; 
	
	/*Date Handling Constants*/
	public static final String DAY_END = "23:59:59"; 
	public static final String DAY_END_SHORT = "23:59"; 
	
	public static final long ONE_DAY = 86400; 
	public static final long TWO_DAYS = 2*ONE_DAY; 
	public static final long THREE_DAYS = 3*ONE_DAY; 
	public static final long FOUR_DAYS = 4*ONE_DAY; 
	public static final long FIVE_DAYS = 5*ONE_DAY; 
	public static final long SIX_DAYS = 6*ONE_DAY; 
	public static final long ONE_WEEK = 604800; 
}
