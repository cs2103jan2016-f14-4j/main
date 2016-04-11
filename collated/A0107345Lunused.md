# A0107345Lunused
###### \taskey\src\taskey\messenger\UserTagDatabase.java
``` java
 * Purpose of this class is to handle the storage and retrieval
 * of userTags, so that they can be displayed by Logic as 
 * categories that users can view their tasks by.
 * Used by ParseView
 * @author Xue Hui
 *
 */
public class UserTagDatabase {
	//public static final int MAX_TAGS = 15; 
	ArrayList<TagCategory> userTags = new ArrayList<TagCategory>(); 
	Storage db; 
	
```
###### \taskey\src\taskey\messenger\UserTagDatabase.java
``` java
	 * Add a new tag to the userTagDatabase
	 * @param tag
	 */
	public void addTag(String tag) {
		TagCategory newTag = new TagCategory(tag); 
		
		if (!containsTagName(tag)) {
			userTags.add(newTag); 
		} else {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tagCat = userTags.get(i); 
				if (tagCat.compareTo(newTag) == 0) { 
					tagCat.increaseCount();
					break; 
				}
			}
		}
	}
	
	/**
	 * Remove a tag from the userTag Database.
	 * Called when tasks with all these tags are deleted 
	 * @param tag
	 * @return true if successfully removed
	 */
	public boolean removeTag(String tag) {
		TagCategory toRemove = new TagCategory(tag); 
		
		if (containsTagName(tag)) {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tagCat = userTags.get(i); 
				if (tagCat.compareTo(toRemove) == 0) { 
					tagCat.decreaseCount();
					//if 0 tasks with that tag, remove it from arraylist
					if (tagCat.isEmpty()) {
						userTags.remove(i);  
					}
					break; 
				}
			}
			return true; 
		}
		return false;
	}
	
```
###### \taskey\src\taskey\messenger\UserTagDatabase.java
``` java
	 * For Logic: Get the entire tagList so that 
	 * it can be displayed by the UI.
	 * @return
	 */
	public ArrayList<TagCategory> getTagList() {
		return cloneTagList(userTags);
	}
	
	/**
```
###### \taskey\src\taskey\messenger\UserTagDatabase.java
``` java
     * FOR DEBUGGING
     */
	@Override
	public String toString() {
		String stringRep = "";
		if (!userTags.isEmpty()) {
			for(int i = 0; i < userTags.size(); i++) {
				TagCategory tag = userTags.get(i);
				stringRep += "Tag Name: " + tag.getTagName() + ", ";
				stringRep += "TagCount: " + tag.getNumTags() + "\n"; 
			}
		}
		return stringRep; 
	}
	
	/*
	public static void main(String[] args) {
		UserTagDatabase db = new UserTagDatabase(); 
		db.addTag("hello");
		db.addTag("hello");
		db.addTag("monkey");
		db.addTag("hello");
		db.removeTag("hello");
		System.out.println(db);
		db.removeTag("monkey");
		System.out.println(db);
		//db.saveTagDatabase(); 
	} */ 
}
```
###### \taskey\src\taskey\parser\AutoComplete.java
``` java
	 * Decided not to use the code below as we decided to change 
	 * what the AutoComplete should display 
	 */
	
	/**
	 * Given a partial input that contains "del xxxx",
	 * display a list of tasks that the user can delete 
	 * (he can delete by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	/*
	private ArrayList<String> completeDelete(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("del", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	} */ 
	
	/**
	 * Given a partial input that contains "done xxxx",
	 * display a list of tasks that the user can set as done 
	 * (he can set done by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	/*
	private ArrayList<String> completeDone(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("done", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	} */ 
}
```
