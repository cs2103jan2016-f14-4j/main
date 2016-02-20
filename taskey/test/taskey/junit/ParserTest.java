package taskey.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import taskey.parser.Parser;

public class ParserTest {

	@Test
	public void test() {
		Parser parser = new Parser(); 
		
		assertEquals("add", parser.getCommand("add a string")); 
		assertEquals("do homework", parser.getTaskName("add", "add do homework"));
		assertEquals("add a song", parser.getTaskName("add", "add add a song"));
	}

}
