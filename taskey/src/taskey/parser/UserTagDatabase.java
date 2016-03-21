package taskey.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import taskey.logic.TagCategory;
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
	ArrayList<TagCategory> userTags = new ArrayList<TagCategory>(); 
	Storage db = new Storage(); 
	
	public UserTagDatabase() {
		//initialise the database of tags. 
		//userTags = db.loadTags();  reinit this when dylan has changed it to arraylist
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
		TagCategory newTag = new TagCategory(tag); 
		
		if (!userTags.contains(newTag)) {
			userTags.add(newTag); 
		} else {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tagCat = userTags.get(i); 
				if (tagCat.compareTo(newTag) == 0) { 
					tagCat.increaseCount();
					break; 
				}
			}
		}
	}
	
	/**
	 * Remove a tag from the userTag Database.
	 * Called when tasks with all these tags are deleted 
	 * @param tag
	 * @return true if successfully removed
	 */
	public boolean removeTag(String tag) {
		TagCategory toRemove = new TagCategory(tag); 
		
		if (userTags.contains(toRemove)) {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tagCat = userTags.get(i); 
				if (tagCat.compareTo(toRemove) == 0) { 
					tagCat.decreaseCount();
					//if 0 tasks with that tag, remove it from arraylist
					if (tagCat.isEmpty()) {
						userTags.remove(i);  
					}
					break; 
				}
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
		if (userTags.contains(new TagCategory(tag))) {
			return true;
		}	
		return false; 
	}
	
	/**
	 * For Logic: Get the entire tagList so that 
	 * it can be displayed by the UI.
	 * @return
	 */
	public ArrayList<TagCategory> getTagList() {
		return userTags; 
	}
	
	/**
	 * Save the tag hash map into a file for persistent storage. 
	 * @return true if save was successful; false otherwise
	 */
	public boolean saveTagDatabase() {
		//return db.saveTags(userTags); //reinit when dylan has changed to arraylist 
		return false;
	}
    
	
    /*
     * FOR DEBUGGING
     */
	@Override
	public String toString() {
		String stringRep = "";
		if (!userTags.isEmpty()) {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tag = userTags.get(i);
				stringRep += "Tag Name: " + tag.getTagName() + ", ";
				stringRep += "TagCount: " + tag.getNumTags() + "\n"; 
			}
		}
		return stringRep; 
	}
	
	/*
	public static void main(String[] args) {
		UserTagDatabase db = new UserTagDatabase(); 
		db.addTag("hello");
		db.addTag("hello");
		db.addTag("monkey");
		db.addTag("hello");
		db.removeTag("hello");
		System.out.println(db);
		db.removeTag("monkey");
		System.out.println(db);
		//db.saveTagDatabase(); 
	} */ 
}
