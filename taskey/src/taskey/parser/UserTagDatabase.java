package taskey.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Purpose of this class is to handle the storage and retrieval
 * of userTags, so that they can be displayed by Logic as 
 * categories that users can search for
 * @author Xue Hui
 *
 */
public class UserTagDatabase {
	//public static final int MAX_TAGS = 15; 
	private static final String DEFAULT_FILENAME = "user_tag_db";
	private File savefile = new File(DEFAULT_FILENAME);
	HashMap<String,Integer> userTags = new HashMap<String,Integer>(); 
	
	public UserTagDatabase() {
		//initialise the database of tags. 
		loadDatabase(); 
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
	 * it can be displayed by the UI 
	 * @return
	 */
	public HashMap<String,Integer> getTagList() {
		return userTags; 
	}
	
	
	/*
	 * LOAD DATABASE  
	 */
	
	/**
	 * Load database of userTags into program
	 */
	private void loadDatabase() {
		//load the file. 
		try {
			userTags = readFromFile(new TypeToken<HashMap<String,Integer>>(){});
			//System.out.println("<Loaded> " + savefile.getPath());
		} catch (Exception e) {
			//do nothing
		}
	}
	
	/**
     * Private method. Reads tasks from JSON file.
     * @return ArrayList of Task objects generated from the JSON file.
     * @throws FileNotFoundException
     */
    private <T> T readFromFile(TypeToken<T> typeToken) throws FileNotFoundException {
    	Gson gson = new Gson();
    	FileReader reader = new FileReader(savefile);
		T object = gson.fromJson(reader, typeToken.getType());
		return object;
    }
    
    
    /*
     * SAVE DATABASE
     */
    
    /**
	 * Called when the program quits, so that the entire tag list 
	 * can be written to file.  
	 * @return true if saved successfully. 
	 */
	public boolean saveTagDatabase() {
		try {
			writeToFile(userTags);
			return true; 
		} catch (IOException e) {
			return false; 
		}
	}
    
	/**
     * Private method. Writes an Object to JSON file.
     * @param object to be written as a JSON file.
     * @throws IOException
     */
    private <T> void writeToFile(T object) throws IOException {
    	Gson gson = new Gson();
    	String json = gson.toJson(object);
    	FileWriter writer = new FileWriter(savefile);
    	writer.write(json);
    	writer.close();
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
