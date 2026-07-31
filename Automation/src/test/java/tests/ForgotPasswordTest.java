package tests;

import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.ForgotPasswordPage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.HomePage;
import com.resumeanalyzer.ResumeAnalyzerAutomation.pages.LoginPage;

import base.BaseTest;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ForgotPasswordTest extends BaseTest {
	LoginPage loginpage;
	HomePage homepage;
	ForgotPasswordPage forgotPasswordPage;

	@BeforeMethod
	public void setUp() {
		homepage = new HomePage(driver);
		loginpage = new LoginPage(driver);
		homepage.clickLogin();
		forgotPasswordPage = loginpage.clickForgotPassword();
	}

	@Test
	public void testForgotPasswordTitleDisplayed() {
		Assert.assertTrue(forgotPasswordPage.isForgotPasswordPageTitleDisplayed(),
				"Forgot Password title should be displayed");
	}

	@Test
	public void testForgotPasswordSubtitleDisplayed() {
		Assert.assertTrue(forgotPasswordPage.isForgotPasswordSubtitleDisplayed(),
				"Forgot Password subtitle should be displayed");
	}

	@Test
	public void testEnterEmailReflectsInField() {
		String testEmail = "testuser@example.com";
		forgotPasswordPage.enterEmail(testEmail);
		Assert.assertEquals(forgotPasswordPage.getEmail(), testEmail,
				"Entered email should match the value in the email field");
	}

	@Test
	public void testSendResetLinkButtonEnabled() {
		forgotPasswordPage.enterEmail("testuser@example.com");
		Assert.assertTrue(forgotPasswordPage.isSendResetLinkEnabled(),
				"Send Reset Link button should be enabled after entering a valid email");
	}

	@Test
	public void testInvalidEmailShowsValidation() {
	    forgotPasswordPage.enterEmail("abc");
	    forgotPasswordPage.clickSendResetLink();

	    Assert.assertFalse(
	        forgotPasswordPage.getEmailValidationMessage().isBlank()
	    );
	}

	@Test
	public void testEmptyEmailShowsValidation() {
	    forgotPasswordPage.clickSendResetLink();

	    Assert.assertFalse(
	        forgotPasswordPage.getEmailValidationMessage().isBlank()
	    );
	}

	@Test
	public void testValidEmailShowsSuccessMessage() {
		forgotPasswordPage.enterEmail("registereduser@example.com");
		forgotPasswordPage.clickSendResetLink();
		Assert.assertTrue(forgotPasswordPage.getSuccessMessage().length() > 0,
				"Success message should be displayed after submitting a valid email");
	}

	@Test
	public void testRememberPasswordPromptDisplayed() {
		Assert.assertTrue(forgotPasswordPage.isRememberPasswordPromptDisplayed(),
				"Remembered password prompt should be displayed");
	}

	@Test
	public void testRememberPasswordPromptText() {
		String promptText = forgotPasswordPage.getRememberPasswordText();
		Assert.assertNotNull(promptText, "Remembered password prompt text should not be null");
		Assert.assertFalse(promptText.isEmpty(), "Remembered password prompt text should not be empty");
	}

	@Test
	public void testClickLoginLinkNavigatesToLoginPage() {
		LoginPage loginPage = forgotPasswordPage.clickLoginText();
		Assert.assertNotNull(loginPage, "LoginPage instance should be returned after clicking login link");
	}
}