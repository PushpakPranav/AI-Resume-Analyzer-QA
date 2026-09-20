package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;


import base.BaseTest;
import constants.TestData;


public class HomeTest extends BaseTest {
	private static final String PAGE_TITLE = "Home — Resume ATS Analyzer";

	HomePage homepage;
	NavbarComponent navbar;
	LoginPage loginPage;

	private DashboardPage loginAsValidUser() {

		homepage.clickLogin();

		return new LoginPage(driver)
				.loginAsValidUser(
						TestData.VALID_EMAIL,
						TestData.VALID_PASSWORD);
	}
	
	private void performLogin() {

		homepage.clickLogin();
		homepage.loginAndGoHome(
				TestData.VALID_EMAIL,
				TestData.VALID_PASSWORD);
		
	}

	@BeforeMethod(alwaysRun= true)
	public void init() {
		homepage = new HomePage(driver);
		navbar = new NavbarComponent(driver);
		loginPage = new LoginPage(driver);
	}


	// ---------------------------------------------------------
	// Header / Branding
	// ---------------------------------------------------------


	@Test
	public void verifyHomeNavigationLinkDisplayed() {
		Assert.assertTrue(
				navbar.isHomeDisplayed(),
				"Home navigation link is not displayed in navbar."
				);
	}


	// ---------------------------------------------------------
	// Page metadata
	// ---------------------------------------------------------

	@Test
	public void verifyPageTitle() {
		Assert.assertEquals(
				driver.getTitle(),
				PAGE_TITLE,
				"Home page title is incorrect."
				);
	}

	@Test
	public void verifyHomeURL() {
		Assert.assertTrue(
			    driver.getCurrentUrl().endsWith("/")
			);
	}


	// ---------------------------------------------------------
	// Navbar state tests (logged out) - Login/Signup navigation
	// ---------------------------------------------------------

	@Test
	public void verifyLoginAndSignupDisplayedWhenUserNotLoggedIn() {

		Assert.assertTrue(
				navbar.isLoginDisplayed(),
				"Login option is not displayed."
		);

		Assert.assertTrue(
				navbar.isSignUpDisplayed(),
				"Sign Up option is not displayed."
		);
	}

