package constants;

import java.util.Set;

public final class TestData {
	private TestData() {}
	
	//HomeTest //LoginTest
	
	public static final String VALID_EMAIL = "testname100@gmail.com";
	public static final String VALID_PASSWORD = "Test@123";
	public static final String EXPECTED_LOGIN_WELCOME_MESSAGE = "Welcome, testname100!";
	public static final String TRIM_LEADING_SPACE_EMAIL = "testwithleadingspace@gmail.com";
	public static final String TRIM_TRAILING_SPACE_EMAIL = "testwithtrailingspace@gmail.com";
	
	public static final String TEST_RESUME1 = System.getProperty("user.dir")
											+ "/src/test/resources/TestData/TestResume1.pdf";
	
	public static final String TEST_RESUME2 = System.getProperty("user.dir")
											+ "/src/test/resources/TestData/TestResume2.pdf";
	
	public static final String JD_File = System.getProperty("user.dir")
			+ "/src/test/resources/TestData/TestJD.txt";
	
	
	//RegisterTest
	
	public static final String VALID_NAME = "Test User";
//	public static final String VALID_PASSWORD = "Test@123";

	public static final String EMPTY_NAME_EMAIL = "emptyname@gmail.com";
	public static final String EMPTY_EMAIL_NAME = "Empty Email User";
	public static final String EMPTY_PASSWORD_NAME = "Empty Password User";
	public static final String EMPTY_PASSWORD_EMAIL = "emptypass@gmail.com";

	public static final String INVALID_EMAIL = "not-an-email";
	public static final String INVALID_EMAIL_NAME = "Invalid Email User";

	public static final String WEAK_PASSWORD = "123";
	public static final String WEAK_PASSWORD_EMAIL = "weakpass@gmail.com";
	public static final String WEAK_PASSWORD_NAME = "Weak Pass User";

	public static final String PASSWORD_NO_UPPERCASE = "test@123";
	public static final String PASSWORD_NO_LOWERCASE = "TEST@123";
	public static final String PASSWORD_NO_NUMBER = "Test@Pass";
	public static final String PASSWORD_NO_SPECIAL_CHAR = "Test1234";
	public static final String PASSWORD_TOO_SHORT = "Te@1";
	public static final String PASSWORD_EXACTLY_EIGHT_CHARS = "Test@123";
	
	public static final String PASSWORD_RULE_NAME = "Rule User";

	public static final String FIRST_USER_NAME = "First User";
	public static final String SECOND_USER_NAME = "Second User";

	public static final String EXPECTED_WELCOME_MESSAGE = "Welcome, Test User!";
	
	
	public static final String EMAIL_MISSING_AT = "usergmail.com";
	public static final String EMAIL_MISSING_USERNAME = "@gmail.com"; 
	public static final String EMAIL_MISSING_DOMAIN = "user@";
	public static final String EMAIL_MISSING_TLD = "user@gmail";
	public static final String EMAIL_MISSING_DOMAIN_NAME = "user@.com";
	public static final String EMAIL_WITH_SPACES = "user name@gmail.com";
	public static final String EMAIL_MULTIPLE_AT = "user@@gmail.com";
	
	public static final String EMAIL_VALIDATION_NAME = "Email Test User";
	
	public static final String TEST_EMAIL = "testuser@example.com";

	// Dashboard / History / AtsResult / JdMatchResult tests
	// (seeded account that already has resume history)

	public static final String DASHBOARD_USER_EMAIL = "test11@gmail.com";
	public static final String DASHBOARD_USER_PASSWORD = "MyStr0ng@Pass!";
	public static final String EXPECTED_DASHBOARD_WELCOME_MESSAGE = "Welcome, ";

	public static final String DASHBOARD_RESUME_PATH = System.getProperty("user.dir")
											+ "/src/test/resources/TestData/TestResume1.pdf";
	public static final String DASHBOARD_RESUME_NAME = "TestResume1.pdf";

	public static final String EXPECTED_DOMAIN = "Software Testing/QA";
	public static final String VALID_EMAIL_FOR_FORGOT_PASSWORD = "test12@gmail.com";
	
	public static final String INVALID_EMAIL_FOR_FORGOT_PASSWORD = "abc";
	
	public static final long MIN_PDF_SIZE = 100;
	
	public static final String ATS_REPORT_FILE_NAME_PATTERN =
	        "ATS_Report_\\d+( \\(\\d+\\))?\\.pdf";
	
	public static final String JD_TEXTAREA_PLACEHOLDER = "Paste the full job description here...";
	
	public static final Set<String> VALID_ATS_GRADES =
	        Set.of("Excellent", "Good", "Average", "Poor");

	public static final String JD_RESUME_PATH = System.getProperty("user.dir")
			+ "/src/test/resources/TestData/Software_Testing_Resume.docx";
	public static final String JD_RESUME_NAME = "Software_Testing_Resume.docx";
	public static final String EXPECTED_DOMAIN_IN_JDPAGE = "Software Testing/QA";
	public static final String DASHBOARD_DELETE_USER_EMAIL = "testuser124@gmail.com";
	public static final String DASHBOARD_DELETE_USER_PASSWORD = "Test@9870";
	
	public static final int MAX_LOGIN_ATTEMPTS = 5;
	public static final String LOCKOUT_ERROR_MESSAGE = "Too many attempts. Please try again later.";
	public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";
	
	public static final String CSRF_SESSION_EXPIRED_MESSAGE = "Your session expired. Please refresh and try again.";
	
	public static final String VALID_AVATAR_IMAGE = System.getProperty("user.dir")
	        + "/src/test/resources/TestData/TestAvatar.jpg";

	public static final String OVERSIZED_AVATAR_IMAGE = System.getProperty("user.dir")
	        + "/src/test/resources/TestData/LargeAvatar.jpg";

	public static final String AVATAR_SIZE_ERROR_MESSAGE = "Avatar must be under 2 MB";
	public static final String AVATAR_TYPE_ERROR_MESSAGE = "Only JPG, PNG, WEBP allowed";
	
	public static final String NEW_PASSWORD_AFTER_RESET = "NewPass@456";
	
	public static final String EMPTY_FILE_RESUME = System.getProperty("user.dir")
	        + "/src/test/resources/TestData/EmptyFile.pdf";

	public static final String OVERSIZED_RESUME = System.getProperty("user.dir")
	        + "/src/test/resources/TestData/OversizedResume.pdf";

	public static final String UPLOAD_WRONG_TYPE_ERROR = "Only PDF and DOCX files are allowed";
	public static final String UPLOAD_EMPTY_FILE_ERROR = "File is empty.";
	public static final String UPLOAD_OVERSIZED_ERROR = "File size must not exceed 5 MB.";
	public static final String JD_WHITESPACE_ERROR = "Job description cannot be empty or contain only spaces.";
	
}