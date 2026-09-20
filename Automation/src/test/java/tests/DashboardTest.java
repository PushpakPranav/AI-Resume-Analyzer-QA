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

public class DashboardTest extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;
	DashboardPage dashboardPage;
	NavbarComponent navbar;
	
	private void ensureResumeHistory() {

	    if (dashboardPage.hasResumeHistory()) {
	        return;
	    }

	    navbar.clickHome();
	    homePage.clickUploadForm();
	    homePage.selectFile(TestData.DASHBOARD_RESUME_PATH);
	    homePage.clickAnalyzeBtn();
	    homePage.waitForAtsResultPage();
	    navbar.clickDashboard();
	}

	@BeforeMethod(alwaysRun = true)
	public void init() {

	    homePage = new HomePage(driver);
	    loginPage = new LoginPage(driver);
	    dashboardPage = new DashboardPage(driver);
	    navbar = new NavbarComponent(driver);

	    homePage.clickLogin();

	    loginPage.loginAsValidUser(
	            TestData.DASHBOARD_USER_EMAIL,
	            TestData.DASHBOARD_USER_PASSWORD
	    );
	    
	    loginPage.waitForDashboard();

	    ensureResumeHistory();
	}


	// ---------------------------------------------------------
	// Page load / URL
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyDashboardUrl() {
		Assert.assertTrue(
				driver.getCurrentUrl().contains("/dashboard"),
				"Dashboard URL is incorrect."
				);
	}

	@Test
	public void verifyDashboardAfterRefresh() {
		driver.navigate().refresh();

		Assert.assertTrue(
				dashboardPage.isUsernameDisplayed(),
				"Dashboard should remain accessible after refresh."
				);
	}


	// ---------------------------------------------------------
	// Logged-in user state
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyWelcomeMessageDisplayedAfterLogin() {
		Assert.assertTrue(
				dashboardPage.getWelcomeMessage().contains(TestData.EXPECTED_DASHBOARD_WELCOME_MESSAGE),
				"Welcome message is not displayed correctly."
				);
	}

	@Test
	public void verifyUsernameDisplayedOnDashboard() {
		Assert.assertTrue(
				dashboardPage.isUsernameDisplayed(),
				"Welcome/username text is not displayed on the dashboard."
				);
	}

	@Test
	public void verifyUserAvatarDisplayedOnDashboard() {
		Assert.assertTrue(
				dashboardPage.isUserAvatarDisplayed(),
				"User avatar is not displayed on the dashboard after login."
				);
	}

	@Test
	public void verifyLoginOptionNotDisplayedOnDashboard() {
		Assert.assertFalse(
				dashboardPage.isLoginDisplayed(),
				"Login option should not be displayed once the user is logged in."
				);
	}

	@Test
	public void verifySignUpOptionNotDisplayedOnDashboard() {
		Assert.assertFalse(
				dashboardPage.isSignUpDisplayed(),
				"Sign Up option should not be displayed once the user is logged in."
				);
	}


	// ---------------------------------------------------------
	// Dashboard layout
	// ---------------------------------------------------------

	@Test
	public void verifyUploadNewResumeBtnDisplayed() {
		Assert.assertTrue(
				dashboardPage.isUploadBtnDisplayed(),
				"'Upload New Resume' button is not displayed on the dashboard."
				);
	}

	@Test(groups = {"smoke"})
	public void verifyResumeHistoryHeadingDisplayed() {
		Assert.assertTrue(
				dashboardPage.isResumeHistoryHeadingDisplayed(),
				"'Resume History' heading is not displayed on the dashboard."
				);
	}

	@Test
	public void verifyDomainsDetectedCardDisplayed() {
		Assert.assertTrue(
				dashboardPage.isDomainsDetectedCardDisplayed(),
				"'Domains Detected' card is not displayed on the dashboard."
				);
	}

	@Test
	public void verifyScoreHistoryChartDisplayed() {
		Assert.assertTrue(
				dashboardPage.isScoreHistoryDisplayed(),
				"Score History chart is not displayed on the dashboard."
				);
	}
}