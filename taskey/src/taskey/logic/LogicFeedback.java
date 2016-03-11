package taskey.logic;

import java.util.ArrayList;

/**
 * This class is the default feedback mechanism from Logic to UI.
 * Objects of this class encapsulate ArrayLists of Task objects for updating the UI display,
 * a ProcessedObject which by itself encapsulates information on the executed command and its associated
 * task, and an integer status code to reflect the outcome of command execution.
 * 
 * @author Hubert
 */
public class LogicFeedback {
	private ArrayList<ArrayList<Task>> taskLists;
	private ProcessedObject po;
	private int statusCode;
	
	/** This is the only constructor for the LogicFeedback class. taskLists and po should not be null.
	 * 
	 * @param taskLists  a list of task lists for the purposes of updating the UI display
	 * @param po         an object encapsulating information on the executed command and its associated task
	 * @param statusCode reflecting outcome of command execution
	 */
	protected LogicFeedback(ArrayList<ArrayList<Task>> taskLists, ProcessedObject po, int statusCode) {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized
		assert (!taskLists.contains(null)); //All lists should be instantiated
		assert (po != null);
		
		this.setTaskLists(taskLists);
		this.setPo(po);
		this.setStatusCode(statusCode);
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

	public int getStatusCode() {
		return statusCode;
	}
	
	//TODO: assert restrictions on statusCode
	protected void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
