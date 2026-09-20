package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.ForgotPasswordPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;
import constants.TestData;

public class ForgotPasswordTest extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;
	ForgotPasswordPage forgotPasswordPage;

	@BeforeMethod(alwaysRun = true)
	public void init() {
		homePage = new HomePage(driver);
		loginPage = new LoginPage(driver);
		homePage.clickLogin();
		forgotPasswordPage = loginPage.clickForgotPassword();
		forgotPasswordPage.waitForForgotPasswordPage();
	}


	// ---------------------------------------------------------
	// Page content
	// ---------------------------------------------------------

	@Test
	public void verifyForgotPasswordTitleDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isForgotPasswordPageTitleDisplayed(),
				"Forgot Password title should be displayed."
				);
	}

	@Test
	public void verifyForgotPasswordSubtitleDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isForgotPasswordSubtitleDisplayed(),
				"Forgot Password subtitle should be displayed."
				);
	}

	@Test
	public void verifyRememberPasswordPromptDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isRememberPasswordPromptDisplayed(),
				"Remember password prompt should be displayed."
				);
	}

	@Test
	public void verifyRememberPasswordPromptText() {
		String promptText = forgotPasswordPage.getRememberPasswordText();

		Assert.assertNotNull(promptText, "Remember password prompt text should not be null.");
		Assert.assertFalse(promptText.isEmpty(), "Remember password prompt text should not be empty.");
	}


	// ---------------------------------------------------------
	// Email field
	// ---------------------------------------------------------

	@Test
	public void verifyEmailFieldDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isEmailFieldDisplayed(),
				"Email field should be displayed."
				);
	}

	@Test
	public void verifyEnterEmailReflectsInField() {
		
		forgotPasswordPage.enterEmail(TestData.TEST_EMAIL);

		Assert.assertEquals(
				forgotPasswordPage.getEmail(),
				TestData.TEST_EMAIL,
				"Entered email should match the value in the email field."
				);
	}


	// ---------------------------------------------------------
	// Send Reset Link button
	// ---------------------------------------------------------

	@Test
	public void verifySendResetLinkButtonDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isSendResetLinkDisplayed(),
				"Send Reset Link button should be displayed."
				);
	}

	@Test
	public void verifySendResetLinkButtonEnabled() {
		Assert.assertTrue(
				forgotPasswordPage.isSendResetLinkEnabled(),
				"Send Reset Link button should be enabled after entering a valid email."
				);
	}


	// ---------------------------------------------------------
	// Validation
	// ---------------------------------------------------------

	@Test
	public void verifyInvalidEmailShowsValidation() {
		forgotPasswordPage.enterEmail(TestData.INVALID_EMAIL_FOR_FORGOT_PASSWORD);
		forgotPasswordPage.clickSendResetLink();

		Assert.assertFalse(
				forgotPasswordPage.getEmailValidationMessage().isBlank(),
				"Validation message should be displayed for an invalid email."
				);
	}

	@Test
	public void verifyEmptyEmailShowsValidation() {
		forgotPasswordPage.clickSendResetLink();

		Assert.assertFalse(
				forgotPasswordPage.getEmailValidationMessage().isBlank(),
				"Validation message should be displayed for an empty email."
				);
	}


	// ---------------------------------------------------------
	// Successful reset request
	// ---------------------------------------------------------

	@Test
	public void verifyValidEmailShowsSuccessMessage() {
		forgotPasswordPage.enterEmail(TestData.VALID_EMAIL_FOR_FORGOT_PASSWORD);
		forgotPasswordPage.clickSendResetLink();

		Assert.assertTrue(
				forgotPasswordPage.isSuccessMessageDisplayed(),
				"Success message should be displayed after submitting a valid email."
				);

		Assert.assertTrue(
				forgotPasswordPage.getSuccessMessage().length() > 0,
				"Success message text should not be empty."
				);
	}


	// ---------------------------------------------------------
	// Navigation
	// ---------------------------------------------------------

	@Test
	public void verifyLoginLinkDisplayed() {
		Assert.assertTrue(
				forgotPasswordPage.isLoginLinkDisplayed(),
				"Login link should be displayed."
				);
	}

	@Test
	public void verifyClickLoginLinkNavigatesToLoginPage() {
		LoginPage loginPageAfterForgotPassword =
		        forgotPasswordPage.clickLoginLink();

		Assert.assertNotNull(
		        loginPageAfterForgotPassword,
		        "LoginPage instance should be returned after clicking the login link."
		);

		Assert.assertTrue(
				driver.getCurrentUrl().contains("auth/login"),
				"User is not redirected to Login page."
				);
	}
}