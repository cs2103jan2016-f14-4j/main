package taskey.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.Task;

/**
 * This class is to allow Storage to easily retrieve the last successfully saved tasklist/tagmap from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to undo the last operation so that its data remains in sync with Storage.
 *
 * For now, this class is only meant to be used by Storage for the above purpose.
 * But, in the future, if more methods are added to this class, Logic could also use this for the undo command.
 * TODO: multiple undos/redos using stacks?
 *
 * @author Dylan
 */
public class History {
	private ArrayDeque<ArrayList<ArrayList<Task>>> taskStack; //tasklist history
	private ArrayDeque<HashMap<String, Integer>> tagStack; //tagmap history
	private static History instance;

	public History() {
		taskStack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		tagStack = new ArrayDeque<HashMap<String, Integer>>();
	}

	/**
	 * Logic can get the History singleton, if it uses this class in the future.
	 * @return the history singleton
	 */
	public static History getInstance() {
		if (instance == null) {
			instance = new History();
		}
		return instance;
	}


	/*========*
	 * Adders *
	 *========*/
	/**
	 * Pushes a superlist to History.
	 * @param superlist to be added
	 */
	public void add(ArrayList<ArrayList<Task>> superlist) {
		taskStack.push(superlist);
	}

	/**
	 * Pushes a tagmap to History.
	 * @param tagmap to be added
	 */
	public void add(HashMap<String, Integer> tagmap) {
		tagStack.push(tagmap);
	}


	/*=========*
	 * Getters *
	 *=========*/
	/**
	 * Peeks at the last added superlist in History.
	 * @return the last added superlist to History
	 */
	public ArrayList<ArrayList<Task>> getLastSuperlist() {
		return taskStack.peek();
	}

	/**
	 * Peeks at the last added tagmap in History.
	 * @return the last added tagmap to History
	 */
	public HashMap<String, Integer> getLastTagmap() {
		return tagStack.peek();
	}


	/*==========*
	 * For undo *
	 *==========*/
	/**
	 * Pop the last added superlist from History.
	 * Used for undo.
	 * @return the removed superlist
	 */
	public ArrayList<ArrayList<Task>> popSuperlist() {
		return taskStack.pop();
	}

	/**
	 * Pop the last added tagmap from History.
	 * Used for undo.
	 * @return the removed tagmap
	 */
	public HashMap<String, Integer> popTagmap() {
		return tagStack.pop();
	}

}