package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;

import base.BaseTest;
import constants.TestData;

public class UnauthorizedAccessTest extends BaseTest {

	HomePage homePage;
	LoginPage loginPage;
	LogoutPage logoutPage;

	@BeforeMethod(alwaysRun = true)
	public void init() {
		homePage = new HomePage(driver);
		loginPage = new LoginPage(driver);
		logoutPage = new LogoutPage(driver);
	}


	// ---------------------------------------------------------
	// Protected routes without a session
	// ---------------------------------------------------------

	@Test
	public void verifyDashboardRequiresLogin() {
		driver.get(config.getProperty("url") + "/dashboard");

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"Unauthenticated user should be redirected to Login when accessing Dashboard directly."
				);
	}

	@Test
	public void verifyHistoryPageRequiresLogin() {
		driver.get(config.getProperty("url") + "/analysis/history/1");

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"Unauthenticated user should be redirected to Login when accessing History directly."
				);
	}


	// ---------------------------------------------------------
	// Protected routes after logout
	// ---------------------------------------------------------

	@Test
	public void verifyDashboardAfterLogout() {
		homePage.clickLogin();
		loginPage.loginAsValidUser(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

		logoutPage.logout();

		driver.get(config.getProperty("url") + "/dashboard");

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"Dashboard should not be accessible after logout."
				);
	}


	// ---------------------------------------------------------
	// Public routes
	// ---------------------------------------------------------

	@Test
	public void verifyDirectLoginPageAccessibleWithoutAuthentication() {
		driver.get(config.getProperty("url") + "/auth/login");

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"Unauthenticated user should be able to access the Login page directly."
				);
	}
}