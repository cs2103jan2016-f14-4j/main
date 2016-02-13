package taskey.logic;

import taskey.parser.TimeConverter; 

public class Task implements Comparable<Task>{
	public static final int NONE = -1; 
	
	private String taskName;
	private String taskDetails;
	private String taskType; //floating,event (has start and end) , work (has a deadline)
	private long[] datesEpoch = {NONE,NONE,NONE,NONE}; 
	private String[] datesHuman = {"","","",""};
	private boolean isRecurring = false; //set to true if it is recurring. 
	//dates*[0]: recurring date (if recurring event) 
	//dates*[1]: start Time (events)
	//date*[2]: end Time (events) 
	//date*[3]: deadline (work) 
	//TODO: Date Array TBC 
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
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
	
	public void setStartDate() {
		//TODO 
	}
	
	public void setEndDate() {
		//TODO 
	}
	
	public boolean getIsRecurring() {
		return isRecurring; 
	}
	
	public void setIsRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring; 
	}
	
	@Override
	//tasks are comparable by their time. 
	public int compareTo(Task anotherTask) {
		// TODO Auto-generated method stub
		return 0;
	}

}
