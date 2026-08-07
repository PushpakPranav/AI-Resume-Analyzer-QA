package constants;

public final class TestData {
	private TestData() {}
	
	public static final String VALID_EMAIL = "testname100@gmail.com";
	public static final String VALID_PASSWORD = "Test@123";
	
	public static final String STRONG_RESUME = System.getProperty("user.dir")
											+ "/src/test/resources/TestData/StrongResume.pdf";
	
	public static final String PERFECT_MATCH_RESUME = System.getProperty("user.dir")
											+ "/src/test/resources/TestData/PerfectMatchResume.pdf";
	
	public static final String JD_File = System.getProperty("user.dir")
			+ "/src/test/resources/TestData/JD.txt";
	
}
