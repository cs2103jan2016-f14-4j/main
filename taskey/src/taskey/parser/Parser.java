package taskey.parser;

import taskey.ui.UiController;
import taskey.ui.UiEventHandler;
import taskey.ui.UiTimeDisplay;

public class Parser {
	private static Parser instance = null; 

    public static Parser getInstance () { 
    	if ( instance == null ) {
    		instance = new Parser();
    	}
    	return instance;
    }
    
    public boolean parseInput(String line) {
    	
    	// find out what kind of input, call logic accordingly
    	// Logic.getInstance().do ...
    	return true;
    }
}
