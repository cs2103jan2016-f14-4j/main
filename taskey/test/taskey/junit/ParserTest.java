package taskey.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import taskey.parser.Parser;

public class ParserTest {

	@Test
	public void testFloating() {
		Parser parser = new Parser(); 
		
		assertEquals("add", parser.getCommand("add a string")); 
		assertEquals("do homework", parser.getTaskName("add", "add do homework"));
		assertEquals("add a song", parser.getTaskName("add", "add add a song"));
	}
	
	public void testDeadline() {
		//add project meeting at 3pm on/by 7 feb 2016
		//add project meeting at 3pm on/by 7 feb
		//add complete essay by today 
		//other special days: tomorrow, next week, next ___eg. friday
		
	}
	
	public void testEvents() {
		//add meeting from tomorrow to 18 feb
		
	}
	
	public void testTag() {
		//eg. add lalala #lala
	}
	
	public void testChanges() {
		//set <task name>/<id> "new task name" 
		//set meeting [16 feb, 17 feb] 
	}
	
	public void testDelete() {
		//del test name/id
	}
	
	public void testSearch() {
		//search #tag name
		//search phrase
	}

	public void testDone() {
		//done <task name/id> 
	}
	
	//other tasks: undo 
	// view all
	// view general
	// view deadline
	// view events
}
