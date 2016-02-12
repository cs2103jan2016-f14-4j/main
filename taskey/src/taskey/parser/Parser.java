package taskey.parser;

import taskey.logic.Task;
import taskey.storage.Storage;

public class Parser {
	private static Parser instance = null; 

    public static Parser getInstance () { 
    	if ( instance == null ) {
    		instance = new Parser();
    	}
    	return instance;
    }
    
    public boolean parseInput(Task myTask) {
    	
    	// find out what kind of task, is it valid
    	// call storage for operations
    	Storage.getInstance().writeToFile(); // or equivalent
    	return true;
    }
}
