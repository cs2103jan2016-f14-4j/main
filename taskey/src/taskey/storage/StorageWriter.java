package taskey.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.TagCategory;
import taskey.logic.Task;

/**
 * @author Dylan
 */
class StorageWriter {
    /**
     * Generic write method. Serializes the given object of the specified type into its equivalent JSON representation.
     * @param dest the JSON file to be written
     * @param object of type T to be serialized
     * @param typeToken represents the generic type T of the given object; this is obtained from the Gson TypeToken class
     * @throws IOException
     */
    private <T> void writeToFile(File dest, T object, TypeToken<T> typeToken) throws IOException {
    	FileWriter writer = new FileWriter(dest);
    	Gson gson = new Gson();
    	String json = gson.toJson(object, typeToken.getType());
    	writer.write(json);
    	writer.close();
    }

    /*================*
     * Save directory *
     *================*/
    /**
     * Writes the given directory to a config file located in System.getProperty("user.dir").
     * @param dir the File representing the directory to be saved
     * @param filename name of the destination file
     */
    void saveDirectory(File dir, String filename) {
    	File dest = new File(filename);
    	try {
    		writeToFile(dest, dir, new TypeToken<File>() {});
    		System.out.println("{New storage directory saved}"); //debug info
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.out.println("{Error saving new directory}"); //debug info
    	}
    }

    /*================*
     * Save task list *
     *================*/
    /**
     * Writes an ArrayList of Task objects to the File dest.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * @param tasks the tasklist to be saved
     * @param dest the destination file to be written to
     * @throws IOException when there is error writing to file
     */
    void saveTasklist(ArrayList<Task> tasks, File dest) throws IOException {
		try {
			writeToFile(dest, tasks, new TypeToken<ArrayList<Task>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }
    
    /*========================*
     * Save tags - New method *
     *========================*/
    /**
     * Saves the given ArrayList of Tags to the File dest.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * @param tags ArrayList containing the user-defined tags
     * @param dest the destination file to be written to
     * @throws IOException when there is error writing to file
     */
    void saveTaglist(ArrayList<TagCategory> tags, File dest) throws IOException {
    	try {
    		writeToFile(dest, tags, new TypeToken<ArrayList<TagCategory>>() {});
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw e;
    	}
    }

    /*============================*
     * Save tags - Leagacy method *
     *============================*/
    /**
     * Deprecated
     * Saves the given HashMap containing user-defined tags to the File dest.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * @param tags HashMap that maps tag strings to their corresponding multiplicities
     * @param dest the destination file to be written to
     * @throws IOException when there is error writing to file
     */
    void saveTags(HashMap<String, Integer> tags, File dest) throws IOException {
    	try {
    		writeToFile(dest, tags, new TypeToken<HashMap<String, Integer>>() {});
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw e;
    	}
    }
}