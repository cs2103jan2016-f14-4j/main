package taskey.logic;

import taskey.parser.TimeConverter; 

public class Task implements Comparable<Task>{
	public static final int NONE = -1; 
	
	private String taskName;
	private String taskDetails;
	private long[] datesEpoch = {NONE,NONE,NONE,NONE}; 
	private String[] datesHuman = {"","","",""};
	//dates*[0]: startDate
	//dates*[1]: endDate
	//date*[2]: recurring
	//date*[3]: ??? 
	//TODO: Date Array TBC 
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
	public Task() {
		taskName = "";
		taskDetails = "";
	}
	
	public Task(String taskName) {
		this.taskName = taskName;
		taskDetails = "";	
	}
	
	public Task(String taskName, String taskDetails) {
		this.taskName = taskName;
		this.taskDetails = taskDetails;
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

	@Override
	//tasks are comparable by their time. 
	public int compareTo(Task anotherTask) {
		// TODO Auto-generated method stub
		return 0;
	}

}
