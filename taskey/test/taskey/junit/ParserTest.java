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
		assertEquals("Command: ADD_FLOATING\ndo homework, FLOATING, \n",
				parser.parseInput("add do homework").toString());
		
		assertEquals("Command: ADD_FLOATING\nmeeting2, FLOATING, \n",
				parser.parseInput("ADD MEETING2").toString());
		
		//disallow task names with only numbers 
		assertEquals("Command: ERROR\nerror type: Error: Task name cannot consist "
				+ "entirely of numbers\n",
				parser.parseInput("add 2345").toString());
	}
	
	@Test
	/**
	 * Tst that adding deadline tasks gets parsed correctly 
	 */
	public void testDeadline() {
		assertEquals("Command: ADD_DEADLINE\nproject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb 2016").toString());
			 
		assertEquals("Command: ADD_DEADLINE\nproject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting by 17 feb 2016 3pm").toString());
		 
		assertEquals("Command: ADD_DEADLINE\nproject meeting, DEADLINE, "
				+ "due on 17 Feb 2016 15:00\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb").toString());
		
		//ensure that numbers in meeting name do not affect date output
		assertEquals("Command: ADD_DEADLINE\nmeeting 222, DEADLINE, due on 17 "
				+ "Feb 2016\n",
				parser.parseInput("add meeting 222 on 17 Feb").toString());
	}
	
	@Test
	/**
	 * Test that adding events get parsed correctly. 
	 */
	public void testEvents() {
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 "
				+ "to 20 Feb 2016\n",
				parser.parseInput("add meeting from 19 Feb 2016 to 20 Feb 2016").toString());
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 "
				+ "to 20 Feb 2016\n",
				parser.parseInput("add meeting from 19 Feb to 20 Feb").toString());	
		
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 15:00 "
				+ "to 19 Feb 2016 16:00\n",
				parser.parseInput("add meeting from 19 feb 3pm to 19 feb 4pm").toString());
		
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 15:00 "
				+ "to 19 Feb 2016 16:00\n",
				parser.parseInput("add meeting from 19 feb 3pm to 4pm").toString());
		
		assertEquals("Command: ADD_EVENT\nproject meeting, EVENT, from 19 Feb 2016 "
				+ "16:00 to 19 Feb 2016 17:00\n",
				parser.parseInput("add project meeting from 4pm to "
						+ "5pm on 19 feb").toString());
		
		//test events with numbers in task name 
		assertEquals("Command: ADD_EVENT\nmtg2234, EVENT, from 19 Feb 2016 16:00"
				+ " to 19 Feb 2016 17:00\n",
				parser.parseInput("add mtg2234 from 19 feb 4pm to 5pm ").toString());
		//System.out.println(p.parse(" from 4pm to 5pm on 19 feb"));
	}
	
	@Test
	/**
	 * Test that tagging gets parsed correctly 
	 */
	public void testTag() {
		assertEquals("Command: ADD_FLOATING\nmeeting, FLOATING, \ntags: sua, serious, \n",
				parser.parseInput("add meeting #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: Cannot be an empty add\n",
				parser.parseInput("add  ").toString());
		assertEquals("Command: ADD_DEADLINE\nmeeting, DEADLINE, due on 17 Feb 2016\n"
				+ "tags: sua, serious, \n",
				parser.parseInput("add meeting on 17 Feb #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: \"17 fbr\" is not an "
				+ "accepted date format\n",
				parser.parseInput("add meeting on 17 Fbr #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Error: \"17 fbr 2016\" is not "
				+ "an accepted date format\n",
				parser.parseInput("add meeting on 17 Fbr 2016 #sua #serious").toString());
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 17 Feb 2016 "
				+ "to 18 Feb 2016\ntags: sua, serious, \n",
				parser.parseInput("add meeting from 17 Feb to 18 Feb #sua #serious").toString());
		
	}
	
	public void testPriority() {
		//test setting of priority here 
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
				+ "Feb 2016\nat index: 1\nnew TaskName: newname\n",
				parser.parseInput("set 2 [16 Feb] \"newName\"").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_BOTH\nmeeting, DEADLINE, "
				+ "due on 19 Feb 2016\nnew TaskName: newname\n",
				parser.parseInput("set meeting \"newName\" [19 feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_BOTH\nEVENT, from 14 Mar "
				+ "2016 to 15 Mar 2016\nat index: 0\nnew TaskName: task 2\n",
				parser.parseInput("set 1 [14 mar,15 mar] \"task 2\"").toString());
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
		//assertEquals("Command: SEARCH\nsearch phrase: #mycategory\n",
		//		parser.parseInput("search #mycategory").toString());
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
		assertEquals("Command: VIEW_BASIC\nview type: ALL, \n",
				parser.parseInput("view all").toString());
		assertEquals("Command: VIEW_BASIC\nview type: GENERAL, \n",
				parser.parseInput("View general").toString());
		assertEquals("Command: VIEW_BASIC\nview type: DEADLINES, \n",
				parser.parseInput("vieW deadlines").toString());
		assertEquals("Command: VIEW_BASIC\nview type: EVENTS, \n",
				parser.parseInput("view Events").toString());
		assertEquals("Command: VIEW_BASIC\nview type: ARCHIVE, \n",
				parser.parseInput("view archive").toString());
		//test view by tag task 
	}
	
	
	/*
	 * All the methods below will be manually tested because 
	 * dates like "tomorrow" and "today" are relative, and the time/date will
	 * change every time the the unit test is run 
	 */
	
	/**
	 * Tests that the methods return the correct dates for human defined events 
	 */
	public void testEventsHuman() {
		System.out.println(parser.parseInput("add meeting from today to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tomorrow to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tmr to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tmr to next wed"));
	}
	
	public void testChangesHuman() {
		System.out.println(parser.parseInput("set 2 [tomorrow 5pm]"));
		System.out.println(parser.parseInput("set 2 [wed 5pm, thu 7pm]"));
	}
	
	public void testDeadlineHuman() {
		//tests the SpecialDateConverter, which is based on relative dates.
		//thus, cannot to assert this 
		System.out.println(parser.parseInput("add sing lullaby on 11 mar"));
		System.out.println(parser.parseInput("add do homework by tonight")); 
		System.out.println(parser.parseInput("add complete essay by today")); 
		System.out.println(parser.parseInput("add complete essay by tmr")); 
		System.out.println(parser.parseInput("add complete essay by this Wed"));
		System.out.println(parser.parseInput("add complete essay by next Wed"));
	}
}
