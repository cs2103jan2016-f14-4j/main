package taskey.storage;

public class Storage {

	private static Storage instance = null; 

    public static Storage getInstance () { 
    	if ( instance == null ) {
    		instance = new Storage();
    	}
    	return instance;
    }
    
    public void readFromFile() {
    	
    }
    
    public void writeToFile() {
    	
    }
}
