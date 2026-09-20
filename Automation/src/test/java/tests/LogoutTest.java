package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LogoutPage;

import base.BaseTest;
import constants.TestData;

public class LogoutTest extends BaseTest {

	HomePage homePage;
	LoginPage loginPage;
	LogoutPage logoutPage;

	@BeforeMethod(alwaysRun = true)
	public void init() {
		homePage = new HomePage(driver);
		loginPage = new LoginPage(driver);
		logoutPage = new LogoutPage(driver);

		homePage.clickLogin();
		loginPage.loginAsValidUser(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
		loginPage.waitForDashboard();
	}


	// ---------------------------------------------------------
	// Logout behavior
	// ---------------------------------------------------------

	@Test
	public void verifyUserCanLogout() {
		logoutPage.logout();

		Assert.assertTrue(
				logoutPage.isLoginDisplayed(),
				"Login button should be visible after logout."
				);

		Assert.assertTrue(
				logoutPage.isSignUpDisplayed(),
				"Sign Up button should be visible after logout."
				);
	}

	@Test
	public void verifyIsLoggedOutHelperReturnsTrueAfterLogout() {
		logoutPage.logout();

		Assert.assertTrue(
				logoutPage.isLoggedOut(),
				"User should be reported as logged out after clicking Logout."
				);
	}

	@Test
	public void verifyLogoutRedirectsToHomePage() {
		logoutPage.logout();

		Assert.assertTrue(
				driver.getCurrentUrl().endsWith("/"),
				"User is not redirected to Home page after logout."
				);
	}


	// ---------------------------------------------------------
	// Session invalidation after logout
	// ---------------------------------------------------------

	@Test
	public void verifyDashboardNotAccessibleAfterLogout() {
		logoutPage.logout();

		driver.get(config.getProperty("url") + "/dashboard");

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"Unauthenticated user should be redirected to Login."
				);
	}

	@Test
	public void verifyDashboardNotAccessibleUsingBrowserBack() {
		logoutPage.logout();

		driver.navigate().back();

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/auth/login"),
				"User should be redirected to Login page after pressing browser back."
				);
	}

	@Test
	public void verifyRefreshAfterLogout() {
		logoutPage.logout();

		driver.navigate().refresh();

		Assert.assertTrue(
				driver.getCurrentUrl().contains("/"),
				"User should remain logged out after refresh."
				);
	}
}