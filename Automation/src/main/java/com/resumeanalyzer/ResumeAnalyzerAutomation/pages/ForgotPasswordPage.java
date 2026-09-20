package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ForgotPasswordPage extends BasePage{

	public ForgotPasswordPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	// =====================
	// Elements
	// =====================
	
	@FindBy(id="forgot-password-title")
	private  WebElement forgotPasswordTitle;

	@FindBy(id="forgot-password-subtitle")
	private  WebElement forgotPasswordPageSubtitle;

	@FindBy(id="forgot-password-email")
	private  WebElement forgotPasswordEmail;

	@FindBy(id="forgot-password-submit")
	private  WebElement sendResetLink;

	@FindBy(id="forgot-password-login-prompt")
	private  WebElement rememberedPasswordText;

	@FindBy(id="forgot-password-login-link")
	private  WebElement loginLinkText;

	@FindBy(id="forgot-password-success-message")
	private  WebElement successMessage;

	@FindBy(id="forgot-password-email-error")
	private WebElement emailError;
	
	@FindBy(id="forgot-password-reset-link")
	private WebElement devModeResetLink;
	
	// =====================
	// Navigation
	// =====================
	
	public LoginPage clickLoginLink() {
	    click(loginLinkText);
	    return new LoginPage(driver);
	}
	
	public ResetPasswordPage clickDevModeResetLink() {
	    click(devModeResetLink);
	    return new ResetPasswordPage(driver);
	}
	// =====================
	// Actions
	// =====================
	
	public void enterEmail(String email) {
		type(forgotPasswordEmail,email);
	}
	
	public void clickSendResetLink() {
		click(sendResetLink);
	}
	
	private void submitResetRequest(String email) {
	    enterEmail(email);
	    clickSendResetLink();
	}
	
	public ForgotPasswordPage requestPasswordReset(String email) {
	    submitResetRequest(email);
	    return this;
	}
	
	// =====================
	// Getters
	// =====================
	
	public String getRememberPasswordText() {
	    return getText(rememberedPasswordText);
	}
	
	public String getSuccessMessage() {
		return getText(successMessage);
	}
	
	public String getEmail() {
	    return getAttribute(forgotPasswordEmail, "value");
	}
	
	public String getEmailValidationMessage() {
		return getAttribute(forgotPasswordEmail,"validationMessage");
	}
	
	public String getEmailError() {
	    return getText(emailError);
	}
	
	// =====================
	// Validations
	// =====================
	
	public boolean isPageLoaded() {
	    return isDisplayed(forgotPasswordTitle)
	            && isDisplayed(forgotPasswordEmail)
	            && isDisplayed(sendResetLink);
	}
	
	public boolean isEmailFieldDisplayed() {
	    return isDisplayed(forgotPasswordEmail);
	}
	
	public boolean isForgotPasswordPageTitleDisplayed() {
		return isDisplayed(forgotPasswordTitle);
	}
	
	public boolean isForgotPasswordSubtitleDisplayed() {
		return isDisplayed(forgotPasswordPageSubtitle);
	}
	
	public boolean isSendResetLinkDisplayed() {
	    return isDisplayed(sendResetLink);
	}
	
	public boolean isSendResetLinkEnabled() {
		return isEnabled(sendResetLink);
	}
	
	public boolean isRememberPasswordPromptDisplayed() {
	    return isDisplayed(rememberedPasswordText);
	}
	
	public boolean isLoginLinkDisplayed() {
	    return isDisplayed(loginLinkText);
	}
	
	public boolean isSuccessMessageDisplayed() {
		waitForVisibility(successMessage);
	    return isDisplayed(successMessage);
	}
	
	public boolean isEmailErrorDisplayed() {
	    return isDisplayed(emailError);
	}

	public void waitForForgotPasswordPage() {
		waitForVisibility(forgotPasswordTitle);
		
	}
}
