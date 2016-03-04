package taskey.parser;

import java.util.Calendar;
import java.util.HashMap;

import taskey.constants.ParserConstants;

public class SpecialDaysConverter {
	private HashMap<String,Long> specialDays = new HashMap<String,Long>();
	private TimeConverter timeConverter = new TimeConverter(); 
	
	public SpecialDaysConverter() { 
		specialDays.put("tomorrow", 
					timeConverter.getCurrTime() + ParserConstants.ONE_DAY); 
		specialDays.put("today", timeConverter.getCurrTime()); 
		
	}
	
	public HashMap<String,Long> getSpecialDays() {
		return specialDays; 
	}
	
	public void processDayOfTheWeek() {
		long currTime = timeConverter.getCurrTime(); 
		Calendar cal = Calendar.getInstance();
		cal.set(timeConverter.getYear(currTime),
				timeConverter.getMonth(currTime)-1,
				timeConverter.getDay(currTime));
		int day = cal.get(cal.DAY_OF_WEEK); 
		System.out.println("Day is "+ day);
		
		switch (day) {
			case 1: //sun
				specialDays.put("this mon", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this tue", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this wed", 
						currTime  + ParserConstants.THREE_DAYS); 
				specialDays.put("this thu", 
						currTime  + ParserConstants.FOUR_DAYS); 
				specialDays.put("this fri", 
						currTime + ParserConstants.FIVE_DAYS); 
				specialDays.put("this sat",
						currTime + ParserConstants.SIX_DAYS); 
				
				specialDays.put("next sun", currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY ); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				break; 
			case 2: //mon
				specialDays.put("this sun", 
						currTime + ParserConstants.SIX_DAYS); 
				specialDays.put("this tue", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this wed", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this thu", 
						currTime + ParserConstants.THREE_DAYS); 
				specialDays.put("this fri", 
						currTime + ParserConstants.FOUR_DAYS); 
				specialDays.put("this sat",
						currTime + ParserConstants.FIVE_DAYS); 
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				break; 
			case 3: //tue
				specialDays.put("this sun", 
						currTime + ParserConstants.FIVE_DAYS); 
				specialDays.put("this mon", 
						currTime + ParserConstants.SIX_DAYS); 
				specialDays.put("this wed", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this thu", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this fri", 
						currTime + ParserConstants.THREE_DAYS); 
				specialDays.put("this sat",
						currTime + ParserConstants.FOUR_DAYS);
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS);
				break; 
			case 4: //wed
				specialDays.put("this sun", 
						currTime + ParserConstants.FOUR_DAYS); 
				specialDays.put("this mon", 
						currTime + ParserConstants.FIVE_DAYS); 
				specialDays.put("this tue", 
						currTime + ParserConstants.SIX_DAYS); 
				specialDays.put("this thu", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this fri", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this sat",
						currTime + ParserConstants.THREE_DAYS);
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS);
				break; 
			case 5: //thu
				specialDays.put("this sun", 
						currTime + ParserConstants.THREE_DAYS); 
				specialDays.put("this mon", 
						currTime + ParserConstants.FOUR_DAYS); 
				specialDays.put("this tue", 
						currTime +  ParserConstants.FIVE_DAYS); 
				specialDays.put("this wed", 
						currTime + ParserConstants.SIX_DAYS); 
				specialDays.put("this fri", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this sat",
						currTime + ParserConstants.TWO_DAYS);
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS);
				break; 
			case 6: //fri
				specialDays.put("this sun", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this mon", 
						currTime + ParserConstants.THREE_DAYS); 
				specialDays.put("this tue", 
						currTime + ParserConstants.FOUR_DAYS); 
				specialDays.put("this wed", 
						currTime + ParserConstants.FIVE_DAYS); 
				specialDays.put("this thu", 
						currTime + ParserConstants.SIX_DAYS); 
				specialDays.put("this sat",
						currTime + ParserConstants.ONE_DAY);
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY);
				break; 
			case 7: //sat
				specialDays.put("this sun", 
						currTime + ParserConstants.ONE_DAY); 
				specialDays.put("this mon", 
						currTime + ParserConstants.TWO_DAYS); 
				specialDays.put("this tue", 
						currTime + ParserConstants.THREE_DAYS); 
				specialDays.put("this wed", 
						currTime + ParserConstants.FOUR_DAYS); 
				specialDays.put("this thu", 
						currTime + ParserConstants.FIVE_DAYS); 
				specialDays.put("this fri", 
						currTime + ParserConstants.SIX_DAYS); 
				
				specialDays.put("next sun", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
				specialDays.put("next mon", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
				specialDays.put("next tue", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
				specialDays.put("next wed", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
				specialDays.put("next thu", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
				specialDays.put("next fri", 
						currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
				specialDays.put("next sat",
						currTime + ParserConstants.ONE_WEEK);
				break; 
		}
		
	}
	
	public static void main(String[] args) {
		SpecialDaysConverter dc = new SpecialDaysConverter(); 
		dc.processDayOfTheWeek(); 
	}
}
