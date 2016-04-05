package taskey.messenger;

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
	 * Tasks are comparable by their start time. 
	 * Used for sorting in ArrayList<Task>
	 * So that one can easily just call Collections.sort(taskList) 
	 */
	public int compareTo(Task anotherTask) {
		long startTime = -1; 
		long endTime = -1; 
		if (this.taskType.compareTo("EVENT") == 0) {
			startTime = getStartDateEpoch(); 
			endTime = getEndDateEpoch(); 
		} else if (this.taskType.compareTo("DEADLINE") == 0) { 
			startTime = getDeadlineEpoch(); 
		}
		
		String otherTaskType = anotherTask.getTaskType(); 
		String otherTaskName = anotherTask.getTaskName(); 
		int otherTaskPriority = anotherTask.getPriority(); 
		long otherStartTime = -1; 
		long otherEndTime = -1; 
		if (anotherTask.getTaskType().compareTo("EVENT") == 0) {
			otherStartTime = anotherTask.getStartDateEpoch(); 
			otherEndTime = anotherTask.getEndDateEpoch(); 
		} else if (anotherTask.getTaskType().compareTo("DEADLINE") == 0) { 
			otherStartTime = anotherTask.getDeadlineEpoch(); 
		}
		
		//1. Sort by Priority
		//2. Sort by type (Event and Deadline first, floating behind)
		//3. Sort by task name (alphabetical order) 
		
		if (this.priority > otherTaskPriority) {
			return 1; 
		} else if (this.priority == otherTaskPriority) {
			return compareByTaskType(startTime, endTime, otherTaskType, 
					otherTaskName, otherStartTime, otherEndTime);
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
	private int compareByTaskType(long startTime, long endTime,
			String otherTaskType, String otherTaskName, long otherStartTime, long otherEndTime) {
		//FLOATING HAS THE LOWEST PRIORITY : compare by name 
		if (this.taskType.equals("FLOATING") && otherTaskType.equals("FLOATING")) {
			return compareTaskNames(otherTaskName);
		} else if (this.taskType.equals("FLOATING") && otherTaskType.equals("EVENT")) {
			return -1; 
		} else if (this.taskType.compareTo("FLOATING") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return -1; 
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("FLOATING") == 0) { 
			return 1; 
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return compareNonFloating(startTime, endTime, otherTaskName, otherTaskType,
					otherStartTime, otherEndTime);
		} else if (this.taskType.compareTo("EVENT") == 0 && otherTaskType.compareTo("EVENT") == 0) {
			return compareNonFloating(startTime, endTime, otherTaskName, otherTaskType,
					otherStartTime, otherEndTime);
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("FLOATING") == 0) { 
			return 1; 
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("EVENT") == 0) {
			return compareNonFloating(startTime, endTime, otherTaskName, otherTaskType,
					otherStartTime, otherEndTime);
		} else if (this.taskType.compareTo("DEADLINE") == 0 && otherTaskType.compareTo("DEADLINE") == 0) {
			return compareNonFloating(startTime, endTime, otherTaskName, otherTaskType,
					otherStartTime, otherEndTime);
		} 
		return 0; //shouldn't get here. 
	}

	/**
	 * Compare Events and Deadline:
	 * 1) Based on Time
	 * 2) Based on name, if time is the same 
	 * @param startTime
	 * @param endTime
	 * @param otherTaskName
	 * @param otherTaskType
	 * @param otherStartTime
	 * @param otherEndTime 
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareNonFloating(long startTime, long endTime, String otherTaskName, String otherTaskType,
			long otherStartTime, long otherEndTime) {
		
		if (this.taskType.equals("EVENT") && otherTaskType.equals("DEADLINE")) {
			return compareEventDeadline(startTime, endTime, otherTaskName, otherStartTime); 
		} else if (this.taskType.equals("EVENT") && otherTaskType.equals("EVENT")) {
			return compareTwoEvents(startTime, endTime, otherTaskName, otherStartTime, otherEndTime);
		} else if (this.taskType.equals("DEADLINE") && otherTaskType.equals("EVENT")) {
			return compareDeadlineEvent(startTime, otherTaskName, otherStartTime, otherEndTime); 
		} else { //this.taskType == Deadline && other.taskType == deadline 
			return compareTwoDeadlines(startTime, otherTaskName, otherStartTime);
		}
	}
	
	/**
	 * This Task is a Deadline, and the other task is an Event.
	 * Do a comparison based on start and end times. 
	 * @param deadlineTime
	 * @param otherTaskName
	 * @param eventStartTime
	 * @param eventEndTime
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareDeadlineEvent(long deadlineTime, String otherTaskName, long eventStartTime, long eventEndTime) {
		if (deadlineTime < eventEndTime) {
			return 1; //deadline ends earlier, so it gets a higher priority
		} else if (deadlineTime == eventEndTime) {
			if (deadlineTime < eventStartTime) {
				return 1; //deadline ends before (other) eventStartTime, so deadline gets higher priority
			} else if (eventStartTime == deadlineTime) {
				return compareTaskNames(otherTaskName); 
			} else {
				return -1; 
			}
		} else {
			return -1; 
		}
	}
	
	/**
	 * This Task is an Event, and the other task is a deadline.
	 * Do a comparison based on start and end times. 
	 * @param eventStartTime
	 * @param eventEndTime
	 * @param otherTaskName
	 * @param deadlineTime
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareEventDeadline(long eventStartTime, long eventEndTime, String otherTaskName, long deadlineTime) {
		if (eventEndTime > deadlineTime) {
			return -1; //deadline ends earlier, so it gets a higher priority
		} else if (eventEndTime == deadlineTime) {
			if (eventStartTime > deadlineTime) {
				return -1; //event starts after deadline, so deadline gets higher priority
			} else if (eventStartTime == deadlineTime) {
				return compareTaskNames(otherTaskName); 
			} else {
				return 1; 
			}
		} else {
			return 1; 
		}
	}
	
	/**
	 * Compare the time priority for 2 events. If they start at the same time,
	 * the event which ends earlier gets higher priority. 
	 * @param startTime
	 * @param endTime
	 * @param otherTaskName
	 * @param otherStartTime
	 * @param otherEndTime
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareTwoEvents(long startTime, long endTime, String otherTaskName, 
			long otherStartTime, long otherEndTime) {
		if (startTime > otherStartTime) {
			return -1; //this task starts later, so it gets less priority
		} else if (startTime == otherStartTime) {
			if (endTime < otherEndTime) {
				return 1; //this task ends earlier, so it gets more priority 
			} else if (endTime == otherEndTime) {
				return compareTaskNames(otherTaskName);
			} else {
				return -1; 
			}
		} else {
			return 1; 
		}
	}

	/**
	 * Compare two start times for 2 deadline tasks 
	 * @param startTime
	 * @param otherTaskName
	 * @param otherStartTime
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareTwoDeadlines(long endTime, String otherTaskName, long otherEndTime) {
		if (endTime > otherEndTime) {
			return -1; //this task ends later, so it gets less priority 
		} else if (endTime == otherEndTime) {
			return compareTaskNames(otherTaskName);
		} else {
			return 1; 
		}
	}
	
	/**
	 * Compare events, floating or deadlines based on their task names
	 * ie. arrange in alphabetical order 
	 * @param otherTaskName
	 * @return 0 if equal, 1 if this task is greater, -1 if this task is lesser
	 */
	private int compareTaskNames(String otherTaskName) {
		//compare by name 
		if (this.taskName.compareTo(otherTaskName) == 0) {
			return 0; 
		} else if (this.taskName.compareTo(otherTaskName) > 0) {
			//this task name is greater than the other task name and should be ordered in front,
			//so that sortReverse() will return it in alphabetical order. 
			//eg. this is Zephyr, that is Wine, then that should come before this. 
			return -1; 
		} else {
			return 1; //less impt than the other task name
		}
		//return taskName.compareTo(otherTaskName);
	}
	
	// @@author A0134177E
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(datesEpoch);
		result = prime * result + Arrays.hashCode(datesHuman);
		result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
		result = prime * result + ((taskType == null) ? 0 : taskType.hashCode());
		
		return result;
	}

	// Two Tasks are considered to be equal if and only if they have the same name, task type, and dates.
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
		
		Task other = (Task) obj;
		
		if (taskName == null) {
			if (other.taskName != null) {
				return false;
			}
		} else if (!taskName.equals(other.taskName)) {
			return false;
		}
		
		if (taskType == null) {
			if (other.taskType != null) {
				return false;
			}
		} else if (!taskType.equals(other.taskType)) {
			return false;
		}
		
		if (!Arrays.equals(datesEpoch, other.datesEpoch)) {
			return false;
		}
		
		if (!Arrays.equals(datesHuman, other.datesHuman)) {
			return false;
		}
			
		return true;
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
		} else if (taskType.equals("EVENT")) { // An event is considered to be occurring this week if its start date or
			                                   // end date is within the current week, or the current time is between
			                                   // the start date and end date.
			long startDate = getStartDateEpoch();
			long endDate = getEndDateEpoch();
			return (timeConverter.isSameWeek(currTime, startDate) || timeConverter.isSameWeek(currTime, endDate)
					|| (startDate <= currTime && currTime <= endDate));
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