package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;

/**
 * @@author A0121618M
 */
class StorageWriter {
	/**
	 * Generic write method. Serializes the given object of the specified type into its equivalent JSON representation.
	 * @param dest the JSON file to be written
	 * @param object of type T to be serialized
	 * @param typeToken represents the generic type T of the given object; this is obtained from the Gson TypeToken class
	 * @throws IOException thrown by FileWriter
	 */
	private <T> void writeToFile(File dest, T object, TypeToken<T> typeToken) throws IOException {
		FileWriter writer = new FileWriter(dest);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonOutput = gson.toJson(object, typeToken.getType());
		writer.write(jsonOutput);
		writer.close();
	}

	/*================*
	 * Save directory *
	 *================*/
	/**
	 * Writes the given abstract path to a config file located in System.getProperty("user.dir").
	 * @param dir the File representing the directory to be saved
	 * @param filename name of the destination file
	 */
	void saveDirectoryConfigFile(File dir, String filename) {
		File dest = new File(filename);
		try {
			writeToFile(dest, dir.getCanonicalFile(), new TypeToken<File>() {});
			//System.out.println("{New storage directory saved}");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("{Error saving new directory}");
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
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
			throw e;
		}
	}

	/*===========*
	 * Save tags *
	 *===========*/
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
}