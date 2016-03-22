package taskey.junit;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;
import taskey.parser.TimeConverter;

/* @@author A0107345L */
public class TimeConverterTest {

	@Test
	public void test() {
		TimeConverter timeConverter = new TimeConverter(); 
		
		try {
			//System.out.println(timeConverter.toHumanTime(1455692400));
			//System.out.println("Current Time in Epoch: " + timeConverter.getCurrTime()); 
			assertEquals(1455123600,timeConverter.toEpochTime("11 Feb 2016 01:00:00"));
			
			assertFalse(timeConverter.isSameDay(timeConverter.getCurrTime(),
					timeConverter.toEpochTime("11 Feb 2016 23:00:00")));
			
			assertEquals(1455206399,timeConverter.toEpochTime("11 Feb 2016"));
			assertEquals(1455206399,timeConverter.toEpochTime("11 Feb"));
			assertEquals(1454342399,timeConverter.toEpochTime("1 Feb 2016"));
			assertEquals(1454342399,timeConverter.toEpochTime("1 Feb"));
			assertEquals("01 Feb 2016 23:59",timeConverter.toHumanTime(1454342399));
			
			long time1 = timeConverter.toEpochTime("31 Mar");
			long time2 = timeConverter.toEpochTime("1 Apr");
			assertTrue(timeConverter.isSameWeek(time1, time2));
			
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
		
		assertEquals("11 Feb 2016 01:00",timeConverter.toHumanTime(1455123600)); 

	}
	
	@Test
	public void testSameWeek() {
		TimeConverter tc = new TimeConverter();
		try {
			long epochTime1 = tc.toEpochTime("27 Mar");
			long epochTime2 = tc.toEpochTime("28 Mar"); 
			//sun and mon are diff days of the week
			assertFalse(tc.isSameWeek(epochTime1, epochTime2));
			epochTime1 = tc.toEpochTime("29 Mar");
			//mon and tues are same days of the week 
			assertTrue(tc.isSameWeek(epochTime1, epochTime2));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
