package taskey.logic;

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
	
	@Override
	/**
	 * Overriding this method for ArrayList's .contains() method 
	 */
    public boolean equals(Object object) {
        boolean isSame = false;

        if (object != null && object instanceof TagCategory) {
        	TagCategory other = (TagCategory) object;
            if (this.tagName.equals(other.getTagName())) {
            	return equals(this.numTags == other.numTags);
            }
        }
        return isSame;
    }

	@Override
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
