#Taskey Quick Start Guide
---
### Pre-requisites:
 - Java SE Runtime Environment 8u74 (can be downloaded from Oracle.com) 


PS: Take note of stuff in bold as they are important keywords. 

###Shortcuts
<ul>
  <li>F1: Help Menu
  <li>F2: Dark Theme
  <li>F3: Light Theme 
  <li>Ctrl+W : Minimise/Re-Open Taskey
  <li>Tab: Scroll through the tabs 
  <li>Page Up: Scroll up the task list
  <li>Page Down: Scroll down the task list
  <li>Left Arrow Key: view previous page
  <li>Right Arrow Key: view next page
  <li>Up/Down Arrow Keys: get previous commands
</ul> 

###Reserved Characters

The following characters are reserved for special usage and should not be used in task names:
<ul>
  <li>! : Used for specifying task priorities (low:!, medium: !!, high: !!!)
  <li># : Used for specifying hashtags for a task 
  <li>^ : Get suggestions a list of dates you can use
</ul>

###Accepted date/time formats
<ul>
  <li>Time should have am/pm included (eg. 'meeting at 3pm', not 'meeting at 3') 
  <li>Dates should be keyed as <b>DD MMM YYYY</b> or <b>DD MMM</b>. 
  <li>Special allowable dates: morning, night, tomorrow, today, next xyz (eg. next wed), this xyz (eg. this fri)  
  <li>morning/night deadlines are 8am/8pm respectively (eg. 'do homework by tonight' will be processed as 'do homework by 8pm today')
</ul>

###Adding a task

Format: 

- Floating: add \<taskname\> 
- Deadline: add \<taskname\> on/by \<date/time\> 
- Event: add \<taskname\> from \<date/time\> to \<date/time\>
- Adding hashtags/ priorities: [any of the above 3] + "#hashtags !!!" (adding of priorities must come after adding of hashtags)

Examples: 
<ul> 
  <li><b>add</b> learn cooking
  <li><b>add</b> learn cooking #leisure
  <li><b>add</b> do homework <b>by</b> tomorrow !!! 
  <li><b>add</b> meet friends <b>on</b> 20 feb 2016 
  <li><b>add</b> project meeting <b>from</b> 4pm <b>to</b> 5pm on 19 feb 
  <li><b>add</b> project meeting <b>from</b> 19 feb 3pm <b>to</b> 4pm
</ul> 

###Deleting a task
A task can be deleted by its index number as shown in the UI.

Format: del \<id\>

Examples: 
<ul> 
  <li><b>del</b> 1
</ul> 

###Editing a task
A task's name, priority or its date details can be changed when required.

Format: set \<old id\> "new taskname"/[new date] 
<br> set \<old id\> \<new priority\>

Examples:
<ul> 
  <li><b>set</b> 1 [none] 
  <li><b>set</b> 1 [19 feb] 
  <li><b>set</b> 1 [19 feb 3pm, 19 feb 4pm] 
  <li><b>set</b> 1 "learn golf" [19 feb 5pm,19 feb 6pm]
  <li><b>set</b> 1 !!!
</ul> 

###Archiving a task
When you are done with a task, you can archive it. 

Format: done \<id\>

Examples:
<ul> 
  <li><b>done</b> 1
</ul> 

###Searching for a task
You can search for tasks via the command "**search**". 
To increase the chances of finding the task(s) that you are looking for, try to include *at least 3 characters* in your search and *be as specific as possible*.

Format: search \<keyword(s)\>

Example:
<ul> 
  <li><b>search</b> meetings
  <li><b>search</b> bio quiz
</ul> 

###Undoing an Action
You can undo the last action by typing "<b>undo</b>". 

###Tagging your Tasks
You can add tags to your tasks, so that you can view them by tags.
Typing the command <b>view</b>, followed by the tag-name allows you to view all tasks with that tag in one window. 

Examples: 
<ul> 
  <li><b>add</b> learn cooking #forfun
  <li><b>add</b> play pokemon #forfun
  <li><b>view</b> forfun 
</ul> 

###Viewing selected tasks
You can choose to view only certain types of task with the "view" command

Format: view \<category\> or view #tag1 #tag2 #tag3 ... <br>
[You can only view one basic category at a time, but you can view multiple user-defined categories at one go]

Examples:
<ul> 
  <li><b>view</b> deadlines 
  <li><b>view</b> #work #leisure 
</ul> 

###Saving
You can save any changes to your task list by typing the command <b>save</b>

###Clearing all tasks
You can clear all your tasks by typing the command <b>clear</b>. To save this change, type <b>save</b>. 
