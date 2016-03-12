package taskey.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;
import taskey.storage.Storage.Constants;


class StorageSaver {

    /**
     * Generic write method. Serializes the specified object of the specified type into its equivalent JSON representation.
     * @param file to be written as JSON
     * @param object of type T to be serialized
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @throws IOException
     */
    private <T> void writeToFile(File file, T object, TypeToken<T> typeToken) throws IOException {
    	FileWriter writer = new FileWriter(file);
    	Gson gson = new Gson();
    	String json = gson.toJson(object, typeToken.getType());
    	writer.write(json);
    	writer.close();
    }

    /*================*
     * Save task list *
     *================*/
    /**
     * Saves an ArrayList of Task objects to the file specified by the given filename String.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * This method is invoked by Logic.
     * @param tasks list of Task objects for saving
     * @param filename name the file will be saved as
     * @throws StorageException contains the last saved tasklist corresponding to the filename
     */
    void saveTasklist(ArrayList<Task> tasks, File file) throws StorageException {
		try {
			writeToFile(file, tasks, new TypeToken<ArrayList<Task>>() {});
		} catch (IOException e) {
			// When exception is encountered during write-after-modified, throw the last-modified superlist to Logic.
			throw new StorageException(e, History.getInstance().peek());
		}

    }

    /*================*
     * Save directory *
     *================*/
    /**
     * Private method. Saves the current directory to file in "user.dir".
     * @return true if save was successful; false otherwise.
     */
    boolean saveDirectory(File directory) {
    	File configFile = new File(Constants.FILENAME_CONFIG);
    	try {
    		writeToFile(configFile, directory, new TypeToken<File>() {});
    		System.out.println("{Storage directory saved}"); //debug info
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    /*===========*
     * Save tags *
     *===========*/
    /**
     * Saves the given HashMap containing user-defined tags to JSON file.
     * @param tags HashMap that maps the tag strings to their corresponding multiplicities
     * @return true if successful; false otherwise.
     */
    boolean saveTags(HashMap<String, Integer> tags, File file) {
    	try {
    		writeToFile(file, tags, new TypeToken<HashMap<String, Integer>>() {});
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}
