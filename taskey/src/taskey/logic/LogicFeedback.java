package taskey.logic;

import java.util.ArrayList;

import taskey.messenger.ProcessedObject;
import taskey.messenger.Task;

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
	private LogicException le;
	
	/** This is the only constructor for the LogicFeedback class. taskLists and po should not be null.
	 * 
	 * @param taskLists  a list of task lists for the purposes of updating the UI display
	 * @param po         an object encapsulating information on the executed command and its associated task
	 * @param le         an exception containing a message to reflect the outcome of command execution. It can be null.
	 */
	public LogicFeedback(ArrayList<ArrayList<Task>> taskLists, ProcessedObject po, LogicException le) {
		assert (taskLists != null);
		assert (taskLists.size() == 8); //taskLists should be fully initialized
		assert (!taskLists.contains(null)); //All lists should be instantiated
		assert (po != null);
		
		this.taskLists = taskLists;
		this.po = po;
		this.le = le;
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

	public LogicException getException() {
		return le;
	}
	
	protected void setException(LogicException le) {
		this.le = le;
	}
}
