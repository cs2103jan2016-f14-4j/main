package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;

/**
 * @author Dylan
 */
class StorageReader {
    /**
     * Generic read method. Deserializes the JSON specified by src into an object of the specified type.
     * @param src JSON file to be read
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @return An object of type T generated from the JSON file.
     * @throws FileNotFoundException if FileReader constructor fails
     */
    private <T> T readFromFile(File src, TypeToken<T> typeToken) throws FileNotFoundException {
    	FileReader reader = new FileReader(src);
    	Gson gson = new Gson();
		T object = gson.fromJson(reader, typeToken.getType()); //TODO Handle type safety
		return object;
    }

    /*================*
     * Load directory *
     *================*/
    /**
     * Tries to read the last-saved directory from a config file located in System.getProperty("user.dir").
     * @param filename name of the config file to be read
     * @return the File representing the last-saved directory, or null if it was not found
     */
    File loadDirectory(String filename) {
    	File src = new File(filename);
    	try {
    		return readFromFile(src, new TypeToken<File>() {});
    	} catch (FileNotFoundException e) {
    		return null;
    	}
    }

    /*================*
     * Load task list *
     *================*/
    /**
     * Returns an ArrayList of Task objects read from the File src.
     * An empty ArrayList is returned if src was not found.
     * @param src the source file to be read from
     * @return the tasklist read from file; or an empty list if the file was not found
     */
    ArrayList<Task> loadTasklist(File src) {
    	ArrayList<Task> tasks;
		try {
			tasks = readFromFile(src, new TypeToken<ArrayList<Task>>() {});
			//System.out.println("{Tasklist loaded} " + src.getName()); //debug info
		} catch (FileNotFoundException e) {
			System.out.println("{Tasklist not found} " + src.getName()); //debug info
			tasks = new ArrayList<Task>();
		}
    	return tasks;
    }
    
    
    /*========================*
     * Load tags - new method *
     *========================*/
    /**
     * Returns an ArrayList of Tags read from the File src.
     * An empty ArrayList is returned if src was not found.
     * @param src source file to be read
     * @return ArrayList containing the user-defined tags, or an empty ArrayList if src was not found
     */
    ArrayList<TagCategory> loadTaglist(File src) {
    	ArrayList<TagCategory> tags = new ArrayList<TagCategory>();
    	try {
    		tags = readFromFile(src, new TypeToken<ArrayList<TagCategory>>() {});
    		//System.out.println("{Tags loaded} " + src.getName()); //debug info
    	} catch (FileNotFoundException e) {
    		System.out.println("{Tags not found} " + src.getName()); //debug info
    		tags = new ArrayList<TagCategory>();
    	}
    	return tags;
    }
    

    /*===========================*
     * Load tags - Legacy method *
     *===========================*/
    /**
     * Returns a HashMap containing the user-defined tags read from the File src.
     * An empty HashMap is returned if src was not found.
     * @param src source file to be read
     * @return the HashMap read from file, or an empty HashMap if the file was not found
     */
    HashMap<String, Integer> loadTags(File src) {
    	HashMap<String, Integer> tags;
    	try {
    		tags = readFromFile(src, new TypeToken<HashMap<String, Integer>>() {});
    		System.out.println("{Tags loaded} " + src.getName()); //debug info
    	} catch (FileNotFoundException e) {
    		System.out.println("{Tags not found} " + src.getName()); //debug info
    		tags = new HashMap<String, Integer>();
    	}
    	return tags;
    }
}