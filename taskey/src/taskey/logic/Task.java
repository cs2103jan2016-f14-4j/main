package taskey.logic;

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

	@Override
	//tasks are comparable by their time. 
	public int compareTo(Task anotherTask) {
		// TODO Auto-generated method stub
		return 0;
	}

}
