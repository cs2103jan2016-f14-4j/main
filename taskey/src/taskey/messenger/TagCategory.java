package taskey.messenger;

import taskey.messenger.TagCategory;

/**
 * @@author A0107345L 
 * This Tag Category encapsulates the tag name and the
 * number of times this tag occurs in the user's tasks. 
 * @author Xue Hui
 *
 */
public class TagCategory implements Comparable<TagCategory> {
	private String tagName = null;
	private int numTags = 0;
	
	public TagCategory(String tagName) {
		this.tagName = tagName; 
		numTags += 1; 
	}
	
	public TagCategory(TagCategory other) {
		tagName = other.tagName;
		numTags = other.numTags;
	}
	
	/**
	 * @return Tag name of the TagCategory
	 */
	public String getTagName() {
		return tagName;
	}
	
	/**
	 * Set the tag name of the tag category
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	/**
	 * Get the number of tasks with this tag 
	 * @return
	 */
	public int getNumTags() {
		return numTags;
	}
	
	/**
	 * Set the number of tasks with this tag 
	 * @param numTags
	 */
	public void setNumTags(int numTags) {
		this.numTags = numTags;
	}
	
	/**
	 * Increase the count of numTags by 1
	 * ie. there's a new task with that tag
	 */
	public void increaseCount() {
		numTags += 1; 
	}
	
	/**
	 * Decrease the count of numTags by 1
	 * ie. a task with this tag has been deleted.
	 */
	public void decreaseCount() {
		if (numTags > 0) {
			numTags -= 1; 
		}
	}
	
	/**
	 * @return true if numTag count is 0. 
	 */
	public boolean isEmpty() {
		if (numTags == 0) {
			return true;
		}
		return false; 
	}
	
	// @@author A0134177E
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numTags;
		result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		TagCategory other = (TagCategory) obj;
		
		if (numTags != other.numTags) {
			return false;
		}
		
		if (tagName == null) {
			if (other.tagName != null) {
				return false;
			}
		} else if (!tagName.equals(other.tagName)) {
			return false;
		}
		
		return true;
	}

	@Override
	//@@author A0107345L
	public int compareTo(TagCategory tag) {
		String otherTagName = tag.getTagName(); 
        if (this.tagName.compareTo(otherTagName) == 0) {
        	return 0; 
        } else if (this.tagName.compareTo(otherTagName) > 0) {
        	return 1;
        } 
		return -1;
	}
}
