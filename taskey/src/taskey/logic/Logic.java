package taskey.logic;

public class Logic {

	private static Logic instance = null; 

    public static Logic getInstance () { 
    	if ( instance == null ) {
    		instance = new Logic();
    	}
    	return instance;
    }
 
    public void doCommand() {
    	// Call storage if need be
    	// Update UI
    	// UiController.getInstance().update();
    }
}
