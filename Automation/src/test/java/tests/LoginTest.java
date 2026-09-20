package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.DashboardPage;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.RegisterPage;

import base.BaseTest;
import constants.TestData;

public class LoginTest extends BaseTest {

	RegisterPage registerPage;
	LoginPage loginPage;
	HomePage homePage;
	DashboardPage dashboardPage;

	@BeforeMethod(alwaysRun = true)
	public void init() {
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		dashboardPage = new DashboardPage(driver);
		homePage.clickLogin();
	}
	
	// ---------------------------------------------------------
	// Login Form
	// ---------------------------------------------------------

	@Test
	public void verifyEmailFieldDisplayed() {
		Assert.assertTrue(
				loginPage.isEmailFieldDisplayed(),
				"Email field is not displayed on Login page."
				);
	}

	@Test
	public void verifyPasswordFieldDisplayed() {
		Assert.assertTrue(
				loginPage.isPasswordFieldDisplayed(),
				"Password field is not displayed on Login page."
				);
	}

	@Test
	public void verifyLoginButtonDisplayed() {
		Assert.assertTrue(
				loginPage.isLoginButtonDisplayed(),
				"Login button is not displayed on Login page."
				);
	}

	@Test
	public void verifyLoginButtonEnabledByDefault() {
		Assert.assertTrue(
				loginPage.isLoginButtonEnabled(),
				"Login button is not enabled by default."
				);
	}

	@Test
	public void verifyWelcomeTextDisplayedOnLoad() {
		Assert.assertNotNull(
				loginPage.getWelcomeText(),
				"Welcome text is not displayed on Login page."
				);
	}

	@Test
	public void verifyNoAccountTextDisplayed() {
		Assert.assertTrue(
				loginPage.getNoAccountText().toLowerCase().contains("account"),
				"No account text is not displayed correctly."
				);
	}


	// ---------------------------------------------------------
	// Navigation
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifySignUpLinkNavigatesToRegisterPage() {
		loginPage.clickSignUpLink();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/register"),
				"User is not redirected to Register page."
				);
	}

	@Test
	public void verifyForgotPasswordLinkDisplayed() {
		Assert.assertTrue(
				loginPage.isForgotPasswordLinkDisplayed(),
				"Forgot Password link is not displayed on Login page."
				);
	}

	@Test(groups = {"smoke"})
	public void verifyForgotPasswordLinkNavigation() {
		loginPage.clickForgotPassword();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("forgot-password"),
				"User is not redirected to Forgot Password page."
				);
	}


	// ---------------------------------------------------------
	// Valid Login
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyValidLogin() {
		loginPage.loginAsValidUser(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
		loginPage.waitForDashboard();
		Assert.assertEquals(
				dashboardPage.getWelcomeMessage(),
				TestData.EXPECTED_LOGIN_WELCOME_MESSAGE,
				"Welcome message is incorrect after successful login."
				);
	}
	
	@Test
	public void verifyLoginSuccessWithLeadingSpaceInEmail() {
		loginPage.loginWithLeadingSpaceInEmail(TestData.TRIM_LEADING_SPACE_EMAIL,TestData.VALID_PASSWORD);
		loginPage.waitForDashboard();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("dashboard"),
				"Login Failed and User is kept on Login page."
				);
	}
	
	@Test
	public void verifyLoginSuccessWithTrailingSpaceInEmail() {
		
		loginPage.loginWithTrailingSpaceInEmail(TestData.TRIM_TRAILING_SPACE_EMAIL,TestData.VALID_PASSWORD);
		loginPage.waitForDashboard();
		Assert.assertTrue(
				driver.getCurrentUrl().contains("dashboard"),
				"Login Failed and User is kept on Login page."
				);
	}


	// ---------------------------------------------------------
	// Invalid Login
	// ---------------------------------------------------------

	@Test(groups = {"smoke"})
	public void verifyInvalidPasswordShowsError() {
		loginPage.loginAsInvalidUser(TestData.VALID_EMAIL, "WrongPass@123");
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not kept on Login page after an invalid password."
				);
	}

	@Test
	public void verifyNonExistentEmailShowsError() {
		loginPage.loginAsInvalidUser(
				"doesnotexist" + System.currentTimeMillis() + "@gmail.com",
				TestData.VALID_PASSWORD
				);
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not kept on Login page after a non-existent email."
				);
	}

	@Test
	public void verifyEmptyEmailFieldValidation() {
		loginPage.loginAsInvalidUser("", TestData.VALID_PASSWORD);
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not kept on Login page with an empty email."
				);
	}

	@Test
	public void verifyEmptyPasswordFieldValidation() {
		loginPage.loginAsInvalidUser(TestData.VALID_EMAIL, "");
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not kept on Login page with an empty password."
				);
	}

	@Test
	public void verifyBothFieldsEmptyValidation() {
		loginPage.loginAsInvalidUser("", "");
		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not kept on Login page when both fields are empty."
				);
	}
}