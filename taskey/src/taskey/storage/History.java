package taskey.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.TagCategory;
import taskey.logic.Task;

/**
 * @@author A0121618M
 * This class is to allow Storage to easily retrieve the last successfully saved tasklist/tagmap from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to undo the last operation so that its data remains in sync with Storage.
 *
 * For now, this class is only meant to be used by Storage for the above purpose.
 * But, in the future, if more methods are added to this class, Logic could also use this for the undo command.
 * TODO: multiple undos/redos using stacks?
 */
public class History {
	private ArrayDeque<ArrayList<ArrayList<Task>>> stack;
	private ArrayDeque<ArrayList<TagCategory>> tagStack; //for exception handling when saving tags

	public History() {
		stack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		tagStack = new ArrayDeque<ArrayList<TagCategory>>();
	}

	public boolean listStackIsEmpty() {
		return stack.isEmpty();
	}
	
	public boolean tagStackIsEmpty() {
		return tagStack.isEmpty();
	}

	/*========*
	 * Adders *
	 *========*/
	/**
	 * @@author A0134177E
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
		
		stack.push(superlist);
		/*
		// Don't add superlist to history if it's identical to the most recent superlist
		for (int i=0; i<superlist.size(); i++) {
			if (! superlist.get(i).equals(mostRecentSuperlist.get(i)) ) {
				stack.push(superlist);
				break;
			}
		}*/
	}
	
	/**
	 * Pushes the given tag list to History.
	 * Has a check that prevents tagList from being added, if it is identical to the tag list at
	 * the top of the stack
	 * @param taglist to be added
	 */
	public void addTagList(ArrayList<TagCategory> tagList) {
		ArrayList<TagCategory> mostRecentTagList = tagStack.peek();
		if (mostRecentTagList == null) {
			tagStack.push(tagList);
			return;
		}
		
		tagStack.push(tagList);
		/*
		// Don't add tag list to history if it's identical to the most recent tag list
		if (!tagList.equals(mostRecentTagList) ) {
			tagStack.push(tagList);
		}*/
	}
	
	public void addTags(HashMap<String, Integer> tags){
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
	 * @@author A0121618M
	 * Peeks at the last added tagmap in History.
	 * Not in use.
	 * @return the tagmap last added to History
	 */
	public ArrayList<TagCategory> peekTags() {
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
	public ArrayList<TagCategory> popTags() {
		return tagStack.pop();
	}

}