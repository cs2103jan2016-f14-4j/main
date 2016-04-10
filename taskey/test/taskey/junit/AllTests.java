package taskey.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	 SystemTest.class, 
	 ParserTest.class,
	 StorageTest.class,
	 TimeConverterTest.class,
	 LoggerTest.class,
	 PatternMatcherTest.class,
	 AutoCompleteTest.class 
})
public class AllTests {
}
