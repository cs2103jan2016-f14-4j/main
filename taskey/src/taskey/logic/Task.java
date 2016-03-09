package taskey.logic;

import java.text.ParseException;
import java.util.ArrayList;

import taskey.parser.TimeConverter; 

/**
 * Task object holds all the details of the task 
 * 
 * taskType can have the following values:
 * 1. FLOATING
 * 2. EVENT (has a start and end time)
 * 3. DEADLINE 
 * 
 * 
 * date arrays have the following format:
 * dates*[0]: recurring interval (if recurring event/deadline) 
 * dates*[1]: start Time (events)
 * dates*[2]: end Time (events) 
 * dates*[3]: deadline 
 * idea for recurring : store the diff in time to the next recurrence in seconds
 * TODO: decide how to store the diff in time of a recurring event in human time. 
 * 
 * @author Xue Hui 
 *
 */

public class Task implements Comparable<Task> {
	public static final int NONE = -1; 
	public static final String EMPTY = ""; 
	
	private String taskName = null;
	private ArrayList<String> taskTags = null;
	private String taskType = null; 
	private long[] datesEpoch = {NONE,NONE,NONE,NONE}; 
	private String[] datesHuman = {EMPTY,EMPTY,EMPTY,EMPTY};
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
	//CONSTRUCTORS ==============================================
	public Task() {
		
	}
	
	public Task(String taskName) {
		this.taskName = taskName;
	}
	
	public Task(String taskName, ArrayList<String> taskTags) {
		this.taskName = taskName;
		this.taskTags = taskTags;
	}
	
	
	//BASIC GET/SET METHODS =====================================
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName; 
	}
	
	public ArrayList<String> getTaskTags() {
		return taskTags; 
	}
	
	public void setTaskTags(ArrayList<String> taskTags) {
		this.taskTags = taskTags; 
	}
	
	public String getTaskType() {
		return taskType; 
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType; 
	}
	
	public String getStartDate() {
		return datesHuman[1]; 
	}
	
	public long getStartDateEpoch() {
		return datesEpoch[1]; 
	}
	
	public String getEndDate() {
		return datesHuman[2]; 
	}
	
	public long getEndDateEpoch() {
		return datesEpoch[2]; 
	}
	
	public String getDeadline() {
		return datesHuman[3]; 
	}
	
	public long getDeadlineEpoch() {
		return datesEpoch[3]; 
	}
	
	/**
	 * Given the startDate in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param startDate
	 */
	public void setStartDate(String startDate) {
		datesHuman[1] = startDate; 
		try {
			datesEpoch[1] = timeConverter.toEpochTime(startDate);
		} catch (ParseException error) {
			//do nothing
		}
	}
	
	/**
	 * Given the startDate in Epoch, 
	 * auto-key in the human time as well. 
	 * @param startDate
	 */
	public void setStartDate(long startDate) {
		datesEpoch[1] = startDate; 
		datesHuman[1] = timeConverter.toHumanTime(startDate); 
	}
	
	/**
	 * Given the endDate in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param endDate
	 */
	public void setEndDate(String endDate) {
		datesHuman[2] = endDate; 
		try {
			datesEpoch[2] = timeConverter.toEpochTime(endDate); 
		} catch (ParseException error) {
			
		}
	}
	
	/**
	 * Given the endDate in Epoch, 
	 * auto-key in the human time as well. 
	 * @param endDate
	 */
	public void setEndDate(long endDate) {
		datesEpoch[2] = endDate; 
		datesHuman[2] = timeConverter.toHumanTime(endDate); 		
	}
	
	/**
	 * Given the deadline in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param deadline
	 */
	public void setDeadline(String deadline) {
		datesHuman[3] = deadline; 
		try {
			datesEpoch[3] = timeConverter.toEpochTime(deadline); 
		} catch (ParseException error) {
			
		}
	}
	
	/**
	 * Given the deadline in Epoch,
	 * auto-key in the human time as well. 
	 * @param deadline
	 */
	public void setDeadline(long deadline) {
		datesEpoch[3] = deadline; 
		datesHuman[3] = timeConverter.toHumanTime(deadline); 	
		
	}
	 
	
	//NON-BASIC METHODS ==========================================
	
	
	public String[] getEventTime() {
		String[] eventTime = {datesHuman[1],datesHuman[2]};
		return eventTime; 
	}
	
	public long[] getEventTimeEpoch() {
		long[] eventTime = {datesEpoch[1],datesEpoch[2]}; 
		return eventTime; 
	}
	
	/**
	 * Add a new tag to your task 
	 * @param tag
	 */
	public void addTaskTag(String tag) {
		if (taskTags != null) {
			taskTags.add(tag); 
		} else {
			taskTags = new ArrayList<String>();
			taskTags.add(tag); 
		}
	}
	
	/**
	 * Remove a tag from your task 
	 * @param tag
	 */
	public void removeTaskTag(String tag) {
		for(int i = 0; i < taskTags.size(); i++) {
			String temp = taskTags.get(i); 
			if(tag.compareTo(temp) == 0) {
				taskTags.remove(i); 
				break; 
			}
		}
		//if empty, remove the arraylist 
		if (taskTags.isEmpty()) {
			taskTags = null; 
		}
	}
	
	/**
	 * Get a duplicate of this task object at a different address 
	 * @return duplicate of this task object at a different address
	 */
	public Task getDuplicate() {
		Task duplicate = new Task();
		
		if (taskName != null) {
			duplicate.setTaskName(taskName);
		}
		
		if (taskTags != null) {
			duplicate.setTaskTags(taskTags);
		}
		
		if (taskType != null) {
			duplicate.setTaskType(this.getTaskType());
			
			switch(taskType) {
				case "FLOATING":
					//nothing else to add. 
					break;
				case "DEADLINE":
					duplicate.setDeadline(this.getDeadlineEpoch());
					break;
				case "EVENT":
					duplicate.setStartDate(this.getStartDateEpoch());
					duplicate.setEndDate(this.getEndDateEpoch());
					break; 
			}
		}
		
		return duplicate; 
	}
	
	@Override
	/**
	 * tasks are comparable by their start time. 
	 * used for sorting in ArrayList<Task>
	 * So that one can easily just call Collections.sort(taskList) 
	 */
	public int compareTo(Task anotherTask) {
		long startTime = datesEpoch[1]; 
		long otherStartTime = anotherTask.getStartDateEpoch(); 
		
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
	 * @return true if they have the same name, else return false 
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Task)) {
		    return false;
		  }
		
		Task other = (Task) obj;
		
		return taskName.equals(other.getTaskName()); 
	}
	
	@Override 
	/**
	 * For debugging 
	 */
	public String toString() {
		String stringRep = ""; 
		
		if (taskName != null) {
			stringRep += taskName;
			stringRep += ", ";
		}
		
		if (taskType != null) {
			stringRep += taskType; 
			stringRep += ", ";
		
			switch(taskType) {
				case "EVENT":
					String[] eventTime = getEventTime(); 
					stringRep += "from " + eventTime[0];
					stringRep += " to " + eventTime[1]; 
					break;
				case "DEADLINE":
					stringRep += "due on " + getDeadline(); 
					break; 
				default:
					break;
			}
		}
		stringRep += "\n";
		
		if (taskTags != null) { 
			stringRep += "tags: ";
			for(int i  =0; i < taskTags.size(); i++) {
				stringRep += taskTags.get(i) + ", "; 
			}
			stringRep += "\n";
		}
		
		return stringRep; 
	}

}
