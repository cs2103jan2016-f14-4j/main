package taskey.logic;

public class ProcessedObject {
	private String command;
	private Task task; 
	
	public ProcessedObject(String command, Task task) {
		this.command = command;
		this.task = task; 
	}
	
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

}
