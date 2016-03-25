package taskey.logic;

import java.util.ArrayList;

/**
 * @@author A0134177E
 * This class is the default feedback mechanism from Logic to UI.
 * Objects of this class encapsulate ArrayLists of Task objects for updating the UI display,
 * a ProcessedObject which by itself encapsulates information on the executed command and its associated
 * task, and an Exception reflecting the outcome of command execution.
 */
public class LogicFeedback {
	private ArrayList<ArrayList<Task>> taskLists;
	private ProcessedObject po;
	private Exception e;
	
	/** This is the only constructor for the LogicFeedback class. taskLists and po should not be null.
	 * 
	 * @param taskLists  a list of task lists for the purposes of updating the UI display
	 * @param po         an object encapsulating information on the executed command and its associated task
	 * @param e          an exception containing an error message. If not error occurred, this will be null.
	 */
	public LogicFeedback(ArrayList<ArrayList<Task>> taskLists, ProcessedObject po, Exception e) {
		assert (taskLists != null);
		assert (taskLists.size() == 8); //taskLists should be fully initialized
		assert (!taskLists.contains(null)); //All lists should be instantiated
		assert (po != null);
		
		this.taskLists = taskLists;
		this.po = po;
		this.e = e;
	}

	public ArrayList<ArrayList<Task>> getTaskLists() {
		return taskLists;
	}

	protected void setTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		assert (taskLists != null);
		this.taskLists = taskLists;
	}

	public ProcessedObject getPo() {
		return po;
	}

	protected void setPo(ProcessedObject po) {
		assert (po != null);
		this.po = po;
	}

	public Exception getException() {
		return e;
	}
	
	protected void setException(Exception e) {
		this.e = e;
	}
}
