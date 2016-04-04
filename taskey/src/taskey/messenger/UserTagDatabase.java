package taskey.messenger;

import java.util.ArrayList;
import java.util.Iterator;

import taskey.storage.Storage;

/**
 * @@author A0107345L-unused
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
	Storage db; 
	
	//@@author A0134177E-unused
	public UserTagDatabase(Storage storage) {
		db = storage;
		//initialise the database of tags. 
		userTags = cloneTagList(db.loadTaglist());  
	}
	
	public void setTags(ArrayList<TagCategory> tagList) {
		assert(tagList != null);
		userTags = cloneTagList(tagList);
	}
	
	/**
	 * Removes all tags from the userTagDatabase
	 */
	public void deleteAllTags() {
		userTags.clear();
	}
	
	/**
	 * @@author A0107345L-unused
	 * Add a new tag to the userTagDatabase
	 * @param tag
	 */
	public void addTag(String tag) {
		TagCategory newTag = new TagCategory(tag); 
		
		if (!containsTagName(tag)) {
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
		
		if (containsTagName(tag)) {
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
	
	//@@author A0134177E-unused
	public boolean removeTagCategory(String tag) {
		for (Iterator<TagCategory> it = userTags.iterator(); it.hasNext();) {
			TagCategory tc = it.next();
			
			if (tc.getTagName().equals(tag)) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @@author A0107345L-unused
	 * For Logic: Get the entire tagList so that 
	 * it can be displayed by the UI.
	 * @return
	 */
	public ArrayList<TagCategory> getTagList() {
		return cloneTagList(userTags);
	}
	
	/**
	 * @@author A0134177E-unused
	 * Save the tag hash map into a file for persistent storage. 
	 * @return true if save was successful; false otherwise
	 */
	/*public boolean saveTagDatabase() {
		try {
			db.saveTaglist(cloneTagList(userTags));
			return true;
		} catch (IOException e) {
			userTags = cloneTagList(db.getHistory().peekTags()); //To revert changes to userTags
			return false; 
		} 
	}*/
	
	public ArrayList<TagCategory> cloneTagList(ArrayList<TagCategory> tagList) {
		ArrayList<TagCategory> clone = new ArrayList<TagCategory>();
		for (TagCategory tc : tagList) {
			clone.add(new TagCategory(tc));
		}
		
		return clone;
	}
	
	/**
	 * Checks if the user tag database has a particular tag
	 * @param tag
	 * @return true if the tag exists in database
	 */
	public boolean containsTagName(String name) {
		for (TagCategory tc : userTags) {
			if (tc.getTagName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
   
    /*
     * @@author A0107345L-unused
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
