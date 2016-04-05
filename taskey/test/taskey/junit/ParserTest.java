package taskey.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import taskey.parser.Parser;

/**
 * @@author A0107345L
 * This class tests all the functionality of Parser to ensure 
 * it is returning the correct thing 
 * @author Xue Hui
 *
 */
public class ParserTest {
	Parser parser = new Parser(); 
	PrettyTimeParser p = new PrettyTimeParser();
	
	@Test
	/**
	 * Test that adding floating tasks get parsed correctly 
	 */
	public void testFloating() {		
		assertEquals("Command: ADD_FLOATING\nDo homework, FLOATING, \n",
				parser.parseInput("add do homework").toString());
		
		assertEquals("Command: ADD_FLOATING\nMEETING2, FLOATING, \n",
				parser.parseInput("ADD MEETING2").toString());
	}
	
	@Test
	/**
	 * Test floating tasks that do not parse correctly 
	 */
	public void testFloatingError() {		
		//disallow task names with only numbers 
		assertEquals("Command: ERROR\nerror type: Error: Task name cannot consist "
				+ "entirely of numbers\n",
				parser.parseInput("add 2345").toString());
		//disallow empty adds 
		assertEquals("Command: ERROR\nerror type: Error: Cannot be an empty add\n",
				parser.parseInput("add ").toString());
		assertEquals("Command: ERROR\nerror type: Error: Cannot be an empty add\n",
				parser.parseInput("add !!").toString());
		//invalid priority 
		assertEquals("Command: ERROR\nerror type: Error: Invalid task priority entered\n",
				parser.parseInput("add mtg !!!!!").toString());
		//test that adding a place to the task doesn't get detected as time
		//because of the keywords "by" and "on" 
		assertEquals("Command: ADD_FLOATING\nDo work by the park, FLOATING, \n",
				parser.parseInput("add do work by the park").toString());
	}
	
	@Test
	/**
	 * Test that adding deadline tasks gets parsed correctly 
	 */
	public void testDeadline() {
		assertEquals("Command: ADD_DEADLINE\nProject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb 2016").toString());
			 
