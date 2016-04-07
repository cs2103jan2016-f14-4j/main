package taskey.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	 LogicTest.class, 
	 ParserTest.class,
	 StorageTest.class,
	 TimeConverterTest.class,
	 LoggerTest.class,
	 IntegrationTest.class,
	 PatternMatcherTest.class,
	 AutoCompleteTest.class 
})
public class AllTests {
}
