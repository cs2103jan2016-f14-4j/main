package taskey.parser;

import java.util.ArrayList;

public class UserTagDatabase {
	public static final int MAX_TAGS = 15; 
	ArrayList<String> userTags = new ArrayList<String>(); 
	
	public UserTagDatabase() {
		//initialise the database of tags. 
	}
	
	/**
	 * Add a new tag to the userTagDatabase
	 * @param tag
	 */
	public void addTag(String tag) {
		userTags.add(tag); 
	}
	
	/**
	 * Remove a tag from the userTag Database.
	 * Called when tasks with all these tags no longer exist
	 * @param tag
	 * @return true if successfully removed
	 */
	public boolean removeTag(String tag) {
		if (userTags.contains(tag)) {
			userTags.remove(tag); 
			return true; 
		}
		return false;
	}
	
	/**
	 * Checks if the user tag database has a particular tag
	 * @param tag
	 * @return true if the tag exists in database
	 */
	public boolean hasTag(String tag) {
		if (userTags.contains(tag)) {
			return true;
		}
		
		return false; 
	}
	
	/**
	 * Called when the program quits, so that the entire tag list 
	 * can be written to file.  
	 * @return true if saved successfully. 
	 */
	public boolean saveTagDatabase() {
		
		return true; 
	}
}
