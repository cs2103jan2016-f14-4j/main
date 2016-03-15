package taskey.constants;

/**
 * Constants for usage by the Parser package
 * @author Xue Hui
 *
 */
public class ParserConstants {
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
	public static final String ERROR_DATE_FORMAT = "Wrong date format"; 
	public static final String ERROR_VIEW_TYPE = "No such category"; 
	public static final String ERROR_COMMAND = "No such command"; 
	public static final String ERROR_ADD_EMPTY = "Cannot be an empty add";
	public static final String ERROR_ONLY_NUMS = "Task name cannot consist entirely of numbers";
	public static final String ERROR_INVALID_INPUT = "Invalid input: cannot be an empty set"; 
	public static final String ERROR_STRING_FORMAT = "Wrong format for new task name/date";
	
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
