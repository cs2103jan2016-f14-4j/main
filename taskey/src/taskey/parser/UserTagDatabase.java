package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import taskey.storage.Storage;

/**
 * @@author A0107345L
 * Purpose of this class is to handle the storage and retrieval
 * of userTags, so that they can be displayed by Logic as 
 * categories that users can view their tasks by.
 * Used by ParseView
 * @author Xue Hui
 *
 */
public class UserTagDatabase {
	//public static final int MAX_TAGS = 15; 
	HashMap<String,Integer> userTags = new HashMap<String,Integer>(); 
	Storage db = new Storage(); 
	
	public UserTagDatabase() {
		//initialise the database of tags. 
		userTags = db.loadTags();  
	}
	
	/**
	 * Removes all tags from the userTagDatabase
	 */
	public void deleteAllTags() {
		userTags.clear();
	}
	
	/**
	 * Add a new tag to the userTagDatabase
	 * @param tag
	 */
	public void addTag(String tag) {
		if (!userTags.containsKey(tag)) {
			userTags.put(tag,new Integer(1)); 
		} else {
			int temp = userTags.get(tag) + 1; 
			userTags.put(tag, temp); 
		}
	}
	
	/**
	 * Remove a tag from the userTag Database.
	 * Called when tasks with all these tags are deleted 
	 * @param tag
	 * @return true if successfully removed
	 */
	public boolean removeTag(String tag) {
		if (userTags.containsKey(tag)) {
			int temp = userTags.get(tag) - 1;
			if (temp <= 0) {
				userTags.remove(tag); 
			} else {
				//there are still tasks with that tag
				userTags.put(tag, temp); 
			}
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
		if (userTags.containsKey(tag)) {
			return true;
		}	
		return false; 
	}
	
	/**
	 * For Logic: Get the entire tagList so that 
	 * it can be displayed by the UI.
	 * Remember to call this function every time a task tag is added/removed,
	 * cos the main form is a HashMap...
	 * @return
	 */
	public ArrayList<String> getTagList() {
		ArrayList<String> tagList = new ArrayList<String>(); 
		
		if (!userTags.isEmpty()) {
			Iterator<Entry<String, Integer>> itKeys = userTags.entrySet().iterator(); 
			
			while(itKeys.hasNext()) {
				Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>) itKeys.next(); 
				tagList.add(pair.getKey()); 
			}
		}
		return tagList; 
	}
	
	/**
	 * For Logic: Get the sizes of each tag category so that 
	 * it can be displayed by the UI.
	 * Remember to call this function every time a task tag is added/removed,
	 * cos the main form is a HashMap...
	 * @return
	 */
	public ArrayList<Integer> getTagSizes() {
		ArrayList<Integer> tagSizes = new ArrayList<Integer>(); 
		
		if (!userTags.isEmpty()) {
			Iterator<Entry<String, Integer>> itKeys = userTags.entrySet().iterator(); 
			
			while(itKeys.hasNext()) {
				Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>) itKeys.next(); 
				tagSizes.add(pair.getValue()); 
			}
		}
		return tagSizes; 
	}
  
	/**
	 * Save the tag hash map into a file for persistent storage. 
	 * @return true if save was successful; false otherwise
	 */
	public boolean saveTagDatabase() {
		return db.saveTags(userTags); 
	}
    
	
    /*
     * FOR DEBUGGING
     */
    
	@Override
	public String toString() {
		String stringRep = "";
		if (!userTags.isEmpty()) {
			Iterator<Entry<String, Integer>> itKeys = userTags.entrySet().iterator(); 
			
			while(itKeys.hasNext()) {
				Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>) itKeys.next(); 
				stringRep += pair.getKey() + ","; 
			}
		}
		return stringRep; 
	}
	
	/*
	public static void main(String[] args) {
		UserTagDatabase db = new UserTagDatabase(); 
		db.addTag("hello");
		db.addTag("monkey");
		System.out.println(db);
		db.saveTagDatabase(); 
	} */
}
