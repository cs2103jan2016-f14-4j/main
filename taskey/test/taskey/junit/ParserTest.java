package taskey.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import taskey.parser.Parser;

public class ParserTest {
	Parser parser = new Parser(); 
	
	@Test
	public void testFloating() {
		assertEquals("add", parser.getCommand("add a string")); 
		assertEquals("do homework", parser.getTaskName("add", "add do homework"));
		assertEquals("add a song", parser.getTaskName("add", "add add a song"));
		
		assertEquals("Command: ADD_FLOATING\ndo homework, FLOATING, \n",
				parser.parseInput("add do homework").toString());
	}
	
	@Test
	public void testDeadline() {
		assertEquals("Command: ADD_DEADLINE\nproject meeting at 3pm, DEADLINE, "
				+ "due on 17 Feb 2016 23:59:59\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb 2016").toString());
		assertEquals("Command: ADD_DEADLINE\nproject meeting at 3pm, DEADLINE, "
				+ "due on 17 Feb 2016 23:59:59\n",
				parser.parseInput("add project meeting at 3pm by 17 feb 2016").toString());
		
		assertEquals("Command: ADD_DEADLINE\nproject meeting at 3pm, DEADLINE, "
				+ "due on 17 Feb 2016 23:59:59\n",
				parser.parseInput("add project meeting at 3pm on 17 Feb").toString());
		//add complete essay by today 
		//other special days: tomorrow, next week, next ___eg. friday
		
	}
	
	@Test
	public void testEvents() {
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 23:59:59 "
				+ "to 20 Feb 2016 23:59:59\n",
				parser.parseInput("add meeting from 19 Feb 2016 to 20 Feb 2016").toString());
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 19 Feb 2016 23:59:59 "
				+ "to 20 Feb 2016 23:59:59\n",
				parser.parseInput("add meeting from 19 Feb to 20 Feb").toString());
		//add meeting from tomorrow to 18 feb
		
	}
	
	public void testTag() {
		//eg. add lalala #lala
	}
	
	@Test
	public void testChanges() {
		//set <task name>/<id> "new task name" 
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_NAME\nat index: 1",
				parser.parseInput("set 1 \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nFLOATING, \nat index: 2",
				parser.parseInput("set 2 [none]").toString());
		assertEquals("Command: ERROR\nerror type: invalid input\n",
				parser.parseInput("set 2 []").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nDEADLINE, "
				+ "due on 17 Feb 2016 23:59:59\nat index: 2",
				parser.parseInput("set 2 [17 feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016 "
				+ "23:59:59 to 17 Feb 2016 23:59:59\nat index: 2",
				parser.parseInput("set 2 [16 feb, 17 Feb]").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016 "
				+ "23:59:59 to 17 Feb 2016 23:59:59\nat index: 2",
				parser.parseInput("set 2 [16 feb,17 Feb]").toString());
		
		
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_NAME\nmeeting, \n",
				parser.parseInput("set meeting \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, FLOATING, \n",
				parser.parseInput("set meeting [none]").toString());
		assertEquals("Command: ERROR\nerror type: invalid input\n",
				parser.parseInput("set meeting []").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, DEADLINE, "
				+ "due on 17 Feb 2016 23:59:59\n",
				parser.parseInput("set meeting [17 feb]").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, EVENT, "
				+ "from 16 Feb 2016 23:59:59 to 17 Feb 2016 23:59:59\n",
				parser.parseInput("set meeting [16 feb, 17 Feb]").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, EVENT, "
				+ "from 16 Feb 2016 23:59:59 to 17 Feb 2016 23:59:59\n",
				parser.parseInput("set meeting [16 feb,17 Feb]").toString());		
	}
	
	@Test
	public void testDelete() {
		assertEquals("Command: DELETE_BY_INDEX\nat index: 4",
				parser.parseInput("del 5").toString());
		assertEquals("Command: DELETE_BY_NAME\nhello world, \n",
				parser.parseInput("del hello world").toString());
		
	}
	
	@Test
	public void testSearch() {
		assertEquals("Command: SEARCH\nsearch phrase: hello world\n",
				parser.parseInput("search hello world").toString());
		assertEquals("Command: SEARCH\nsearch phrase: #mycategory\n",
				parser.parseInput("search #mycategory").toString());
	}
	
	@Test
	public void testDone() {
		assertEquals("Command: DONE_BY_INDEX\nat index: 5",
				parser.parseInput("done 5").toString());
		assertEquals("Command: DONE_BY_NAME\nhello world, \n",
				parser.parseInput("done hello world").toString());
			
	}
	
	@Test
	public void testUndo() {
		assertEquals("Command: UNDO\n",parser.parseInput("undo").toString());	
	}
	
	@Test 
	public void testView() {
		assertEquals("Command: VIEW\nview type: ALL\n",
				parser.parseInput("view all").toString());
		assertEquals("Command: VIEW\nview type: GENERAL\n",
				parser.parseInput("View general").toString());
		assertEquals("Command: VIEW\nview type: DEADLINES\n",
				parser.parseInput("vieW deadlines").toString());
		assertEquals("Command: VIEW\nview type: EVENTS\n",
				parser.parseInput("view Events").toString());
		
		String myString = "add test lala #lala #lalala";
		String[] splitString = myString.split("#");
		for (int i=0; i < splitString.length; i++) {
			System.out.println("["+splitString[i] + "]");
		}
		
	}
}
