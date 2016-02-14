package taskey.logic;

import taskey.parser.TimeConverter; 

/**
 * Task object holds all the details of the task 
 * @author Xue Hui 
 *
 */

public class Task implements Comparable<Task>{
	public static final int NONE = -1; 
	public static final String EMPTY = ""; 
	
	private String taskName;
	private String taskDetails;
	private String taskType; //floating,event (has start and end) , work (has a deadline)
	private long[] datesEpoch = {NONE,NONE,NONE,NONE}; 
	private String[] datesHuman = {EMPTY,EMPTY,EMPTY,EMPTY};
	private boolean isRecurring = false; //set to true if it is recurring. 
	//dates*[0]: recurring interval (if recurring event/deadline) 
	//dates*[1]: start Time (events)
	//dates*[2]: end Time (events) 
	//dates*[3]: deadline 
	//idea for recurring : store the diff in time to the next recurrence in seconds?
	private TimeConverter timeConverter = new TimeConverter(); 
	
	//CONSTRUCTORS ==============================================
	public Task() {
		taskName = "";
		taskDetails = "";
		taskType = ""; 
	}
	
	public Task(String taskName) {
		this.taskName = taskName;
		taskDetails = "";	
		taskType = "";
	}
	
	public Task(String taskName, String taskDetails) {
		this.taskName = taskName;
		this.taskDetails = taskDetails;
		taskType = ""; 
	}
	
	
	//BASIC GET/SET METHODS =====================================
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName; 
	}
	
	public String getTaskDetails() {
		return taskDetails; 
	}
	
	public void setTaskDetails(String taskDetails) {
		this.taskDetails = taskDetails; 
	}
	
	public String getTaskType() {
		return taskType; 
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType; 
	}
	
	public long getStartDate() {
		return 1; 
	}
	
	public long getEndDate() {
		return 1; 
	}
	
	public void setStartDate(String startDate) {
		//TODO 
	}
	
	public void setStartDate(long startDate) {
		
	}
	
	public void setEndDate(String endDate) {
		//TODO 
	}
	
	public void setEndDate(long endDate) {
		
	}
	
	public boolean getIsRecurring() {
		return isRecurring; 
	}
	
	public void setIsRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring; 
	}
	
	
	//NON-BASIC METHODS ==========================================
	
	public String getDeadline() {
		return datesHuman[3]; 
	}
	
	public long getDeadlineEpoch() {
		return datesEpoch[3]; 
	}
	
	public String[] getEventTime() {
		String[] eventTime = {datesHuman[1],datesHuman[2]};
		return eventTime; 
	}
	
	public long[] getEventTimeEpoch() {
		long[] eventTime = {datesEpoch[1],datesEpoch[2]}; 
		return eventTime; 
	}
	
	public String getRecurringTime() {
		return datesHuman[0]; 
	}
	
	public long getRecurringTimeEpoch() {
		return datesEpoch[0]; 
	}
	
	
	@Override
	/**
	 * tasks are comparable by their time. 
	 * used for sorting in ArrayList<Task>
	 * So that one can easily just call Collections.sort(taskList) 
	 */
	public int compareTo(Task anotherTask) {
		long startTime = datesEpoch[1]; 
		long otherStartTime = anotherTask.getStartDate(); 
		
		if (startTime > otherStartTime) {
			return 1; 
		} else if (startTime == otherStartTime) {
			return 0;
		} else {
			return -1;
		} 
	}
	
	/**
	 * tasks are the same if they have the same name
	 * used for UPDATE_BY_NAME and DELETE_BY_NAME
	 * @param anotherTask
	 * @return 0 if they have the same name, else return -1 
	 */
	public int equals(Task anotherTask) {
		if (taskName.compareTo(anotherTask.getTaskName()) == 0) {
			return 0; 
		}	
		return -1; 
	}

}
