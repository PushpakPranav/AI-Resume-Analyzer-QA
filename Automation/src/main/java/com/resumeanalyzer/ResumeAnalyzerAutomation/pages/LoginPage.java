package com.resumeanalyzer.ResumeAnalyzerAutomation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.resumeanalyzer.ResumeAnalyzerAutomation.components.NavbarComponent;


public class LoginPage extends BasePage{
	private final NavbarComponent navbar;

	public LoginPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
		navbar = new NavbarComponent(driver);
		
	}
	
//	  =====================
//	  Elements
//	  =====================
	
	@FindBy(xpath="//input[@name='email']")
	private WebElement emailInput;
	
	@FindBy(xpath="//input[@name='password']")
	private WebElement passwordInput;
	
	@FindBy(xpath="//button[@type='submit']")
	private WebElement loginBtn;
	
	@FindBy(xpath="//a[@class='text-primary fw-semibold']")
	private WebElement signUpLink;
	
	@FindBy(className="fw-bold")
	private WebElement welcomeText;
	
	@FindBy(xpath="//p[@class='text-center text-muted small mt-3 mb-0']")
	private WebElement noAccountText;
	
	@FindBy(xpath="//h4[@class='welcome-text']")
	private WebElement welcomeMessage;
	
	@FindBy(xpath = "//a[normalize-space()='Forgot password?']")
	private WebElement forgotPasswordLink;
	
//	  =====================
//	  Navigation
//	  =====================
	
	public void clickHomeLink() {
		navbar.clickHome();
	}
	
	public void clickSignUpLink() {
	    click(signUpLink);
	}
	
	public ForgotPasswordPage clickForgotPassword() {
		click(forgotPasswordLink);
		return new ForgotPasswordPage(driver);
	}
	
//	  =====================
//	  Actions
//	  =====================
	private void performLogin(String email, String password) {
	    type(emailInput, email);
	    type(passwordInput, password);
	    click(loginBtn);
	}
	
	public DashboardPage loginAsValidUser(String email, String password) {
	    performLogin(email, password);
	    return new DashboardPage(driver);
	}

	public LoginPage loginAsInvalidUser(String email, String password) {
	    performLogin(email, password);
	    return this;
	}
	
//	  =====================
//	  Getters
//	  =====================
	
	public String getWelcomeText() {
		return getText(welcomeText);
	}
	
	public String getNoAccountText(){
	    return getText(noAccountText);
	}

	public String getWelcomeMessage() {
		return getText(welcomeMessage);
	}
	
//	  =====================
//	  Validations
//	  =====================
	
	public boolean isLoginButtonDisplayed() {
	    return isDisplayed(loginBtn);
	}
	
	public boolean isLoginButtonEnabled() {
	    return isEnabled(loginBtn);
	}

	public boolean isForgotPasswordLinkDisplayed() {
	    return isDisplayed(forgotPasswordLink);
	}

	public boolean isEmailFieldDisplayed() {
	    return isDisplayed(emailInput);
	}
	public boolean isPasswordFieldDisplayed() {
	    return isDisplayed(passwordInput);
	}
	public boolean isSignUpLinkDisplayed() {
	    return isDisplayed(signUpLink);
	}

	
}
