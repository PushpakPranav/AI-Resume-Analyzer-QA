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
	
	//Elements
	@FindBy(id="forgot-password-title") WebElement forgotPasswordTitle;
	@FindBy(id="forgot-password-subtitle") WebElement forgotPasswordPageSubtitle;
	@FindBy(id="forgot-password-email") WebElement forgotPasswordEmail;
	@FindBy(id="forgot-password-submit") WebElement sendResetLink;
	@FindBy(id="forgot-password-login-prompt") WebElement rememberedPasswordText;
	@FindBy(id="forgot-password-login-link") WebElement loginLinkText;
	@FindBy(id="forgot-password-success-message") WebElement successMessage;
	@FindBy(id="forgot-password-email-error")
	WebElement emailError;
	
	
	public boolean isForgotPasswordPageTitleDisplayed() {
		return isDisplayed(forgotPasswordTitle);
	}
	
	public boolean isForgotPasswordSubtitleDisplayed() {
		return isDisplayed(forgotPasswordPageSubtitle);
	}
	public void enterEmail(String email) {
		type(forgotPasswordEmail,email);
	}
	public boolean isSendResetLinkEnabled() {
	    return sendResetLink.isEnabled();
	}
	public void clickSendResetLink() {
		click(sendResetLink);
	}
	public boolean isRememberPasswordPromptDisplayed() {
	    return isDisplayed(rememberedPasswordText);
	}
	public String getRememberPasswordText() {
	    return getText(rememberedPasswordText);
	}
	public LoginPage clickLoginText() {
	    click(loginLinkText);
	    return new LoginPage(driver);
	}
	public String getSuccessMessage() {
		return getText(successMessage);
	}
	public String getEmail() {
	    return forgotPasswordEmail.getAttribute("value");
	}
	public String getEmailValidationMessage() {
	    return forgotPasswordEmail.getAttribute("validationMessage");
	}
}
