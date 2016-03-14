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
	private ArrayDeque<ArrayList<ArrayList<Task>>> stack;
	private ArrayDeque<HashMap<String, Integer>> tagStack; //for exception handling when saving tags

	public History() {
		stack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		tagStack = new ArrayDeque<HashMap<String, Integer>>();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}


	/*========*
	 * Adders *
	 *========*/
	/**
	 * Pushes the given superlist to History.
	 * Has a check that prevents superlist from being added, if all of its constituent lists
	 * are equal to those of the most recently added superlist.
	 * @param superlist to be added
	 */
	public void add(ArrayList<ArrayList<Task>> superlist) {
		ArrayList<ArrayList<Task>> mostRecentSuperlist = stack.peek();
		if (mostRecentSuperlist == null) {
			stack.push(superlist);
			return;
		}

		// Don't add superlist to history if it's identical to the most recent superlist
		for (int i=0; i<superlist.size(); i++) {
			if (! superlist.get(i).equals(mostRecentSuperlist.get(i)) ) {
				stack.push(superlist);
				break;
			}
		}
	}

	/**
	 * Pushes the given tagmap to History.
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
	 * @return the superlist last added to History
	 */
	public ArrayList<ArrayList<Task>> peek() {
		return stack.peek();
	}

	/**
	 * Peeks at the last added tagmap in History.
	 * Not in use.
	 * @return the tagmap last added to History
	 */
	public HashMap<String, Integer> peekTags() {
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
	public ArrayList<ArrayList<Task>> pop() {
		return stack.pop();
	}

	/**
	 * Pop the last added tagmap from History.
	 * For undo (not in use).
	 * @return the removed tagmap
	 */
	public HashMap<String, Integer> popTags() {
		return tagStack.pop();
	}

}