	@Test(groups = {"smoke"})
	public void verifyLoginNavigation() {

		homepage.clickLogin();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("login"),
				"User is not redirected to Login page."
			);
	}

	@Test(groups = {"smoke"})
	public void verifySignupNavigation() {

		homepage.clickSignUp();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("register"),
				"User is not redirected to Sign Up page."
				);
	}


	// ---------------------------------------------------------
	// Navbar state tests (logged in) - Avatar / Dashboard / Logout
	// ---------------------------------------------------------

	@Test
	public void verifyAfterLoginSignUpLoginOptionsAreNotDisplayed() {
		performLogin();
		Assert.assertFalse(
				navbar.isLoginDisplayed(),
				"Login option is still displayed after user has logged in."
				);
		Assert.assertFalse(
				navbar.isSignUpDisplayed(),
				"Sign Up option is still displayed after user has logged in."
				);
	}

	@Test
	public void verifyUserAvatarDisplayedAfterLogin() {
		loginAsValidUser();
		Assert.assertTrue(
				navbar.isUserAvatarDisplayed(),
				"User avatar is not displayed after login"
				);
	}

	@Test
	public void verifyUsernameDisplayedAfterLogin() {
		DashboardPage dashboard = loginAsValidUser();
		Assert.assertTrue(
				dashboard.isUsernameDisplayed(),
				"Username is not displayed after Login"
				);
	}

	@Test(groups = {"smoke"})
	public void verifyDashboardDisplayedAfterLogin() {
		loginAsValidUser();
		Assert.assertTrue(
				homepage.isDashboardDisplayed(),
				"dashboard is not displayed after login"
				);
	}

	@Test
	public void verifyLogoutOptionDisplayed() {
		performLogin();
		navbar.clickAvatar();
		Assert.assertTrue(
				navbar.isLogoutDisplayed(),
				"Logout option is not displayed after clicking on user avatar"
				);
	}

	@Test(groups = {"smoke"})
	public void verifyLogoutRedirectsToLoginSignup() {
		performLogin();
		navbar.clickAvatar();
		navbar.clickLogout();

		Assert.assertTrue(
				navbar.isLoginDisplayed(),
				"Login option is not displayed after logout."
				);
		Assert.assertTrue(
				navbar.isSignUpDisplayed(),
				"Sign Up option is not displayed after logout."
				);
	}


	// ---------------------------------------------------------
	// Home <-> Dashboard navigation
	// ---------------------------------------------------------

	@Test
	public void verifyHomeNavigationFromDashboard() {
		performLogin();
		Assert.assertTrue(
				homepage.isUploadFormDisplayed(),
				"Resume Upload Form is not displayed after navigating back Home from Dashboard."
				);
	}


	// ---------------------------------------------------------
	// Resume Upload Form tests
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyUploadFormDisplayed() {
		Assert.assertTrue(
				homepage.isUploadFormDisplayed(),
				"Resume Upload Form is not displayed."
				);
	}

	@Test
	public void verifyUploadInstructionText() {
		Assert.assertEquals(
				homepage.getUploadInstructionText(),
				"Drag & drop or click to browse",
				"Upload instruction text is not displayed correctly."
				);
	}

	@Test
	public void verifySupportedFileTypeText() {
		Assert.assertEquals(
				homepage.getSupportedFileTypeText(),
				"Supports PDF and DOCX",
				"Supported file type text is not displayed correctly."
				);
	}

	@Test
	public void verifySelectedFileNameDisplayed() {
		homepage.selectFile(TestData.TEST_RESUME1);

		Assert.assertEquals(
				homepage.getSelectedFileName(),
				"TestResume1.pdf",
				"Selected file name is not displayed correctly."
				);
	}

	@Test
	public void verifySelectedFileReplaced() {
		homepage.selectFile(TestData.TEST_RESUME1);
		String firstFile = homepage.getSelectedFileName();

		homepage.selectFile(TestData.TEST_RESUME2);
		String secondFile = homepage.getSelectedFileName();

		Assert.assertNotEquals(
				firstFile,
				secondFile,
				"Previously selected file was not replaced."
				);

		Assert.assertEquals(
				secondFile,
				"TestResume2.pdf",
				"Displayed file name is incorrect."
				);
	}


	// ---------------------------------------------------------
	// Analyze Button tests
	// ---------------------------------------------------------

	@Test
	public void verifyAnalyzeButtonDisplayed() {
		Assert.assertTrue(
				homepage.isAnalyzeBtnDisplayed(),
				"Analyze Button is not displayed"
				);
	}

	@Test
	public void verifyAnalyzeButtonDisabled() {
		Assert.assertFalse(
				homepage.isAnalyzeBtnEnabled(),
				"Analyze button is Enabled"
				);
	}

	@Test(groups = {"smoke"})
	public void verifyAnalyzeButtonEnabledAfterFileSelection() {
		homepage.selectFile(TestData.TEST_RESUME1);
		Assert.assertTrue(
				homepage.isAnalyzeBtnEnabled(),
				"Analyze button did not become enabled after selecting a valid resume."
				);
	}

	@Test(groups = {"smoke"})
	public void verifyAnalyzeButtonNavigatesToResultsAfterFileSelection() {
		homepage.selectFile(TestData.TEST_RESUME1);
		homepage.clickAnalyzeBtn();
		Assert.assertTrue(
				homepage.waitForAtsResultPage(),
	            "User is not navigated to ATS Result page after clicking Analyze."
				);
	}


	// ---------------------------------------------------------
	// How It Works section tests
	// ---------------------------------------------------------

	@Test
	public void verifyHowItWorksSectionDisplayed() {

		Assert.assertTrue(
				homepage.isHowItWorksSectionDisplayed(),
				"How It Works section is not displayed."
				);
	}

	@Test
	public void verifyAllStepsDisplayed() {
		Assert.assertTrue(
				homepage.areAllFourStepsDisplayed(),
				"All four steps are not displayed in How It Works Section"
				);
	}

}