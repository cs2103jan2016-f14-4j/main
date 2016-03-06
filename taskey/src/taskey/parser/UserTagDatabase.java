package taskey.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import taskey.logic.Task;

public class UserTagDatabase {
	//public static final int MAX_TAGS = 15; 
	private static final String DEFAULT_FILENAME = "user_tag_db";
	private File savefile = new File(DEFAULT_FILENAME);
	ArrayList<String> userTags = new ArrayList<String>(); 
	
	public UserTagDatabase() {
		//initialise the database of tags. 
		loadDatabase(); 
	}
	
	private void loadDatabase() {
		//load the file. 
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(savefile));
			@SuppressWarnings("unchecked")
			ArrayList<String> userTagsTemp = (ArrayList<String>) ois.readObject();
	    	this.userTags = userTagsTemp; 
	    	ois.close();
		} catch (Exception e) {
			//if no database, do nothing.
			//file will be created later. 
		} 
	}
	
	/**
	 * Add a new tag to the userTagDatabase
	 * @param tag
	 */
	public void addTag(String tag) {
		if (!userTags.contains(tag)) {
			userTags.add(tag); 
		}
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
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(savefile));
			oos.writeObject(userTags);
	    	oos.close();
			return true; 
		} catch (Exception e) {
			if (!savefile.exists()) {
				try {
					savefile.createNewFile();
					oos = new ObjectOutputStream(new FileOutputStream(savefile));
					oos.writeObject(userTags);
			    	oos.close();
				} catch (IOException e1) {
					//else do nothing
				} 
			}
			return true; 
		}
	}
	
	@Override
	public String toString() {
		String stringRep = "";
		if (!userTags.isEmpty()) {
			for(int i = 0; i < userTags.size(); i++) {
				stringRep += userTags.get(i) + ","; 
			}
		}
		return stringRep; 
	}
	
	public static void main(String[] args) {
		UserTagDatabase db = new UserTagDatabase(); 
		db.addTag("hello");
		db.addTag("newworld");
		System.out.println(db);
		db.saveTagDatabase(); 
	}
}