		assertEquals("Command: ADD_DEADLINE\nProject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting by 17 feb 2016 3pm").toString());
		 
		assertEquals("Command: ADD_DEADLINE\nProject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb").toString());
		
		assertEquals("Command: ADD_DEADLINE\nDo work, DEADLINE, due on 17 Feb 2016 15:00\n",
				parser.parseInput("add do work at 17 feb 3pm").toString());
	}
	
	@Test
	/**
	 * Test that adding deadline tasks don't give strange results for 
	 * error cases, so on... 
	 */
	public void testDeadlineError() {
		//ensure that words with "by" and "on" in it split correctly
		//ie. make sure that the correct keywords are detected. 
		assertEquals("Command: ADD_DEADLINE\nSing lullaby, DEADLINE, due on 11 Mar 2016\n",
				parser.parseInput("add sing lullaby on 11 mar").toString());
		assertEquals("Command: ADD_DEADLINE\nBuy baygon, DEADLINE, due on 11 Mar 2016\n",
				parser.parseInput("add buy baygon on 11 mar").toString());
		//ensure that numbers in meeting name do not affect date output
		assertEquals("Command: ADD_DEADLINE\nMeeting 222, DEADLINE, due on 17 "
				+ "Feb 2016\n",
				parser.parseInput("add meeting 222 on 17 Feb").toString());
	}
	
	@Test
	/**
	 * Check that grammatically incorrect dates get caught before 
	 * PrettyTime returns the wrong date 
	 */
	public void testDeadlineGrammar() {
		assertEquals("Command: ERROR\nerror type: Error: \"by 3pm on 17 Feb 2016\" is "
				+ "a grammatically incorrect date\n",
				parser.parseInput("add project meeting by 3pm on 17 Feb 2016").toString());
	}
	
	@Test
	/**
	 * Test that adding events get parsed correctly. 
	 */
	public void testEvents() {
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 19 Feb 2016 "
				+ "to 20 Feb 2016\n",
				parser.parseInput("add meeting from 19 Feb 2016 to 20 Feb 2016").toString());
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 19 Feb 2016 "
				+ "to 20 Feb 2016\n",
				parser.parseInput("add meeting from 19 Feb to 20 Feb").toString());	
		
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 19 Feb 2016 15:00 "
				+ "to 19 Feb 2016 16:00\n",
				parser.parseInput("add meeting from 19 feb 3pm to 19 feb 4pm").toString());
		
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 19 Feb 2016 15:00 "
				+ "to 19 Feb 2016 16:00\n",
				parser.parseInput("add meeting from 19 feb 3pm to 4pm").toString());
		
		assertEquals("Command: ADD_EVENT\nProject meeting, EVENT, from 19 Feb 2016 "
				+ "16:00 to 19 Feb 2016 17:00\n",
				parser.parseInput("add project meeting from 4pm to "
						+ "5pm on 19 feb").toString());
	}
	
	@Test 
	/**
	 * Test that events with numbers in their task names get parsed correctly 
	 */
	public void testEventsNumbers() {
		//test events with numbers in task name 
		assertEquals("Command: ADD_EVENT\nMtg2234, EVENT, from 19 Feb 2016 16:00"
				+ " to 19 Feb 2016 17:00\n",
				parser.parseInput("add mtg2234 from 19 feb 4pm to 5pm ").toString());
		//System.out.println(p.parse(" from 4pm to 5pm on 19 feb"));
	}
	
	@Test 
	/**
	 *  Test that for adding events: startTime >= endTime gives error 
	 */
	public void testEventsValidTime() {
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("add mtg from 19 feb 5pm to 3pm").toString());
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("add mtg from 21 feb to 20 feb").toString());
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("add mtg from tmr to 18 feb 3pm").toString());
	}
	
	@Test
	/**
	 * Test that tagging gets parsed correctly 
	 */
	public void testTag() {
		assertEquals("Command: ADD_FLOATING\nMeeting, FLOATING, \ntags: sua, serious, \n",
				parser.parseInput("add meeting #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: Cannot be an empty add\n",
				parser.parseInput("add  ").toString());
		assertEquals("Command: ADD_DEADLINE\nMeeting, DEADLINE, due on 17 Feb 2016\n"
				+ "tags: sua, serious, \n",
				parser.parseInput("add meeting on 17 Feb #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: \"17 Fbr\" is not an "
				+ "accepted date format\n",
				parser.parseInput("add meeting on 17 Fbr #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: \"17 Fbr 2016\" is not "
				+ "an accepted date format\n",
				parser.parseInput("add meeting on 17 Fbr 2016 #sua #serious").toString());
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 17 Feb 2016 "
				+ "to 18 Feb 2016\ntags: sua, serious, \n",
				parser.parseInput("add meeting from 17 Feb to 18 Feb #sua #serious").toString());
		
	}
	
	@Test
	/**
	 * Test invalid tags for adding 
	 */
	public void testTagInvalid() {
		//adding the same tag multiple times
		assertEquals("Command: ERROR\nerror type: Error: Invalid tags added to the task\n",
				parser.parseInput("add meeting #work #work #work").toString());
		//adding deadlines/general/all/events: invalid categories cos these are default categories
		assertEquals("Command: ERROR\nerror type: Error: Invalid tags added to the task\n",
				parser.parseInput("add meeting #work #general #deadline").toString());
	}
	
	@Test
	/**
	 * Test the adding of tasks with priority 
	 */
	public void testAddPriority() {
		//test setting of priority here 
		assertEquals("Command: ADD_FLOATING\nMeeting, FLOATING, \n",
				parser.parseInput("add meeting !").toString());
		assertEquals("Command: ADD_DEADLINE\nMeeting, DEADLINE, due on 18 Feb 2016\npriority: 3\n",
				parser.parseInput("add meeting on 18 feb !!!").toString());
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 18 Feb 2016 to 19 Feb 2016\n"
				+ "priority: 2\n",
				parser.parseInput("add meeting from 18 feb to 19 feb !!").toString());
		assertEquals("Command: ADD_EVENT\nMeeting, EVENT, from 18 Feb 2016 to 19 Feb 2016\n"
				+ "tags: boo, \npriority: 2\n",
				parser.parseInput("add meeting from 18 feb to 19 feb #boo !!").toString());
		assertEquals("Command: ADD_FLOATING\nMeeting, FLOATING, \ntags: sua, \npriority: 2\n",
				parser.parseInput("add meeting #sua !!").toString());
	}
	
	@Test
	/**
	 * Test adding of tasks with wrong priority 
	 */
	public void testAddWrongPriority() {
		assertEquals("Command: ERROR\nerror type: Error: Invalid task priority entered\n",
				parser.parseInput("add test !!!!!").toString());
	}
	
	@Test 
	/**
	 * Test setting of changes via using task index
	 */
	public void testChangesById() {
		//set <task name>/<id> "new task name" 
		//by task id
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_NAME\nat index: 0\n"
				+ "new TaskName: urgent meeting\n",
				parser.parseInput("set 1 \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nFLOATING, \nat index: 1\n",
				parser.parseInput("set 2 [none]").toString());
		assertEquals("Command: ERROR\nerror type: Error: Wrong format for new task name/date\n",
				parser.parseInput("set 2 []").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nDEADLINE, "
				+ "due on 17 Feb 2016\nat index: 1\n",
				parser.parseInput("set 2 [17 feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016 "
				+ "to 17 Feb 2016\nat index: 1\n",
				parser.parseInput("set 2 [16 feb, 17 Feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016 "
				+ "to 17 Feb 2016\nat index: 1\n",
				parser.parseInput("set 2 [16 feb,17 Feb]").toString());
	}
	
	@Test 
	/**
	 * Test setting of changes via using task name 
	 */
	public void testChangesByName() {
		//by task name
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_NAME\nmeeting, \n"
				+ "new TaskName: urgent meeting\n",
				parser.parseInput("set meeting \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, FLOATING, \n",
				parser.parseInput("set meeting [none]").toString());
		assertEquals("Command: ERROR\nerror type: Error: Wrong format for new task name/date\n",
				parser.parseInput("set meeting []").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, DEADLINE, "
				+ "due on 17 Feb 2016\n",
				parser.parseInput("set meeting [17 feb]").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, EVENT, "
				+ "from 16 Feb 2016 to 17 Feb 2016\n",
				parser.parseInput("set meeting [16 feb, 17 Feb]").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, EVENT, "
				+ "from 16 Feb 2016 to 17 Feb 2016\n",
				parser.parseInput("set meeting [16 feb,17 Feb]").toString());	
	}
	
	@Test
	/**
	 * Test the setting of new time 
	 */
	public void testChangesTimeUsage() { 
		//test time usage
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nDEADLINE, due on 16 "
				+ "Feb 2016 15:00\nat index: 1\n",
				parser.parseInput("set 2 [16 feb 3pm]").toString());
		
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016"
				+ " 15:00 to 19 Feb 2016 17:00\nat index: 1\n",
				parser.parseInput("set 2 [16 feb 3pm,19 feb 5pm]").toString());
	}
	
	@Test 
	/**
	 * Test the parsing of setting both name and task name in 1 command
	 */
	public void testChangesBothNameDate() { 
		//test combination
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_BOTH\nDEADLINE, due on 16 "
				+ "Feb 2016\nat index: 1\nnew TaskName: newName\n",
				parser.parseInput("set 2 [16 Feb] \"newName\"").toString());
		
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_BOTH\nFLOATING, \nat index: 0\n"
				+ "new TaskName: newName\n",
				parser.parseInput("set 1 \"newName\" [none]").toString());
		
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_BOTH\nmeeting, DEADLINE, "
				+ "due on 19 Feb 2016\nnew TaskName: newName\n",
				parser.parseInput("set meeting \"newName\" [19 feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_BOTH\nEVENT, from 14 Mar "
				+ "2016 to 15 Mar 2016\nat index: 0\nnew TaskName: task 2\n",
				parser.parseInput("set 1 [14 mar,15 mar] \"task 2\"").toString());
	}
	
	@Test
	/**
	 * Test the changing of task priorities 
	 */
	public void testChangesTaskPriority() {
		//test boundary case 
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_PRIORITY\nat index: 0\n"
				+ "newPriority: 3\n",
				parser.parseInput("set 1 !!!").toString()); 
		//test out of bound case 
		assertEquals("Command: ERROR\nerror type: Error: Invalid task priority entered\n",
				parser.parseInput("set 1 !!!!").toString());
		//test working case 
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_PRIORITY\ndo homework, \n"
				+ "newPriority: 2\n",
				parser.parseInput("set do homework !!").toString());
		//test boundary case 
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_PRIORITY\nat index: 1\n"
				+ "newPriority: 1\n",
				parser.parseInput("set 2 !").toString()); 
		//test no task name given 
		assertEquals("Command: ERROR\nerror type: Error: Task name/index is not given\n",
				parser.parseInput("set !").toString()); 
		//test no priority given
		assertEquals("Command: ERROR\nerror type: Error: Wrong format for new "
				+ "task name/date\n",
				parser.parseInput("set 2").toString()); 
	}
	
	@Test
	/**
	 * Test: Changing an task to an invalid event date (ie. startTime >= endTime) 
	 */
	public void testChangesInvalidEvent() {
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("set 1 [19 feb 5pm, 19 feb 3pm]").toString());
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("set 1 [21 feb, 20 feb]").toString());
		assertEquals("Command: ERROR\nerror type: Error: Event starting time cannot be"
				+ " later than the ending time\n",
				parser.parseInput("set 1 [tmr, 19 feb 3pm]").toString());
	}
	
	@Test 
	/**
	 * Test that trying to do empty changes returns error 
	 */
	public void testChangesEmptyError() {
		assertEquals("Command: ERROR\nerror type: Error: Cannot be an empty change\n",
				parser.parseInput("set ").toString());
	}
	
	@Test
	/**
	 * Tests that the delete by index and by task name parses correctly
	 * Also tests that it parses a category to be deleted correctly. 
	 */
	public void testDelete() {
		assertEquals("Command: DELETE_BY_INDEX\nat index: 4\n",
				parser.parseInput("del 5").toString());
		assertEquals("Command: DELETE_BY_NAME\nhello world, \n",
				parser.parseInput("del hello world").toString());
		assertEquals("Command: DELETE_BY_CATEGORY\ncategory: hello\n",
				parser.parseInput("del #hello").toString());
	}
	
	@Test
	/**
	 * Test that the search feature parses correctly 
	 */
	public void testSearch() {
		assertEquals("Command: SEARCH\nsearch phrase: hello world\n",
				parser.parseInput("search hello world").toString());
	}
	
	@Test
	/**
	 * Test that done parses correctly by both index and task name 
	 */
	public void testDone() {
		assertEquals("Command: DONE_BY_INDEX\nat index: 4\n",
				parser.parseInput("done 5").toString());
		assertEquals("Command: DONE_BY_NAME\nhello world, \n",
				parser.parseInput("done hello world").toString());
			
	}
	
	@Test
	/**
	 * Test that the undo feature parses correctly
	 */
	public void testUndo() {
		assertEquals("Command: UNDO\n",parser.parseInput("undo").toString());	
	}
	
	
	@Test 
	/**
	 * Test that the basic view types are all working correctly 
	 */
	public void testView() {
		assertEquals("Command: VIEW_BASIC\nview type: all, \n",
				parser.parseInput("view all").toString());
		assertEquals("Command: VIEW_BASIC\nview type: general, \n",
				parser.parseInput("View general").toString());
		assertEquals("Command: VIEW_BASIC\nview type: deadlines, \n",
				parser.parseInput("vieW deadlines").toString());
		assertEquals("Command: VIEW_BASIC\nview type: events, \n",
				parser.parseInput("view Events").toString());
		assertEquals("Command: VIEW_BASIC\nview type: archive, \n",
				parser.parseInput("view archive").toString());
		
	}
	
	@Test 
	/**
	 * Test the viewing of tasks by tag categories 
	 */
	public void testViewTags() {
		assertEquals("Command: VIEW_TAGS\nview type: work, \n",
				parser.parseInput("view #work").toString());
		assertEquals("Command: VIEW_TAGS\nview type: work, homework, yolo, \n",
				parser.parseInput("view #work #homework #yolo").toString());
	}
	
	@Test
	/**
	 * Test the error parsing of viewing tasks by tag categories
	 */
	public void testViewTagsError() {
		assertEquals("Command: ERROR\nerror type: Error: \"lala\" is not a valid tag\n",
				parser.parseInput("view lala #yolo").toString());
		assertEquals("Command: ERROR\nerror type: Error: \"lala\" is not a valid tag\n",
				parser.parseInput("view #yolo lala #boo").toString());
	}
	
	@Test
	/**
	 * Test the parsing of the change Directory Command
	 */
	public void testChangeDir() {
		assertEquals("Command: CHANGE_FILE_LOC\nnewLocation: C:/Desktop\n",
				parser.parseInput("setdir C:/Desktop").toString());
	}
	
	@Test
	/**
	 * Test the parsing of the clear command
	 */
	public void testClear() {
		assertEquals("Command: CLEAR\n",
				parser.parseInput("clear").toString());
	}
	
	@Test
	/**
	 * Test the parsing of the save command 
	 */
	public void testSave() {
		assertEquals("Command: SAVE\n",
				parser.parseInput("save").toString());
	}
	
	@Test
	/**
	 * Test parsing of invalid command
	 */
	public void testNoSuchCommand() {
		assertEquals("Command: ERROR\nerror type: Error: \"write\" is not a valid command\n",
				parser.parseInput("write oo").toString());
	}
	
	
	/* 
	public void testDate() {
		PrettyTimeParser p  = new PrettyTimeParser(); 
		System.out.println(p.parse("today 3pm"));
		System.out.println(p.parse("tomorrow 3pm"));
		System.out.println(parser.parseInput("set 1 [today 3pm, tmr 3pm]"));
	} */ 
	
	
	/*
	 * All the methods below will be manually tested because 
	 * dates like "tomorrow" and "today" are relative, and the time/date will
	 * change every time the the unit test is run 
	 */
	
	@Test
	/**
	 * Tests that the methods return the correct dates for human defined events 
	 */
	public void testEventsHuman() {
		System.out.println(parser.parseInput("add meeting from today to 8 Apr"));
		System.out.println(parser.parseInput("add meeting from tomorrow to 8 Apr"));
		System.out.println(parser.parseInput("add meeting from tmr to 8 Apr"));
		System.out.println(parser.parseInput("add meeting from tmr to next wed"));
	}
	
	@Test
	/**
	 * Test human date edits
	 */
	public void testChangesHuman() {
		System.out.println(parser.parseInput("set 2 [tomorrow 5pm]"));
		System.out.println(parser.parseInput("set 2 [wed 5pm, thu 7pm]"));
	}
	
	@Test 
	/**
	 * Test human deadlines
	 */
	public void testDeadlineHuman() { 
		System.out.println(parser.parseInput("add do homework by tonight")); 
		System.out.println(parser.parseInput("add complete essay by today")); 
		System.out.println(parser.parseInput("add complete essay by tmr")); 
		System.out.println(parser.parseInput("add complete essay by this Wed"));
		System.out.println(parser.parseInput("add complete essay by next Wed"));
		System.out.println(parser.parseInput("add sdasda by mon")); 
		System.out.println(parser.parseInput("add asdsad on sat")); 
		System.out.println(parser.parseInput("add sdasad by sat")); 
	}
}
