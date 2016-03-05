package taskey.junit;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;
import taskey.parser.TimeConverter;

public class TimeConverterTest {

	@Test
	public void test() {
		TimeConverter timeConverter = new TimeConverter(); 
		
		try {
			//System.out.println("Current Time in Epoch: " + timeConverter.getCurrTime()); 
			assertEquals(1455123600,timeConverter.toEpochTime("11 Feb 2016 01:00:00"));
			
			assertFalse(timeConverter.isSameDay(timeConverter.getCurrTime(),
					timeConverter.toEpochTime("11 Feb 2016 23:00:00")));
			
			assertEquals(1455206399,timeConverter.toEpochTime("11 Feb 2016"));
			assertEquals(1455206399,timeConverter.toEpochTime("11 Feb"));
			assertEquals(1454342399,timeConverter.toEpochTime("1 Feb 2016"));
			assertEquals(1454342399,timeConverter.toEpochTime("1 Feb"));
			assertEquals("01 Feb 2016 23:59:59",timeConverter.toHumanTime(1454342399));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//compare 10Feb 11pm vs 11Feb 1am 
		assertFalse(timeConverter.isSameDay(1455123600,1455116400));
		//compare 11 feb a few minutes later 
		assertTrue(timeConverter.isSameDay(1455123600,1455123800));
		//compare 10Feb 11pm and 11Feb 1am 
		assertTrue(timeConverter.isSameWeek(1455123600,1455116400));
		
		assertEquals("11 Feb 2016 01:00:00",timeConverter.toHumanTime(1455123600)); 

	}

}
