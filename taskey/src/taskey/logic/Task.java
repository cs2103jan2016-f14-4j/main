package taskey.logic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import taskey.parser.TimeConverter; 
import static taskey.constants.ParserConstants.DAY_END_SHORT; 

/**
 * @@author A0107345L
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
 * RECURRING WILL NOT BE IMPLEMENTED FOR NOW 
 * 
 * PRIORITY FOR THE TASK:
 * HIGH: 3
 * MEDIUM: 2
 * LOW: 1
 * Default: LOW (1) 
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
	private int priority = 1; //default. to add this to toString for debugging
	boolean pinTask = false; //default: not pinned, to decide whether or not to add this
	
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
	
	public Task(Task other) {
		if (other.taskName != null) {
			taskName = other.taskName;
		}
		
		if (other.taskTags != null) {
			taskTags = new ArrayList<String>(other.taskTags);
		}
		
		priority = other.priority;
		
		if (other.taskType != null) {
			taskType = other.taskType;
			
			switch(taskType) {
				case "FLOATING":
					//nothing else to add. 
					break;
				case "DEADLINE":
					setDeadline(other.getDeadlineEpoch());
					break;
				case "EVENT":
					setStartDate(other.getStartDateEpoch());
					setEndDate(other.getEndDateEpoch());
					break; 
			}
		}	
	}	
	
	//BASIC GET/SET METHODS =====================================
	
	/**
	 * @return Task Name
	 */
	public String getTaskName() {
		return taskName;
	}
	
	/**
	 * Set the Task Name of the task
	 * @param taskName
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName; 
	}
	
	/**
	 * @return all tags that the task contains as an ArrayList
	 */
	public ArrayList<String> getTaskTags() {
		return taskTags; 
	}
	
	/**
	 * Set an ArrayList of task tags to a task 
	 * @param taskTags
	 */
	public void setTaskTags(ArrayList<String> taskTags) {
		this.taskTags = taskTags; 
	}
	
	/**
	 * @return type of the task (event, floating, deadline)
	 */
	public String getTaskType() {
		return taskType; 
	}
	
	/**
	 * Set the task type (event, floating, deadline) 
	 * @param taskType
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType; 
	}
	
	/**
	 * @return Start Date of an event, without 23:59
	 */
	public String getStartDate() {
		String date = datesHuman[1]; 
		
		if (date.contains(DAY_END_SHORT)) {
			date = date.replaceFirst(DAY_END_SHORT, ""); 
		}
		return date.trim(); 
	}
	
	/**
	 * @return startDate of an event without stripping off 23:59
	 */
	public String getStartDateFull() {
		return datesHuman[1]; 
	}
	
	/**
	 * @return startDate of an event in epoch
	 */
	public long getStartDateEpoch() {
		return datesEpoch[1]; 
	}
	
	/**
	 * @return end date of an event in human time, without 23:59
	 */
	public String getEndDate() {
		String date = datesHuman[2]; 
		
		if (date.contains(DAY_END_SHORT)) {
			date = date.replaceFirst(DAY_END_SHORT, ""); 
		}
		return date.trim(); 
	}
	
	/**
	 * @return end date of an event in human time,
	 * without stripping off 23:59 
	 */
	public String getEndDateFull() {
		return datesHuman[2]; 
	}
	
	/**
	 * @return end date of an event in epoch time
	 */
	public long getEndDateEpoch() {
		return datesEpoch[2]; 
	}
	
	/**
	 * @return human deadline without 23:59 
	 */
	public String getDeadline() {
		String date = datesHuman[3]; 
		
		if (date.contains(DAY_END_SHORT)) {
			date = date.replaceFirst(DAY_END_SHORT, ""); 
		}
		return date.trim(); 
	}
	
	/**
	 * @return human deadline with 23:59
	 */
	public String getDeadlineFull() {
		return datesHuman[3]; 
	}
	
	/**
	 * @return deadline in epoch time 
	 */
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
	
	/**
	 * @return priority of the task. where 1 is the lowest priority and
	 * 3 is the highest priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Set the priority of the task, where 1 is the lowest priority and
	 * 3 is the highest priority 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority; 
	}
	
	//NON-BASIC METHODS ==========================================
	
	/**
	 * @return Start and End time of an event in human readable
	 * form, without 23:59 
	 */
	public String[] getEventTime() {
		String[] eventTime = {getStartDate(),getEndDate()};
		return eventTime; 
	}
	
	/**
	 * @return Start and End time of an event in human readable
	 * form, with 23:59 
	 */
	public String[] getEventTimeFull() {
		String[] eventTime = {getStartDateFull(),getEndDateFull()};
		return eventTime; 
	}
	
	/**
	 * @return Start and End time of an event in epoch form 
	 */
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
	
	@Override
	/**
	 * tasks are comparable by their start time. 
	 * used for sorting in ArrayList<Task>
	 * So that one can easily just call Collections.sort(taskList) 
	 */
	public int compareTo(Task anotherTask) {
		long startTime = -1; 
		if (this.taskType.compareTo("EVENT") == 0) {
			startTime = getStartDateEpoch(); 
		} else if (this.taskType.compareTo("DEADLINE") == 0) { 
			startTime = getDeadlineEpoch(); 
		}
		
		String otherTaskType = anotherTask.getTaskType(); 
		String otherTaskName = anotherTask.getTaskName(); 
		int otherTaskPriority = anotherTask.getPriority(); 
		long otherStartTime = -1; 
		if (anotherTask.getTaskType().compareTo("EVENT") == 0) {
			otherStartTime = anotherTask.getStartDateEpoch(); 
		} else if (anotherTask.getTaskType().compareTo("DEADLINE") == 0) { 
			otherStartTime = anotherTask.getDeadlineEpoch(); 
		}
		//1. Sort by Priority
		//2. Sort by type (Event and Deadline first, floating behind
		//3. Sort by task name (alphabetical order) 
		
		if (this.priority > otherTaskPriority) {
			return 1; 
		} else if (this.priority == otherTaskPriority) {
			return compareByTaskType(startTime, otherTaskType, otherTaskName, otherStartTime);
		} else {
			return -1; //this.priority < otherTaskPriority 
		}
	}
	
	/**
	 * Compare the priority of the tasks based on whether it is floating, deadline or event
	 * @param startTime
	 * @param otherTaskType
	 * @param otherTaskName
	 * @param otherStartTime
	 * @return 1 if this task is "greater" than the other Task, 0 if they are the same,
	 * and -1 if this task is "lesser" then the other Task 
	 */
	private int compareByTaskType(long startTime, String otherTaskType, String otherTaskName, long otherStartTime) {
		//FLOATING HAS THE LOWEST PRIORITY : compare by name 
		if (this.taskType.compareTo("FLOATING") == 0 && otherTaskType.compareTo("FLOATING") == 0) {
			return compareTaskNames(otherTaskName);
		} else if (this.taskType.compareTo("FLOATING") == 0 && otherTaskType.compareTo("EVENT") == 0) {
			return -1; 
		} else if (this.taskType.compareTo("FLOATING") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return -1; 
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("FLOATING") == 0) { 
			return 1; 
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return compareNonFloating(startTime, otherTaskName, otherStartTime);
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("EVENT") == 0) {
			return compareNonFloating(startTime, otherTaskName, otherStartTime);
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("FLOATING") == 0) { 
			return 1; 
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("EVENT") == 0) {
			return compareNonFloating(startTime, otherTaskName, otherStartTime);
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return compareNonFloating(startTime, otherTaskName, otherStartTime);
		} 
		return 0; //shouldn't get here. 
	}

	/**
	 * Compare Events and Deadline:
	 * 1) Based on Time
	 * 2) Based on name, if time is the same 
	 * @param startTime
	 * @param otherTaskName
	 * @param otherStartTime
	 * @return
	 */
	private int compareNonFloating(long startTime, String otherTaskName, long otherStartTime) {
		if (startTime > otherStartTime) {
			return 1;
		} else if (startTime == otherStartTime) {
			return compareTaskNames(otherTaskName);
		} else {
			return -1; 
		}
	}
	
	/**
	 * Compare events, floatings or deadlines based on their task names
	 * @param otherTaskName
	 * @return
	 */
	private int compareTaskNames(String otherTaskName) {
		//compare by name 
		if (this.taskName.compareTo(otherTaskName) == 0) {
			return 0; 
		} else if (this.taskName.compareTo(otherTaskName) > 0) {
			return 1; //this task name is greater than the other task name 
		} else {
			return -1; //less impt than the other task name
		}
	}
	
	
	/**
	 * @@author A0134177E
	 * Two Tasks are considered to be equal if and only if they have the same name, task type, and dates.
	 * used for UPDATE_BY_NAME and DELETE_BY_NAME
	 * @param anotherTask
	 * @return true if the two Tasks are equal
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Task)) {
		    return false;
		  }
		
		Task other = (Task) obj;
		
		// All Task objects are created by Parser, and should not have a null task name or task type
		assert(taskName != null && other.taskName != null); 
		assert(taskType != null && other.taskType != null);
		
		if (taskName.equals(other.taskName) && taskType.equals(other.taskType)) {
			return (Arrays.equals(datesEpoch, other.datesEpoch) && Arrays.equals(datesHuman, other.datesHuman));
		}
		
		return false;
	}
	
	// Returns true if and only if the task is expired, according to the current time on the user's computer clock.
	public boolean isExpired() {
		long currTime = timeConverter.getCurrTime();
		
		if (taskType.equals("DEADLINE")) { // TODO: remove magic strings
			long deadline = getDeadlineEpoch();
			return (deadline < currTime);
		} else if (taskType.equals("EVENT")) {
			long endDate = getEndDateEpoch();
			return (endDate < currTime);
		} else { // Floating tasks are never expired
			return false;
		}
	}
	
	// Returns true if and only if the task is occurring this week, according to the current time on the user's computer 
	// clock.
	public boolean isThisWeek() {
		long currTime = timeConverter.getCurrTime();
		
		if (taskType.equals("DEADLINE")) { // TODO: remove magic strings
			long deadline = getDeadlineEpoch();
			return (timeConverter.isSameWeek(currTime, deadline));
		} else if (taskType.equals("EVENT")) {
			long startDate = getStartDateEpoch();
			return (timeConverter.isSameWeek(currTime, startDate));
		} else { // Floating tasks are never this week
			return false;
		}
	}
	
	@Override 
	/**
	 * @@author A0107345L 
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
			for(int i = 0; i < taskTags.size(); i++) {
				stringRep += taskTags.get(i) + ", "; 
			}
			stringRep += "\n";
		}
		
		if (priority > 1) {
			stringRep += "priority: " + priority + "\n";
		}
		
		return stringRep; 
	}
	
	/* @@author A0134177E
	 * To test whether the getDuplicate() method returns a deep copy of a given Task.
	public static void main(String[] args) {
		Task t1 = new Task("t1");
		t1.setTaskType("FLOATING");
		t1.addTaskTag("meow");
		Task t2 = t1.getDuplicate();
		t2.setTaskName("t2");
		t2.setTaskType("DEADLINE");
		t2.addTaskTag("crap");
		System.out.println("t1: " + t1);
		System.out.println("t2: " + t2);
	}*/
}