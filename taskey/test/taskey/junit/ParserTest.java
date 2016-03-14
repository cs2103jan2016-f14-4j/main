package taskey.junit;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import taskey.parser.Parser;

public class ParserTest {
	Parser parser = new Parser(); 
	PrettyTimeParser p = new PrettyTimeParser();
	
	@Test
	public void testFloating() {
		assertEquals("add", parser.getCommand("add a string")); 
		assertEquals("do homework", parser.getTaskName("add", "add do homework"));
		assertEquals("add a song", parser.getTaskName("add", "add add a song"));
		
		assertEquals("Command: ADD_FLOATING\ndo homework, FLOATING, \n",
				parser.parseInput("add do homework").toString());
		
		assertEquals("Command: ADD_FLOATING\nmeeting2, FLOATING, \n",
				parser.parseInput("ADD MEETING2").toString());
		
		//add 2345
	}
	
	@Test
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
		
		//add meeting222 on 17 feb 
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
	
	@Test
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
		//add mtg2234 from 19 feb 4pm to 5pm 
		//System.out.println(p.parse(" from 4pm to 5pm on 19 feb"));
	}
	
	public void testEventsHuman() {
		System.out.println(parser.parseInput("add meeting from today to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tomorrow to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tmr to 8 Mar"));
		System.out.println(parser.parseInput("add meeting from tmr to next wed"));
	}
	
	@Test
	public void testTag() {
		assertEquals("Command: ADD_FLOATING\nmeeting, FLOATING, \ntags: sua, serious, \n",
				parser.parseInput("add meeting #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Cannot be an empty add\n",
				parser.parseInput("add  ").toString());
		assertEquals("Command: ADD_DEADLINE\nmeeting, DEADLINE, due on 17 Feb 2016\n"
				+ "tags: sua, serious, \n",
				parser.parseInput("add meeting on 17 Feb #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Wrong date format\n",
				parser.parseInput("add meeting on 17 Fbr #sua #serious").toString());
		assertEquals("Command: ERROR\nerror type: Wrong date format\n",
				parser.parseInput("add meeting on 17 Fbr 2016 #sua #serious").toString());
		assertEquals("Command: ADD_EVENT\nmeeting, EVENT, from 17 Feb 2016 "
				+ "to 18 Feb 2016\ntags: sua, serious, \n",
				parser.parseInput("add meeting from 17 Feb to 18 Feb #sua #serious").toString());
		
	}
	
	@Test 
	public void testChanges() {
		//set <task name>/<id> "new task name" 
		//by task id
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_NAME\nat index: 0\n"
				+ "new TaskName: urgent meeting\n",
				parser.parseInput("set 1 \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nFLOATING, \nat index: 1\n",
				parser.parseInput("set 2 [none]").toString());
		assertEquals("Command: ERROR\nerror type: Wrong format for new task name/date\n",
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
		
		//by task name
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_NAME\nmeeting, \n"
				+ "new TaskName: urgent meeting\n",
				parser.parseInput("set meeting \"urgent meeting\"").toString());
		assertEquals("Command: UPDATE_BY_NAME_CHANGE_DATE\nmeeting, FLOATING, \n",
				parser.parseInput("set meeting [none]").toString());
		assertEquals("Command: ERROR\nerror type: Wrong format for new task name/date\n",
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
		
		//test time usage
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nDEADLINE, due on 16 "
				+ "Feb 2016 15:00\nat index: 1\n",
				parser.parseInput("set 2 [16 feb 3pm]").toString());
		
		assertEquals("Command: UPDATE_BY_INDEX_CHANGE_DATE\nEVENT, from 16 Feb 2016"
				+ " 15:00 to 19 Feb 2016 17:00\nat index: 1\n",
				parser.parseInput("set 2 [16 feb 3pm,19 feb 5pm]").toString());
		
		//test combination
		//TODO: change to assert 
		//System.out.println(parser.parseInput("set 2 [16 Feb] \"newName\""));
		//System.out.println(parser.parseInput("set meeting \"newName\" [19 feb]"));
	}
	
	public void testChangesHuman() {
		System.out.println(parser.parseInput("set 2 [tomorrow 5pm]"));
		System.out.println(parser.parseInput("set 2 [wed 5pm, thu 7pm]"));
	}
	
	@Test
	public void testDelete() {
		assertEquals("Command: DELETE_BY_INDEX\nat index: 4\n",
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
		assertEquals("Command: DONE_BY_INDEX\nat index: 4\n",
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
		assertEquals("Command: VIEW\nview type: ARCHIVE\n",
				parser.parseInput("view archive").toString());
	}
}
