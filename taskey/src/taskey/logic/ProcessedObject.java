package taskey.logic;

/**
 * This class will be used for facilitating transfer of a processed task from 
 * Parser to Logic. 
 * 
 * command types:
 * 1. ADD_FLOATING
 * 2. ADD_DEADLINE
 * 3. ADD_EVENT
 * 4. ADD_RECURRING
 * 5. DELETE
 * 6. UPDATE
 * 7. VIEW 
 * 
 * @author Xue Hui
 *
 */
public class ProcessedObject {
	private String command;
	private Task task; 
	private int deleteIndex = -1; //only used if command is delete 
	
	//CONSTRUCTORS ====================================================
	public ProcessedObject(String command, Task task) {
		this.command = command;
		this.task = task; 
	}
	
	public ProcessedObject(String command, Task task, int deleteIndex) {
		this.command = command;
		this.task = task; 
		this.deleteIndex = deleteIndex; 
	}
	
	//=================================================================
	
	//Corresponding GET/SET methods ===================================
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command; 
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task; 
	}
	
	public int getDeleteIndex() {
		return deleteIndex; 
	}
	
	public void setDeleteIndex(int deleteIndex) {
		this.deleteIndex = deleteIndex; 
	}

}
