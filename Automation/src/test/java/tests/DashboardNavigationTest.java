package tests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HistoryPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class DashboardNavigationTest extends BaseTest {

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
	// Dashboard -> Home
	// ---------------------------------------------------------

	@Test
	public void verifyClickHomeNavigatesToHomePage() {
		dashboardPage.clickHome();

		Assert.assertTrue(
				homePage.isUploadFormDisplayed(),
				"Upload form is not displayed after navigating Home from the dashboard."
				);
	}


	// ---------------------------------------------------------
	// Dashboard -> History
	// ---------------------------------------------------------

	@Test
	public void verifyHistoryButtonDisplayed() {
		Assert.assertTrue(
				dashboardPage.isHistoryButtonDisplayed(),
				"History button is not displayed for a resume with history."
				);
	}

	@Test
	public void verifyClickHistoryBtnNavigatesToHistoryPage() {
		HistoryPage historyPage = dashboardPage.clickFirstHistoryButton();

		historyPage.waitForHistoryPage();

		Assert.assertTrue(
		        driver.getCurrentUrl().contains("/analysis/history"),
		        "Navigation to History page failed."
		);

		Assert.assertTrue(
		        historyPage.isHistoryHeadingDisplayed(),
		        "History page is not loaded properly."
		);
	}
}