package taskey.logic;

import taskey.parser.Parser;

public class Logic {

	private static Logic instance = null; 

    public static Logic getInstance () { 
    	if ( instance == null ) {
    		instance = new Logic();
    	}
    	return instance;
    }
 
    public void getCommand(String line) {
    	// create task object from line, pass to parser
    	Parser.getInstance().parseInput(new Task());
    	// Update UI 
    	// UiController.getInstance().update();
    }
}
