package taskey.constants;

public class ParserConstants {
	public static final String DELETE_BY_INDEX = "DELETE_BY_INDEX"; 
	public static final String DELETE_BY_NAME = "DELETE_BY_NAME"; 
	public static final String DONE_BY_INDEX = "DONE_BY_INDEX"; 
	public static final String DONE_BY_NAME = "DONE_BY_NAME"; 
	public static final String UPDATE_BY_INDEX_CHANGE_NAME = "UPDATE_BY_INDEX_CHANGE_NAME"; 
	public static final String UPDATE_BY_INDEX_CHANGE_DATE = "UPDATE_BY_INDEX_CHANGE_DATE"; 
	public static final String UPDATE_BY_NAME_CHANGE_NAME = "UPDATE_BY_NAME_CHANGE_NAME";
	public static final String UPDATE_BY_NAME_CHANGE_DATE = "UPDATE_BY_NAME_CHANGE_DATE";
	
	/* Errors */ 
	public static final String ERROR_DATE_FORMAT = "Wrong date format"; 
	public static final String ERROR_VIEW_TYPE = "No such category"; 
	public static final String ERROR_COMMAND = "No such command"; 
	
	/*Date Handling Constants*/
	public static final String DAY_END = "23:59:59"; 
	
	public static final long ONE_DAY = 86400; 
	public static final long TWO_DAYS = 2*ONE_DAY; 
	public static final long THREE_DAYS = 3*ONE_DAY; 
	public static final long FOUR_DAYS = 4*ONE_DAY; 
	public static final long FIVE_DAYS = 5*ONE_DAY; 
	public static final long SIX_DAYS = 6*ONE_DAY; 
	public static final long ONE_WEEK = 604800; 
}
